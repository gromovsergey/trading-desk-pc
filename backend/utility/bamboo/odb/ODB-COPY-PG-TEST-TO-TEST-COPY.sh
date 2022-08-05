#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Monday, 5:00 am

case $BAMBOO_SUBTASK in
  TESTCOPY)
    doc refresh_postgres_common_db "test" "stat-test" "stat_test" \
      "--truncate-tables=stat.webwisediscoveritem,ctr.ctr_kw_tow_matrix,stat.channelinventory,jobs.jobs"
    ;;
  REFRESHCOPY)
    doc fast_copy_statdb "stat-dev0" "test_trunk_copy" "test_copy" "oradev.ocslab.com" "addbtc.ocslab.com" "1521"
    ;;
  REFRESHFULLCOPY)
    doc fast_copy_statdb "stat-dev0" "test_trunk_full_copy" "test_full_copy" "oradev.ocslab.com" "addbtc.ocslab.com" "1521"
    ;;
  PATCHLC)
    doc patch_oracle "ADSERVER_LC" "trunk" "trunk" "no" "no"
    ;;
  PATCHCOPY)
    doc patch_postgres "test_trunk_copy" "trunk" "S"
    ;;
  PATCHFULLCOPY)
    doc patch_postgres "test_trunk_full_copy" "trunk" "S"
    ;;
  *)
    echo "Unknown subtask $BAMBOO_SUBTASK"
    exit 1
    ;;
esac

exit 0

