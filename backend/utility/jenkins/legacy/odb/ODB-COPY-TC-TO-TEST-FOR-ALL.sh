#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Saturday at 6:00 pm

doc refresh_postgres "stat-test.ocslab.com" "test_copy" "ui_dev_11"

exit 0

