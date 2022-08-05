#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.ui.sh
. $WORKING_DIR/commons.pgdb.sh
. $WORKING_DIR/commons.adclient.sh

VERSION_FILE=$WORKING_DIR/foros-ui-version
get_from_store "foros-ui-version" $VERSION_FILE
VERSION=`cat $VERSION_FILE`

get_from_store "moscow-nb-oui-at-creatives" $WORKING_DIR/adclient_rpms
get_from_store "moscow-nb-at" $WORKING_DIR/ui_rpms
get_from_store "moscow-nb-at" $WORKING_DIR/pgdb_rpms

for host in oui-nbouiat0 oui-nbouiat1 ; do
  if [ "$host" = "oui-nbouiat1" ] ; then
    echo ": Unmounting remote dirs to install FOROS UI"
    docl ssh $host "sudo umount /u01/foros/ui/var/cache"
    docl ssh $host "sudo umount /u01/foros/ui/var/sync"
    docl ssh $host "sudo umount /u01/foros/ui/var/www"
  fi

  echo ; echo ": $host"
  uninstall_packages "$host" "foros-ui"
  install_packages_from_repo "$host" "foros-ui" "$VERSION"
  ui_install_config_rpms "$host"

  if [ "$host" = "oui-nbouiat1" ] ; then
    echo ": Mounting remote dirs on $host"
    docl ssh $host "sudo mount /u01/foros/ui/var/cache"
    docl ssh $host "sudo mount /u01/foros/ui/var/sync"
    docl ssh $host "sudo mount /u01/foros/ui/var/www"
  fi
done

echo ; echo ": FOROS UI Creatives on oui-nbouiat0"
adclient_install_config_rpms "oui-nbouiat0" "foros-creatives"

echo ; echo ": stat-nbouiat"
uninstall_packages "stat-nbouiat" "foros-pgdb"
install_packages_from_repo "stat-nbouiat" "foros-pgdb" "$VERSION"
pgdb_install_config_rpms "stat-nbouiat"

echo ; echo ": Upgrade other packages (DC, libNLPIR etc)"
for host in oui-nbouiat0 oui-nbouiat1 stat-nbouiat ; do
  doc ssh $host sudo yum -y \
    -x 'foros-ui' -x 'foros-pgdb' -x 'foros-pgadm' -x 'foros-bi-ui' -x 'foros-bi-pgdb' \
    -x 'java-1.7.0-oracle-devel' -x 'java-1.7.0-oracle' \
    -x 'foros-config-ui-*' -x 'foros-config-pgdb-*' -x 'foros-config-pgadm-*' \
    upgrade
done

exit 0

