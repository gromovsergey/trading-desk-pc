#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

VERSION_FILE=$WORKING_DIR/foros-ui-version
get_from_store "foros-ui-version" $VERSION_FILE
VERSION=`cat $VERSION_FILE`

install_packages "$ZENOSS_NB_HOST" `product_get_package_list "pgdb" -v $VERSION -Z -i`

exit 0
