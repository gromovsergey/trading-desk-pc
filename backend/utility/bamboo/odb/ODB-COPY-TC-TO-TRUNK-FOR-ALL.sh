#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Saturday at 12:00 am

SKIP_ORACLE_REFRESH="${bamboo_ouiSkipOracleRefresh}"
FORCE_RUN="${bamboo_ouiForceRun}"

# Don't execute this task during a working week
# User must DISABLE "4.1 Patch Unittest TEST-FOR-ALL" before
NH=`date +%u`
[ "$NH" -lt "6" -a "$FORCE_RUN" = "false" ] && { echo -e "To start,\n1) Disable (ENABLE AFTER) '4.2 Patch Unittest TRUNK-FOR-ALL'\n2) Run this as Customised" ; exit 1 ; }

# Refresh Oracle and Postgres UI_DEV_12
[ "$SKIP_ORACLE_REFRESH" != "true" ] && { doc refresh_oracle_from_test_patched_till_trunk "12" ; }
doc refresh_postgres_from_test "12"

# Patch it till trunk and sync their structure/data
# The patching must be done BEFORE the sync
doc patch_schema_replication "oradev.ocslab.com" 1521 "addbtc.ocslab.com" "UI_DEV_12"
doc patch_schema "oradev.ocslab.com" 1521 "addbtc.ocslab.com" "UI_DEV_12" "trunk" "no" "no"
doc patch_pg_repl "stat-dev0" 5432 "ui_dev_12" "oix" "trunk"
doc replicate_tables "stat-dev0.ocslab.com" 5432 "ui_dev_12"
doc patch_pg_statdb "stat-dev0" 5432 "ui_dev_12" "oix" "trunk"

# Refresh Oracle and Postgres UNITTEST_UI_12
doc refresh_oracle_unittest "12"
doc copy_postgres_unittest "12"

doc patch_schema_replication "ora-nb.ocslab.com" 1521 "addbnba.ocslab.com" "UNITTEST_UI_12"
doc patch_schema "ora-nb.ocslab.com" 1521 "addbnba.ocslab.com" "UNITTEST_UI_12" "trunk" "no" "no"
doc patch_pg_repl "stat-dev0" 5432 "unittest_ui_12" "oix" "trunk"
doc replicate_tables "stat-dev0.ocslab.com" 5432 "unittest_ui_12"
doc patch_pg_statdb "stat-dev0" 5432 "unittest_ui_12" "oix" "trunk"

exit 0

