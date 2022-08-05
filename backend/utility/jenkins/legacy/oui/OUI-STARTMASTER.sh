#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.zenoss.sh

start_cluster "stat-nbmaster" "pgdb-moscow"
start_cluster "oui-nbmaster0" "ui-moscow"

for host in oui-nbmaster0 stat-nbmaster; do
  set_zenoss_device_state $ZENOSS_NB_HOST $host $ZENOSS_PRODUCTION_STATE
done

exit 0

