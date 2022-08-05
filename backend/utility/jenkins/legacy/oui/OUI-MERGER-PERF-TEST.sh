#! /bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

docl checkout_file svn+ssh://svn/home/svnroot/oix/ui/trunk/utility/dev/merger-performance-test/perf_test.py perf_test.py
docl cp $CHECKOUT_FILE $WORKING_DIR

execute_remote_ex `whoami` stat-nbperf "*.py *.sh" <<-"EOF"
  WORKING_DIR=$(cd `dirname $0`; pwd)
  . $WORKING_DIR/commons.sh

  PERF_DB=nb_trunk_perf
  RESULT_DB="'dbname=jmeter_tests host=stat-nbmaster port=5432 user=oix password=adserver'"
  MERGER_FOLDER=/opt/foros/pgdb/var/spool/merger
  MERGER_FAILURE_FOLDER=/opt/foros/pgdb/var/spool/merger/failure
  MERGER_LOG=/opt/foros/pgdb/var/log/nb_trunk_perf/merger/merger.log
  ROW_COUNT=20000
  FILES_COUNT=10
  TIMEOUT=9000 # sec, = 2 h 30 min

  doc "sudo -Hiu uiuser python -u $WORKING_DIR/perf_test.py $PERF_DB "$RESULT_DB" $MERGER_FOLDER $MERGER_FAILURE_FOLDER $MERGER_LOG $ROW_COUNT $FILES_COUNT $TIMEOUT"
EOF

exit $?
