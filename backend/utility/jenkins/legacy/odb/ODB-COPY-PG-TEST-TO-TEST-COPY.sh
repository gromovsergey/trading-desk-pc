#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Each Saturday at 3:00 am

doc copy_statdb "stat-dev0" "test_full_copy" "stat-test" "stat_test" --full-copy
doc copy_statdb "stat-dev0" "test_copy" "stat-dev0" "test_full_copy" \
  "--truncate-tables=stat.webwisediscoveritem,stat.channelinventory,jobs.jobs"

# Keep a copy from Test for AT Test colo
# This is for a case when PostgreSQL on stat-dev0 is upgraded (from 9.4 to 9.5 for example)
# but on AT Test there is still 9.4
doc copy_statdb "stat-attest" "test_copy" "stat-test" "stat_test" \
  "--truncate-tables=stat.webwisediscoveritem,stat.channelinventory,jobs.jobs"

exit 0
