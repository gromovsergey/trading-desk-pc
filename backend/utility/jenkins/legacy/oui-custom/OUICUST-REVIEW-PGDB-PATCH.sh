#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.jira.sh
. $WORKING_DIR/commons.ui.build.sh

JIRA_ISSUE=${bamboo__1_JiraIssue}
DO_JAVA_UNIT_TESTS=${bamboo__2_DoJavaUnitTests}
LDAP_USER="oix.project.coordinator"
LDAP_PASSWORD=`cat ~/.bamboo | awk '{ if (NR == 2) print $0 }'`

echo ": Getting $JIRA_ISSUE fix versions"
data="`doc jira --action getIssue --issue "$JIRA_ISSUE"`"
[ "$?" != "0" ] && { echo ": $data" ; exit 1 ; }

fvs=`echo "$data" | grep 'Fix versions  . . . . . . . . :'`
fvs=${fvs:31}
echo ": Found next: $fvs"

versions=
for ver in $fvs ; do
  if (( n % 2 == 0 )) ; then
    ver=${ver#\'}
    ver=${ver%.*} # removing a tag
    ver=${ver%.*} # removing a branch
    if ! echo "$versions" | grep -q "$ver " ; then
      versions="$versions "$ver 
    fi
  fi
  (( n+= 1 ))  
done
[ -z "$versions" ] && { echo ": Empty fix version" ; exit 1 ; }
echo "Found versions: $versions"

doc jira --action getAttachmentList --outputFormat 999 --issue "$JIRA_ISSUE" --file list.csv
echo ": Found next attachments:"
files=`tail -n+2 list.csv | cut -d , -f 3 | sort`
echo "$files"
[ -z "$files" ] && { echo ": No attachments found" ; exit 1 ; }

echo
jira_comment=
for ver in $versions ; do
  started_time=$(date +%s.%N)
  
  echo "Looking for $ver patch"
  patch=`echo "$files" | grep "${ver}.patch"`
  [ -z "$patch" ] && patch="$files"
  echo "Found: $patch"

  # Removing "" as /usr/bin/patch can't work with them
  # Waiting for "svn patch" in svn 1.7
  patch=${patch:1}
  patch=${patch%\"}
  
  get_latests_branch 1 "svn+ssh://svn/home/svnroot/oix/pgdb/branches"
  trunk_branch=`get_next_branch $LATESTS_BRANCH` \
    || { echo ": Can't get the next branch for $LATESTS_BRANCH" ; exit 1 ; }
  echo ": trunk is $trunk_branch"
  branch_name=${ver}.0
  if [ "$branch_name" = "$trunk_branch" ] ; then
    branch="trunk"
    branch_name="trunk"
  else
    doc get_svn_path "$branch_name"
    branch=$SVN_PATH
  fi

  echo
  echo ": ========================================================= "
  echo ":  Start review $patch for PGDB $branch "
  echo ": ========================================================= "
  doc jira --action getAttachment --issue "$JIRA_ISSUE" --name $patch --file $WORKING_DIR/$patch
  
  doc svn_checkout_folder "svn+ssh://svn/home/svnroot/oix/pgdb/$branch" "$branch"
  
  echo ": patch -p0 -E -d $CHECKOUT_FOLDER < $WORKING_DIR/$patch"
  /usr/bin/patch -p0 -E -d $CHECKOUT_FOLDER < $WORKING_DIR/$patch 2>&1
  [ "$?" != "0" ] && { echo ": Error" ; exit 1 ; }
  
  echo ": List changes"
  svn status $CHECKOUT_FOLDER > $WORKING_DIR/status.txt
  cat $WORKING_DIR/status.txt

  echo ": Adding new files" # remove with "svn patch"
  while read line ; do 
    if [ "${line:0:1}" = "?" ] ; then 
      doc svn add ${line:1}
    fi 
  done < $WORKING_DIR/status.txt
  
  echo ": Copy an empty database to stat-dev0/review_pgdb_patch"
  doc fast_copy_statdb "stat-dev0" "review_pgdb_patch" "adserver_empty"
  
  echo ": Patching it"
  doc patch_postgres "review_pgdb_patch" "$branch_name" "S"
  doc patch_database_using_working_copy "stat-dev0" "5432" "review_pgdb_patch" "oix" $branch $WORKING_DIR/svn
  
  echo ": Executing PGDB tests"
  doc pgdb_do_epic "install.sh" "review_pgdb_patch"  
  doc pgdb_do_epic "update.sh" "review_pgdb_patch"  
  pgdb_tests_info="[PGDB tests PASSED]"
  
  java_tests_info="[Java tests SKIPPED]"
  if [ "${DO_JAVA_UNIT_TESTS:0:1}" = "y" ] ; then
    echo ": Executing Java unit tests"
    doc ui_unittest "oix-dev9" $branch "review_pgdb_patch"
    java_tests_info="[Java tests $branch PASSED]"
  fi
  
  stopped_time=$(date +%s.%N)
  dt=$(echo "$stopped_time - $started_time" | bc)
  dd=$(echo "$dt/86400" | bc)
  dt2=$(echo "$dt-86400*$dd" | bc)
  dh=$(echo "$dt2/3600" | bc)
  dt3=$(echo "$dt2-3600*$dh" | bc)
  dm=$(echo "$dt3/60" | bc)
  ds=$(echo "$dt3-60*$dm" | bc)
  used_time=`printf "%d:%02d:%02d:%02.4f" $dd $dh $dm $ds`
  info="Patch $patch is reviewed by Bamboo in $branch_name, $used_time sec $pgdb_tests_info $java_tests_info"
  echo ": $info"
  jira_comment=`echo -e "$jira_comment \n $info"`
done

echo ": Adding a comment to $JIRA_ISSUE"
jira --action addComment --issue "$JIRA_ISSUE" --comment "$jira_comment" 2>&1
[ "$?" != "0" ] && { echo ": Error" ; exit 1 ; }

exit 0
