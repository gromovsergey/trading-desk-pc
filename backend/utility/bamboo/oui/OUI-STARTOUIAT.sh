#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.zenoss.sh

echo ": $BAMBOO_SUBTASK: Copy Postgres DB"
copy_statdb "stat-nbouiat" "nb_trunk_auto" "stat-dev0.ocslab.com" "nb_copy" \
  "--truncate-tables=stat.webwisediscoveritem,ctr.ctr_kw_tow_matrix,jobs.jobs"
[ "$?" != "0" ] && exit 1

echo ": $BAMBOO_SUBTASK: fixing jobs settings"
jobs_settings="svn+ssh://svn/home/svnroot/oix/pgdb/trunk/cms-plugin/configs/moscow-nb-oui-at/jobs_settings.sql"
doc svn export $jobs_settings $WORKING_DIR/jobs_settings.sql
doc apply_pg_sql "stat-nbouiat" "5432" "nb_trunk_auto" "oix" $WORKING_DIR/jobs_settings.sql

echo ": $BAMBOO_SUBTASK: Copying data folders from NB Master colo to NB Auto colo"
doc sync_oui_files "oui-nbmaster0" "oui-nbouiat0" "uikey-moscow-nb-master" "uiuser"

echo ": $BAMBOO_SUBTASK: Clear /opt/foros/ui/var/www/Preview/* files"
doc ssh oui-nbouiat0 "sudo -u uiuser rm -rf /opt/foros/ui/var/www/Preview/*"

start_cluster "stat-nbouiat" "pgdb-moscow"
start_cluster "oui-nbouiat0" "ui-moscow"

echo ": $BAMBOO_SUBTASK: Switch ZenOSS on"
for host in oui-nbouiat0 oui-nbouiat1 stat-nbouiat; do
  set_zenoss_device_state $ZENOSS_NB_HOST $host $ZENOSS_PRODUCTION_STATE
done

exit 0

