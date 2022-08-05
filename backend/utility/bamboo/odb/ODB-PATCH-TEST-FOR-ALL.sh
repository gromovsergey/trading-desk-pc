#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Monday, Tuesday, Wednesday, Thursday and Friday at 9:00 pm

# Patch Oracle UI_DEV_11 till latests Test's branch
get_pgdb_version "stat-test.ocslab.com" 5432 "stat_test" "oix"
doc patch_oracle "UI_DEV_11" "$PGDB_VERSION" "trunk" "no" "no"
doc patch_pg_repl "stat-dev0" 5432 "ui_dev_11" "oix" "trunk"
doc replicate_tables "stat-dev0.ocslab.com" 5432 "ui_dev_11"
doc patch_pg_statdb "stat-dev0" 5432 "ui_dev_11" "oix" "branches/$PGDB_VERSION"

get_pgdb_bi_version "stat-test.ocslab.com" 5432 "stat_test" "bi"
doc patch_pg_bi "stat-dev0" 5432 "ui_dev_11" "bi" "branches/$BI_VERSION"

exit 0

