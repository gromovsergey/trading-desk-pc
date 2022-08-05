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
install_packages_from_repo "oui-nbperf0" "foros-ui" "$VERSION"
ui_install_config_rpms "oui-nbperf0"
adclient_install_config_rpms "oui-nbperf0" "foros-creatives"

echo ; echo ": stat-nbperf"
uninstall_packages "stat-nbperf" "foros-pgdb"
install_packages_from_repo "stat-nbperf" "foros-pgdb" "$VERSION"
pgdb_install_config_rpms "stat-nbperf"

echo ; echo ": Upgrade other packages (DC, libNLPIR etc)"
for host in oui-nbperf0 stat-nbperf ; do
  doc ssh $host sudo yum -y \
    -x 'foros-ui' -x 'foros-pgdb' -x 'foros-pgadm' -x 'oui-bi-ui' -x 'oui-bi-pgdb' \
    -x 'java-1.7.0-oracle-devel' -x 'java-1.7.0-oracle' \
    -x 'foros-config-ui-*' -x 'foros-config-pgdb-*' -x 'foros-config-pgadm-*' \
    upgrade
done

exit 0
