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
    docl ssh $host "sudo umount /u01/foros/ui/var/sync"
    docl ssh $host "sudo umount /u01/foros/ui/var/www"
  fi

  echo ; echo ": $host"
  uninstall_packages "$host" "foros-ui"
  install_packages "$host" "foros-ui-${VERSION}*.rpm"
  ui_install_config_rpms "$host"

  if [ "$host" = "oui-nbouiat1" ] ; then
    echo ": Mounting remote dirs on $host"
    docl ssh $host "sudo mount /u01/foros/ui/var/sync"
    docl ssh $host "sudo mount /u01/foros/ui/var/www"
  fi
done

echo ; echo ": FOROS UI Creatives on oui-nbouiat0"
adclient_install_config_rpms "oui-nbouiat0" "foros-creatives"

echo ; echo ": stat-nbouiat"
uninstall_packages "stat-nbouiat" "foros-pgdb"
install_packages "stat-nbouiat" "foros-pgdb-${VERSION}*.rpm"
pgdb_install_config_rpms "stat-nbouiat"

# Do not upgrade postgresql; it must be upgraded only as a dependency of foros-pgadm
for host in oui-nbouiat0 oui-nbouiat1 ; do
  doc ssh $host sudo yum -y -x 'foros-*' -x 'boost-*' upgrade
done

doc ssh stat-nbouiat sudo yum -y -x 'foros-*' -x 'postgresql*' -x 'boost-*' upgrade

exit 0
