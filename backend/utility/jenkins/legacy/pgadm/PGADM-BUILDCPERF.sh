#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.pgadm.sh

VERSION_FILE=$WORKING_DIR/foros-postgresdb-version
get_from_store "foros-postgresdb-version" $VERSION_FILE
VERSION=`cat $VERSION_FILE`

postgresdb_download_colocation_xml "moscow-nb-oui-perf" "$VERSION"
postgresdb_create_config_rpms "$VERSION" "trunk" "$COLO_XML_FILE"

create_store "moscow-nb-perf-postgresdb"
put_to_store "moscow-nb-perf-postgresdb" $WORKING_DIR/postgresdb_rpms
