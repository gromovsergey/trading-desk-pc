#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.zenoss.sh

doc start_product "pgdb" "moscow-nb-oui-perf" "stat-nbperf"
doc start_product "ui" "moscow-nb-oui-perf" "oui-nbperf0"

for host in oui-nbperf0 stat-nbperf; do
  set_zenoss_device_state $ZENOSS_NB_HOST $host $ZENOSS_PRODUCTION_STATE
done

exit 0

