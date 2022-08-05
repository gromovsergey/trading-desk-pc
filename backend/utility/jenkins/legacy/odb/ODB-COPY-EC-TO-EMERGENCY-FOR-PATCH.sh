#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

doc refresh_postgres "epostdb00.ocslab.com" "emergency_copy" "ui_dev_17"

exit 0
