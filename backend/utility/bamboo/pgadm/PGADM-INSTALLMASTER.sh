#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.pgadm.sh

VERSION_FILE=$WORKING_DIR/foros-postgresdb-version
get_from_store "foros-postgresdb-version" $VERSION_FILE
VERSION=`cat $VERSION_FILE`

get_from_store "moscow-nb-master-postgresdb" $WORKING_DIR/postgresdb_rpms

echo ; echo ": $0: stat-nbmaster"
echo "---- FOROS POSTGRESDB ---------------------------"
uninstall_packages stat-nbmaster "foros-pgadm" "foros-pgdb"
# delete after OUI-24300
uninstall_packages stat-nbmaster "perl-DBD-Oracle"
install_packages_from_repo stat-nbmaster "foros-pgadm" "$VERSION"

echo
echo "---- FOROS POSTGRESDB configuration -------------"
postgresdb_install_config_rpms stat-nbmaster
echo "-----------------------------------------------"

exit 0
