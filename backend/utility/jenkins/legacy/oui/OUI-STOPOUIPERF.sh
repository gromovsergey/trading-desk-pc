#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.zenoss.sh

for host in oui-nbperf0 stat-nbperf; do
  set_zenoss_device_state $ZENOSS_NB_HOST $host $ZENOSS_MAINTENANCE_STATE
done

doc stop_product "ui" "moscow-nb-oui-perf" "oui-nbperf0"
doc stop_product "pgdb" "moscow-nb-oui-perf" "stat-nbperf"

exit 0
