#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.ui.sh
. $WORKING_DIR/commons.pgdb.sh
. $WORKING_DIR/commons.adclient.sh

VERSION_FILE=$WORKING_DIR/foros-ui-version
get_from_store "foros-ui-version" $VERSION_FILE
VERSION=`cat $VERSION_FILE`

get_from_store "moscow-nb-master-creatives" $WORKING_DIR/adclient_rpms
get_from_store "moscow-nb-master" $WORKING_DIR/ui_rpms
get_from_store "moscow-nb-master" $WORKING_DIR/pgdb_rpms

echo ; echo ": oui-nbmaster0"
uninstall_packages "oui-nbmaster0" "foros-ui"
install_packages_from_repo "oui-nbmaster0" "foros-ui" "$VERSION"
ui_install_config_rpms "oui-nbmaster0"
adclient_install_config_rpms "oui-nbmaster0" "foros-creatives"

echo ; echo ": stat-nbmaster"
uninstall_packages "stat-nbmaster" "foros-pgdb"
install_packages_from_repo "stat-nbmaster" "foros-pgdb" "$VERSION"
pgdb_install_config_rpms "stat-nbmaster"

echo ; echo ": Upgrade other packages (DC, libNLPIR etc)"
for host in stat-nbmaster oui-nbmaster0 ; do
  doc ssh $host sudo yum -y \
    -x 'foros-ui' -x 'foros-pgdb' -x 'foros-pgadm' -x 'foros-bi-ui' -x 'foros-bi-pgdb' \
    -x 'java-1.7.0-oracle-devel' -x 'java-1.7.0-oracle' \
    -x 'foros-config-ui-*' -x 'foros-config-pgdb-*' -x 'foros-config-pgadm-*' \
    upgrade
done

exit 0
