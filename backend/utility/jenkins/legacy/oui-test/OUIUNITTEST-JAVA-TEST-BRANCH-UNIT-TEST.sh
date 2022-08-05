#!/bin/bash

WORKING_DIR=`pwd`
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.ui.sh

# Scheduled at
# Each Monday, Tuesday, Wednesday, Thursday and Friday every 3 hours from 10:00 am to 8:00 pm

#get_pgdb_version "stat-test.ocslab.com" 5432 "stat_test"
#doc patch_postgres "unittest_ui_11" "$PGDB_VERSION" "Skip"
get_latests_branch 1 "svn+ssh://svn/home/svnroot/oix/ui/branches"
doc patch_postgres "unittest_ui_11" "$LATESTS_BRANCH" "Skip"
doc ui_unittest oix-dev7 branches/$LATESTS_BRANCH

exit 0

