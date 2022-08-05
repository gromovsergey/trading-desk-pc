#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Saturday at 6:00 pm

SKIP_ORACLE_REFRESH="${bamboo_ouiSkipOracleRefresh}"
FORCE_RUN="${bamboo_ouiForceRun}"

# Don't execute this task during a working week
# User must DISABLE "4.1 Patch Unittest TEST-FOR-ALL" before
NH=`date +%u`
[ "$NH" -lt "6" -a "$FORCE_RUN" = "false" ] && { echo -e "To start,\n1) Disable (ENABLE AFTER) '4.1 Patch Unittest TEST-FOR-ALL'\n2) Run this as Customised" ; exit 1 ; }

get_pgdb_version "stat-test.ocslab.com" 5432 "stat_test" "oix"

# Refresh Oracle and Postgres UI_DEV_11
[ "$SKIP_ORACLE_REFRESH" != "true" ] && { doc refresh_oracle_from_test "11" ; }
doc refresh_postgres_from_test "11"
doc patch_oracle "UI_DEV_11" "$PGDB_VERSION" "trunk" "no" "no"
doc patch_pg_repl "stat-dev0" 5432 "ui_dev_11" "oix" "trunk"
doc replicate_tables "stat-dev0.ocslab.com" 5432 "ui_dev_11"
doc patch_pg_statdb "stat-dev0" 5432 "ui_dev_11" "oix" "branches/$PGDB_VERSION"

# Refresh Oracle and Postgres UNITTEST_UI_11
doc refresh_oracle_unittest "11"
doc copy_postgres_unittest "11"
doc patch_oracle "UNITTEST_UI_11" "$PGDB_VERSION" "trunk" "no" "no"
doc patch_pg_repl "stat-dev0" 5432 "unittest_ui_11" "oix" "trunk"
doc replicate_tables "stat-dev0.ocslab.com" 5432 "unittest_ui_11"
doc patch_pg_statdb "stat-dev0" 5432 "unittest_ui_11" "oix" "branches/$PGDB_VERSION"

exit 0

