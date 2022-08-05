#!/bin/bash

WORKING_DIR=`pwd`
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.ui.sh

# Patch the database
get_pgdb_version "epostgres.ocslab.com" 5432 "stat" "oix"
doc patch_oracle "UNITTEST_UI_10" "$PGDB_VERSION" "trunk" "no" "no"
doc patch_pg_repl "stat-dev0" 5432 "unittest_ui_10" "oix" "trunk"
doc replicate_tables "stat-dev0.ocslab.com" 5432 "unittest_ui_10"
doc patch_pg_statdb "stat-dev0" 5432 "unittest_ui_10" "oix" "branches/$PGDB_VERSION"

get_latests_branch 2 "svn+ssh://svn/home/svnroot/oix/ui/branches"
ui_unittest oix-dev7 branches/$LATESTS_BRANCH
result=$?

exit $result

