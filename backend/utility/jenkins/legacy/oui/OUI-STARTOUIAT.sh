#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.zenoss.sh

echo ": $BAMBOO_SUBTASK: Copy Postgres DB"
doc copy_statdb "stat-nbouiat" "nb_trunk_auto" "stat-nbmaster" "stat" \
  "--truncate-tables=stat.webwisediscoveritem,stat.channelinventory,jobs.jobs"

echo ": $BAMBOO_SUBTASK: fixing jobs settings"
jobs_settings="svn+ssh://svn/home/svnroot/foros/pgdb/trunk/cms-plugin/configs/moscow-nb-oui-at/jobs_settings.sql"
doc svn export $jobs_settings $WORKING_DIR/jobs_settings.sql
doc apply_pg_sql "stat-nbouiat" "5432" "nb_trunk_auto" "oix" $WORKING_DIR/jobs_settings.sql

echo ": $BAMBOO_SUBTASK: Sync files"
doc sync_oui_files "oui-nbmaster0" "oui-nbouiat0" "uiuser"

# echo ": $BAMBOO_SUBTASK: Copy Impala database"
# doc impala_refresh_database "nbmaster" "nbauto" 1
# doc update_hadoop_applied_patches "nbmaster" "nbauto"

echo ": $BAMBOO_SUBTASK: Start the cluster"
start_cluster "stat-nbouiat" "pgdb-moscow"
start_cluster "oui-nbouiat0" "ui-moscow"

echo ": $BAMBOO_SUBTASK: Switch ZenOSS on"
for host in oui-nbouiat0 oui-nbouiat1 stat-nbouiat; do
  set_zenoss_device_state $ZENOSS_NB_HOST $host $ZENOSS_PRODUCTION_STATE
done

exit 0
