#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Saturday at 7:00 am

get_pgdb_version "epostgres.ocslab.com" 5432 "stat" "oix"

# Refresh Oracle and Postgres UI_DEV_10
doc refresh_oracle_from_emergency "10"
doc refresh_postgres_from_emergency "10"
doc patch_schema_replication "oradev.ocslab.com" 1521 "addbtc.ocslab.com" "UI_DEV_10"
doc patch_oracle "UI_DEV_10" "$PGDB_VERSION" "trunk" "no" "no"
doc replicate_tables "stat-dev0.ocslab.com" 5432 "ui_dev_10"
doc patch_postgres "ui_dev_10" "$PGDB_VERSION" "trunk"

# Refresh Oracle and Postgres UNITTEST_UI_10
doc refresh_oracle_unittest "10"
doc copy_postgres_unittest "10"
doc patch_schema_replication "ora-nb.ocslab.com" 1521 "addbnba.ocslab.com" "UNITTEST_UI_10"
doc patch_schema "ora-nb.ocslab.com" 1521 "addbnba.ocslab.com" "UNITTEST_UI_10" "branches/$PGDB_VERSION" "no" "no"
doc replicate_tables "stat-dev0.ocslab.com" 5432 "unittest_ui_10"
doc patch_postgres "unittest_ui_10" "$PGDB_VERSION" "trunk"

exit 0

