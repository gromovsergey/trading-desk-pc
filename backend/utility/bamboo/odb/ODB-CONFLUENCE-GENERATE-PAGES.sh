#!/bin/bash

WORKING_DIR=`pwd`
. $WORKING_DIR/commons.sh

create_passfile() {
  echo ": $FUNCNAME: "
  PASSFILE=$WORKING_DIR/.confluencepassword
  echo "${bamboo_password}" > $PASSFILE
  chmod 600 $PASSFILE
}

delete_passfile() {
  echo ": $FUNCNAME: "
  rm -f $PASSFILE $@
}

trap "delete_passfile $PASSFILE" EXIT

checkout_utils() {
  echo ": $FUNCNAME: "
  svn_export_folder "svn+ssh://svn/home/svnroot/oix/ui/trunk/utility/dev/pgtables" || return 1
  UTILS_FOLDER=$CHECKOUT_FOLDER
  return 0
}

download_pages() {
  echo ": $FUNCNAME: $@"
  local pages="$1"
  $UTILS_FOLDER/download_pages.py --passfile $PASSFILE $pages || return 1
  return 0
}

create_comments() {
  echo ": $FUNCNAME: $@"
  local pages="$1"
  local comments="$2"
  $UTILS_FOLDER/create_comments.py $pages $comments || return 1
  # echo ": $FUNCNAME: cat $comments"
  # cat $comments
  return 0
}

commit_comments() {
  echo ": $FUNCNAME: $@"
  local comments="$1"
  local svnurl="http://svn.ocslab.com/svnroot/oix/pgdb/trunk/patches/patch-utility-scripts"
  local auth="--username oix.project.coordinator --password ${bamboo_password} --non-interactive"

  svn co $auth $svnurl $WORKING_DIR/patch-utility-scripts || return 1
  pushd $WORKING_DIR/patch-utility-scripts
    cp $comments . || return 1
    local diff="`svn diff`"
    echo "$diff"
    if [ -z "$diff" ]; then
      echo ": $FUNCNAME: There are no changes - nothing to do"
    else
      svn commit $auth -m "Comments updated by ${bamboo_resultsUrl}" || return 1
    fi
  popd
  return 0
}

apply_comments() {
  echo ": $FUNCNAME: $@"
  local comments="$1"
  local tempfile=`mktemp $WORKING_DIR/comments.XXXXXXXX`
  echo "set foros.comments_errors = 'raise';" > $tempfile
  cat $comments >> $tempfile
  apply_pg_sql "stat-dev0" "5432" "unittest_ui_12" "oix" $tempfile "-tA" "-v ON_ERROR_STOP=1" || return 1
  return 0
}

update_pages() {
  echo ": $FUNCNAME: $@"
  local pages="$1"
  local newpages="$2"
  $UTILS_FOLDER/update_pages.py \
    --tables-sql $WORKING_DIR/get_tables_description.sql \
    --columns-sql $WORKING_DIR/get_columns_description.sql \
    $pages $newpages || return 1
  return 0
}

upload_pages() {
  echo ": $FUNCNAME: $@"
  local pages="$1"
  local newpages="$2"
  $UTILS_FOLDER/upload_pages.py --passfile $PASSFILE $pages $newpages || return 1
  return 0
}

collect_artifacts() {
  echo ": $FUNCNAME: $@"
  local artdir="/u01/Bamboo/bamboo-home/xml-data/build-dir/ODB-CONFLUENCEGENERATEPGPATCHTOCOMMENTCOLUMNS1-JOB1/arts"
  rm -rf "$artdir"
  mkdir -p "$artdir"
  cp $@ $artdir
}

doc checkout_utils
doc create_passfile
PAGES=$WORKING_DIR/pages.json
doc download_pages $PAGES
COMMENTS=$WORKING_DIR/comments.psql
doc create_comments $PAGES $COMMENTS
doc commit_comments $COMMENTS
doc apply_comments $COMMENTS
NEWPAGES=$WORKING_DIR/newpages.json
doc update_pages $PAGES $NEWPAGES
doc upload_pages $PAGES $NEWPAGES
doc delete_passfile
collect_artifacts $PAGES $COMMENTS $NEWPAGES $WORKING_DIR/*log
