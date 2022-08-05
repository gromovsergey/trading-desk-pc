#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Monday, Tuesday, Wednesday, Thursday and Friday every hour from 10:00 am to 9:00 pm

get_pgdb_version "epostgres.ocslab.com" 5432 "stat" "oix"
doc patch_oracle "UI_DEV_17" "$PGDB_VERSION" "trunk" "no" "no"
doc patch_postgres "ui_dev_17" "$PGDB_VERSION" "trunk"

get_pgdb_bi_version "epostgres.ocslab.com" 5432 "stat" "bi"
doc patch_pg_bi "stat-dev0" 5432 "ui_dev_17" "bi" "branches/$BI_VERSION"

exit 0
