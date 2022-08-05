#!/bin/bash

merger_start() {
  local svn_path=$1
  local database=$2
  local level=$3

  [ -z "$svn_path" ] && { echo ": $FUNCNAME: Undefined svn_path" ; exit 1 ; }
  [ -z "$database" ] && { echo ": $FUNCNAME: Undefined database" ; exit 1 ; }
  [ -z "$level" ] && level="PERF"

  doc sudo -iu uiuser mkdir -p $WORKING_DIR/{merger_cache,merger_cache/failure,merger_log,ply-output-dir}
  doc svn_export_folder svn+ssh://svn/home/svnroot/oix/pgdb/$svn_path/utility/merger

  echo "
[general]
incoming-directory = $WORKING_DIR/merger_cache
failure-directory = $WORKING_DIR/merger_cache/failure
rules-directory = $CHECKOUT_FOLDER/share
pid-file = $WORKING_DIR/merger.pid
workers = 1

[logging]
file = $WORKING_DIR/merger_log/merger.log
level = $level

[misc]
ply-output-dir = $WORKING_DIR/ply-output-dir

[credentials]
database = $database
user = merger
host = stat-dev0
port = 5432
" > $WORKING_DIR/merger.conf
    local params="--no-wait-replication $WORKING_DIR/merger.conf"

  echo "
stat-dev0:5432:*:merger:adserver
  " > $WORKING_DIR/.pgpass
  docl chmod 600 $WORKING_DIR/.pgpass
  docl sudo chown uiuser:uiuser $WORKING_DIR/.pgpass

  echo; echo "Starting merger:"
  docl sudo -i -u uiuser env PGPASSFILE=$WORKING_DIR/.pgpass python -u $CHECKOUT_FOLDER/merger.py $params
}

merger_stop() {
  echo; echo "Stopping merger:"
  ps axu | grep `cat $WORKING_DIR/merger.pid`
  sudo -u uiuser -i kill `cat $WORKING_DIR/merger.pid`
  sudo chown -R maint:maint $WORKING_DIR/{merger_cache,merger_cache/failure,merger_log,ply-output-dir,.pgpass}
}

merger_func_test() {
  local svn_path=$1
  local db=$2

  [ -z "$svn_path" ] && { echo ": $FUNCNAME: Undefined svn_path" ; exit 1 ; }
  [ -z "$db" ] && { echo ": $FUNCNAME: Undefined db" ; exit 1 ; }

  echo ": $FUNCNAME: branch '$svn_path', db '$db'"
  execute_remote_ex `whoami` stat-dev0 "*.py *.sh" $svn_path $db <<-"EOF"
    WORKING_DIR=$(cd `dirname $0`; pwd)
    . $WORKING_DIR/commons.sh

    PGDB_SVN_PATH=$1
    PG_DATABASE=$2

    trap "merger_stop" EXIT
    merger_start $PGDB_SVN_PATH $PG_DATABASE "DEBUG"

    RESULT_DB="--do-not-save-results"
    MERGER_FOLDER=$WORKING_DIR/merger_cache
    MERGER_FAILURE_FOLDER=$WORKING_DIR/merger_cache/failure
    MERGER_LOG=$WORKING_DIR/merger_log/merger.log
    ROW_COUNT=1
    FILES_COUNT=10
    TIMEOUT=600

    echo; echo "Starting perf_test.py"
    doc "sudo -u uiuser python -u $WORKING_DIR/perf_test.py -- $PG_DATABASE $RESULT_DB $MERGER_FOLDER $MERGER_FAILURE_FOLDER $MERGER_LOG $ROW_COUNT $FILES_COUNT $TIMEOUT"
    echo; echo "Checking merger log for incorrect files"
    exit 0
EOF
  return $?
}
