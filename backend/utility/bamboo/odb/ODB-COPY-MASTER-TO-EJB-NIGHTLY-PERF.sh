#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Weekly - every Saturday at 9 am

doc copy_statdb "stat-nbperf.ocslab.com" "ejb_nightly_perf" "stat-dev0.ocslab.com" "nb_copy"

exit 0
