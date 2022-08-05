#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

VERSION_FILE=$WORKING_DIR/foros-ui-version
VERSION=`cat_from_store foros-ui-version $VERSION_FILE`


echo ; echo ": stat-nbperf"
uninstall_packages "stat-nbperf" "foros-bi-pgdb"
install_packages "stat-nbperf" "foros-bi-pgdb-${VERSION}*.rpm"
start_cluster "stat-nbperf" "pgdb-moscow" "bi"

exit 0

