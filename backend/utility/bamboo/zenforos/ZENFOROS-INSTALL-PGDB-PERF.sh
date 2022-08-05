#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.pgdb.sh

VERSION_FILE=$WORKING_DIR/foros-ui-version
update_zenforos_plugin $ZENOSS_NB_HOST "pgdb" -c "moscow-nb-oui-perf" -v `cat_from_store foros-ui-version $VERSION_FILE`
test_snmp_walk $ZENOSS_NB_HOST "moscow-nb-oui-perf" "stat-nbperf"
zenoss_model_colocation $ZENOSS_NB_HOST "moscow-nb-oui-perf/PGDB"

exit 0
