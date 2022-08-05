#!/bin/bash

WORKING_DIR=`pwd`
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.ui.sh

# Scheduled at
# Each Monday, Tuesday, Wednesday, Thursday and Friday every 3 hours from 10:00 am to 8:00 pm

# Patch the database
get_pgdb_version "stat-test.ocslab.com" 5432 "stat_test" "oix"
doc patch_oracle "UNITTEST_UI_11" "$PGDB_VERSION" "trunk" "no" "no"
doc patch_pg_repl "stat-dev0" 5432 "unittest_ui_11" "oix" "trunk"
#doc replicate_tables "stat-dev0.ocslab.com" 5432 "unittest_ui_11"
doc patch_pg_statdb "stat-dev0" 5432 "unittest_ui_11" "oix" "branches/$PGDB_VERSION"

doc get_latests_branch 1 "svn+ssh://svn/home/svnroot/oix/ui/branches"
doc ui_unittest oix-dev6 branches/$LATESTS_BRANCH

exit 0

