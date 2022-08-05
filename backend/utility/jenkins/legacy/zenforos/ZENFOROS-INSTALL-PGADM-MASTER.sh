#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.pgadm.sh

VERSION_FILE=$WORKING_DIR/foros-postgresdb-version
update_zenforos_plugin $ZENOSS_NB_HOST "pgadm" -c "moscow-nb-master" -v `cat_from_store foros-postgresdb-version $VERSION_FILE`

exit 0

