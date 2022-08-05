#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.zenoss.sh

FORCE_RUN="${bamboo_ouiForceRun}"

# Don't stop this colo during a day
if [ "$FORCE_RUN" = "false" ] ; then
  NH=`date +%H`
  [ "$NH" -gt "6" -a "$NH" -lt "20" ] && { echo "Can't be stopped during a day. Managed by NB workflow only" ; exit 1 ; }
fi

for host in oui-nbouiat0 oui-nbouiat1 stat-nbouiat; do
  set_zenoss_device_state $ZENOSS_NB_HOST $host $ZENOSS_MAINTENANCE_STATE
done

doc stop_cluster "oui-nbouiat0" "ui-moscow"
doc stop_cluster "stat-nbouiat" "pgdb-moscow"

exit 0

