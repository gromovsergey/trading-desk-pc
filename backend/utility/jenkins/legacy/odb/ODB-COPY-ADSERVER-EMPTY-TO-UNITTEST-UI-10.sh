#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Saturday at 12:00 am

get_pgdb_version "epostdb00.ocslab.com" 5432 "stat"

doc fast_copy_statdb "stat-dev0" "unittest_db_3" "adserver_empty"
doc pgdb_do_epic "install.sh" "unittest_db_3"
doc patch_postgres "unittest_db_3" "$PGDB_VERSION" "$BI_VERSION"

doc fast_copy_statdb "stat-dev0" "unittest_merger_3" "adserver_empty"
doc patch_postgres "unittest_merger_3" "$PGDB_VERSION" "$BI_VERSION"

doc fast_copy_statdb "stat-dev0" "unittest_ui_10" "adserver_empty"
doc patch_postgres "unittest_ui_10" "$PGDB_VERSION" "$BI_VERSION"

exit 0
