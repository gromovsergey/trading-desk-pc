#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Saturday at 12:00 am

get_pgdb_version "stat-test.ocslab.com" 5432 "stat_test"

doc fast_copy_statdb "stat-dev0" "unittest_db_2" "adserver_empty"
doc pgdb_do_epic "install.sh" "unittest_db_2"
doc patch_postgres "unittest_db_2" "$PGDB_VERSION" "$BI_VERSION"

doc fast_copy_statdb "stat-dev0" "unittest_merger_2" "adserver_empty"
doc patch_postgres "unittest_merger_2" "$PGDB_VERSION" "$BI_VERSION"

doc fast_copy_statdb "stat-dev0" "unittest_ui_11" "adserver_empty"
doc patch_postgres "unittest_ui_11" "$PGDB_VERSION" "$BI_VERSION"

exit 0
