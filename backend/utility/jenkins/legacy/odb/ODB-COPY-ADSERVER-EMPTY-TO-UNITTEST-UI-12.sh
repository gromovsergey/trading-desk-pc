#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Saturday at 12:00 am

echo ": Copying PGDB unittest database"
doc fast_copy_statdb "stat-dev0" "unittest_db_1" "adserver_empty"
doc pgdb_do_epic "install.sh" "unittest_db_1"
doc patch_postgres "unittest_db_1" "trunk" "trunk"

echo ": Copying Merger unittest database"
doc fast_copy_statdb "stat-dev0" "unittest_merger_1" "adserver_empty"
doc patch_postgres "unittest_merger_1" "trunk" "trunk"

echo ": Copying FOROS UI unittest database"
doc fast_copy_statdb "stat-dev0" "unittest_ui_12" "adserver_empty"
doc patch_postgres "unittest_ui_12" "trunk" "trunk"

exit 0
