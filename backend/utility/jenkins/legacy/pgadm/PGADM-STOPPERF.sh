#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.zenoss.sh

doc stop_product "pgadm" "moscow-nb-oui-perf" "stat-nbperf"

exit 0
