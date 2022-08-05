#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

VERSION_FILE=$WORKING_DIR/foros-ui-version
VERSION=`cat_from_store foros-ui-version $VERSION_FILE`


echo ; echo ": stat-nbmaster"
uninstall_packages "stat-nbmaster" "foros-bi-pgdb"
install_packages_from_repo "stat-nbmaster" "foros-bi-pgdb" "$VERSION"
start_cluster "stat-nbmaster" "pgdb-moscow" "bi"

exit 0

