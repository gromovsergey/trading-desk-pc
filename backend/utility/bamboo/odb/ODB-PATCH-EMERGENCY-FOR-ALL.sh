#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Monday, Tuesday, Wednesday, Thursday and Friday at 9:00 pm

# Patch UI_DEV_10 till latest Emergency branch
get_pgdb_version "epostgres.ocslab.com" 5432 "stat" "oix"
doc patch_oracle "UI_DEV_10" "$PGDB_VERSION" "trunk" "no" "no"
doc patch_pg_repl "stat-dev0" 5432 "ui_dev_10" "oix" "trunk"
doc replicate_tables "stat-dev0.ocslab.com" 5432 "ui_dev_10"
doc patch_pg_statdb "stat-dev0" 5432 "ui_dev_10" "oix" "branches/$PGDB_VERSION"

get_pgdb_bi_version "epostgres.ocslab.com" 5432 "stat" "bi"
doc patch_pg_bi "stat-dev0" 5432 "ui_dev_10" "bi" "branches/$BI_VERSION"

exit 0

