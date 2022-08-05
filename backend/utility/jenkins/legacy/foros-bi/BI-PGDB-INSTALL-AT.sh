#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

VERSION_FILE=$WORKING_DIR/foros-ui-version
VERSION=`cat_from_store foros-ui-version $VERSION_FILE`


echo ; echo ": stat-nbouiat"
uninstall_packages "stat-nbouiat" "foros-bi-pgdb"
install_packages "stat-nbouiat" "foros-bi-pgdb-${VERSION}*.rpm"
start_cluster "stat-nbouiat" "pgdb-moscow" "bi"

exit 0

