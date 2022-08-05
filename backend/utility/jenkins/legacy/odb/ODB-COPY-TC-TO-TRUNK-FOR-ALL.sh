#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

doc refresh_postgres "stat-test.ocslab.com" "test_copy" "ui_dev_12"

exit 0

