#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Monday, Tuesday, Wednesday, Thursday and Friday every hour from 10:00 am to 9:00 pm

get_pgdb_version "epostdb00.ocslab.com" 5432 "stat"
doc patch_postgres "ui_dev_17" "$PGDB_VERSION" "$BI_VERSION"

exit 0
