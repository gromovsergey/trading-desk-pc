#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Uncomment when Oracle is off
# doc patch_pg_statdb "stat-nbperf.ocslab.com" 5432 "ejb_nightly_perf" "oix" "trunk"

exit 0
