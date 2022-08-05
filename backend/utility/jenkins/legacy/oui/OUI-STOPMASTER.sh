#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.zenoss.sh

for host in oui-nbmaster0 stat-nbmaster; do
  set_zenoss_device_state $ZENOSS_NB_HOST $host $ZENOSS_MAINTENANCE_STATE
done

doc stop_cluster "oui-nbmaster0" "ui-moscow"
doc stop_cluster "stat-nbmaster" "pgdb-moscow"

exit 0


