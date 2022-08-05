#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Monday, Tuesday, Wednesday, Thursday and Friday every hour from 10:00 am to 9:00 pm

get_latests_branch 1 "svn+ssh://svn/home/svnroot/oix/pgdb/branches"
PGDB_VERSION="$LATESTS_BRANCH"

get_latests_branch 1 "svn+ssh://svn/home/svnroot/oix/bi/branches"
BI_VERSION="$LATESTS_BRANCH"

doc patch_postgres "ui_dev_18" "$PGDB_VERSION" "$BI_VERSION"

exit 0

