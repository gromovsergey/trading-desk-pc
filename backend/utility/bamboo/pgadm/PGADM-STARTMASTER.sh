#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.zenoss.sh

start_cluster "stat-nbmaster" "pgadm"

exit 0
