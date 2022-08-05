#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Saturday at 7:00 am

doc refresh_postgres "epostdb00.ocslab.com" "emergency_copy" "ui_dev_10"

exit 0
