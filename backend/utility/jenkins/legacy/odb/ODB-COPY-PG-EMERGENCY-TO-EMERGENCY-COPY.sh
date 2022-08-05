#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
TMP_WORKING_DIR="/tmp/jenkins-bamboo$WORKING_DIR"
rm -rf "$TMP_WORKING_DIR"
mkdir -p "$TMP_WORKING_DIR"
cp -rf "$WORKING_DIR"/* "$TMP_WORKING_DIR"/
WORKING_DIR="$TMP_WORKING_DIR"

. $WORKING_DIR/commons.sh

# Schedule: Each Monday, 3:00 am

doc copy_statdb "stat-dev0" "emergency_full_copy" "epostdb00" "stat" --full-copy
doc copy_statdb "stat-dev0" "emergency_copy" "stat-dev0" "emergency_full_copy" \
  "--truncate-tables=stat.webwisediscoveritem,stat.channelinventory,jobs.jobs"

exit 0
