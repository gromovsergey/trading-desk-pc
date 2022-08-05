#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Manually per request from QA

doc stop_cluster "oui-nbmaster0" "ui-moscow"
doc stop_cluster "stat-nbmaster" "pgdb-moscow"

doc copy_statdb "stat-nbmaster" "stat" "stat-dev0" "test_full_copy" --full-copy

jobs_settings="svn+ssh://svn/home/svnroot/oix/pgdb/trunk/cms-plugin/configs/moscow-nb-master/jobs_settings.sql"
doc svn export $jobs_settings $WORKING_DIR/jobs_settings.sql
doc apply_pg_sql "stat-nbmaster" "5432" "stat" "oix" $WORKING_DIR/jobs_settings.sql

doc sync_oui_files "voix0" "oui-nbmaster0" "uiuser"

exit 0

