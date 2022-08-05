#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.ui.sh
. $WORKING_DIR/commons.pgdb.sh

VERSION_FILE=$WORKING_DIR/foros-ui-version
get_from_store "foros-ui-version" $VERSION_FILE
VERSION=`cat $VERSION_FILE`

ui_download_colocation_xml "moscow-nb-oui-perf" $VERSION
ui_create_config_rpms $VERSION "trunk" $COLO_XML_FILE

pgdb_download_colocation_xml "moscow-nb-oui-perf" $VERSION
pgdb_create_config_rpms $VERSION "trunk" $COLO_XML_FILE

create_store "moscow-nb-perf"
put_to_store "moscow-nb-perf" $WORKING_DIR/ui_rpms
put_to_store "moscow-nb-perf" $WORKING_DIR/pgdb_rpms

exit 0

