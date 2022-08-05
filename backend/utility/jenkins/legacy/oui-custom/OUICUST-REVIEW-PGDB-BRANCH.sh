#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.jira.sh
. $WORKING_DIR/commons.ui.build.sh

JIRA_ISSUE=${bamboo__1_JiraIssue}
PGDB_BRANCH=${bamboo__2_PgdbSvnBranch}
[ "$PGDB_BRANCH" = "svn branch name" ] && { echo ": Invalid parameter _2_PgdbSvnBracnh" ; exit 1 ; }
DO_JAVA_UNIT_TESTS=${bamboo__3_DoJavaUnitTests}
LDAP_USER="pgdb.project.coordinator"
LDAP_PASSWORD=`cat ~/.bamboo | awk '{ if (NR == 1) print $0 }'`

started_time=$(date +%s.%N)

doc get_svn_path "$PGDB_BRANCH"
branch="$SVN_PATH"  
echo ": Reviewing $branch"
  
echo ": Copy an empty database to stat-dev0/review_pgdb_branch"
doc fast_copy_statdb "stat-dev0" "review_pgdb_branch" "adserver_empty"
  
echo ": Patching it using $branch"
doc patch_postgres "review_pgdb_branch" "$PGDB_BRANCH" "S"
  
echo ": Executing PGDB tests"
doc pgdb_do_epic "install.sh" "review_pgdb_branch"  
doc pgdb_do_epic "update.sh" "review_pgdb_branch"  
pgdb_tests_info="[PGDB tests PASSED]"

java_tests_info="[Java tests SKIPPED]"
if [ "${JAVA_UNIT_TESTS:0:1}" = "y" ] ; then
  echo ": Executing Java unit tests"
  doc get_ancestor "svn+ssh://svn/home/svnroot/oix/pgdb/$branch"
  doc ui_unittest "oix-dev8" $SVN_ANCESTOR_BRANCH_NAME "review_pgdb_branch"
  java_tests_info="[Java tests $SVN_ANCESTOR_BRANCH_NAME PASSED]"
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

echo ": Adding a comment to $JIRA_ISSUE"
jira --action addComment --issue "$JIRA_ISSUE" \
  --comment "Branch $branch is reviewed by Bamboo in $used_time sec $pgdb_tests_info $java_tests_info" 2>&1
[ "$?" != "0" ] && { echo ": Error" ; exit 1 ; }

exit 0
