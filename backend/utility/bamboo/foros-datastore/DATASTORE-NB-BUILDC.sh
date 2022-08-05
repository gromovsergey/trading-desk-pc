#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.dstr.sh

COLOCATION="${bamboo_Colocation}"

VERSION_FILE=$WORKING_DIR/foros-ui-version
VERSION=`cat_from_store foros-ui-version $VERSION_FILE`

dstr_download_colocation_xml "$COLOCATION" $VERSION
dstr_create_config_rpms $VERSION "trunk" $COLO_XML_FILE

create_store "$COLOCATION-dstr"
put_to_store "$COLOCATION-dstr" $WORKING_DIR/dstr_rpms

exit 0

