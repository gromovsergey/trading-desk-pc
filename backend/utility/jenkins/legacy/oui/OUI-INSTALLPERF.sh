#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.ui.sh
. $WORKING_DIR/commons.pgdb.sh
. $WORKING_DIR/commons.adclient.sh

VERSION_FILE=$WORKING_DIR/foros-ui-version
get_from_store "foros-ui-version" $VERSION_FILE
VERSION=`cat $VERSION_FILE`

get_from_store "moscow-nb-oui-perf-creatives" $WORKING_DIR/adclient_rpms
get_from_store "moscow-nb-perf" $WORKING_DIR/ui_rpms
get_from_store "moscow-nb-perf" $WORKING_DIR/pgdb_rpms

echo ; echo ": oui-nbperf0"
uninstall_packages "oui-nbperf0" "foros-ui"
install_packages "oui-nbperf0" "foros-ui-${VERSION}*.rpm"
ui_install_config_rpms "oui-nbperf0"
adclient_install_config_rpms "oui-nbperf0" "foros-creatives"

echo ; echo ": stat-nbperf"
uninstall_packages "stat-nbperf" "foros-pgdb"
install_packages "stat-nbperf" "foros-pgdb-${VERSION}*.rpm"
pgdb_install_config_rpms "stat-nbperf"

# Do not upgrade postgresql; it must be upgraded only as a dependency of foros-pgadm
doc ssh oui-nbperf0 sudo yum -y -x 'foros-*' -x 'boost-*' upgrade
doc ssh stat-nbperf sudo yum -y -x 'foros-*' -x 'postgresql*' -x 'boost-*' upgrade

exit 0
