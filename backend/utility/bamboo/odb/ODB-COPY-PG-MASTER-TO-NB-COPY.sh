#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

doc refresh_postgres_common_db "nb" "stat-nbmaster" "nb_trunk_manual" \
  "--truncate-tables=stat.webwisediscoveritem,ctr.ctr_kw_tow_matrix,jobs.jobs"

exit 0


