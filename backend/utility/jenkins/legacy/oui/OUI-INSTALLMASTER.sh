#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.ui.sh
. $WORKING_DIR/commons.pgdb.sh
. $WORKING_DIR/commons.adclient.sh

VERSION_FILE=$WORKING_DIR/foros-ui-version
get_from_store "foros-ui-version" $VERSION_FILE
VERSION=`cat $VERSION_FILE`

#get_from_store "moscow-nb-master-creatives" $WORKING_DIR/adclient_rpms
get_from_store "moscow-nb-master" $WORKING_DIR/ui_rpms
get_from_store "moscow-nb-master" $WORKING_DIR/pgdb_rpms

echo ; echo ": oui-nbmaster0"
uninstall_packages "oui-nbmaster0" "foros-ui"
install_packages "oui-nbmaster0" "foros-ui-${VERSION}*.rpm"
ui_install_config_rpms "oui-nbmaster0"
#adclient_install_config_rpms "oui-nbmaster0" "foros-creatives"

echo ; echo ": stat-nbmaster"
uninstall_packages "stat-nbmaster" "foros-pgdb"
install_packages "stat-nbmaster" "foros-pgdb-${VERSION}*.rpm"
pgdb_install_config_rpms "stat-nbmaster"

# Do not upgrade postgresql; it must be upgraded only as a dependency of foros-pgadm
doc ssh oui-nbmaster0 sudo yum -y -x 'foros-*' -x 'boost-*' upgrade
doc ssh stat-nbmaster sudo yum -y -x 'foros-*' -x 'postgresql*' -x 'boost-*' upgrade

exit 0
