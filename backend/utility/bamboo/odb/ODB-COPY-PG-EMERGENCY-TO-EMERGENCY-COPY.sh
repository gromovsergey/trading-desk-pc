#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Monday, 3:00 am
doc refresh_postgres_common_db "emergency" "epostgres" "stat" \
  "--truncate-tables=stat.webwisediscoveritem,stat.channelinventory,jobs.jobs"

exit 0

