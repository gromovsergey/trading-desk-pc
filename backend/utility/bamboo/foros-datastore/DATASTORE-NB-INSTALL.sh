#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.dstr.sh

COLOCATION="${bamboo_Colocation}"
HADOOP_HOST="hadoop1"

case $COLOCATION in
  moscow-nb-master)
    STAT_HOST="stat-nbmaster"
    ;;
  *)
    echo "Unknown colocation $COLOCATION"; exit 1;
    ;;
esac

VERSION_FILE=$WORKING_DIR/foros-ui-version
VERSION=`cat_from_store foros-ui-version $VERSION_FILE`

get_from_store "$COLOCATION-dstr" $WORKING_DIR/dstr_rpms

echo ; echo ": $HADOOP_HOST"
dstr_install_hadoop_config_rpms "$HADOOP_HOST"

echo ; echo ": $STAT_HOST"
uninstall_packages "$STAT_HOST" "foros-datastore-hdfs-load"
install_packages_from_repo "$STAT_HOST" "foros-datastore-hdfs-load" "$VERSION"
dstr_install_stat_config_rpms "$STAT_HOST"

exit 0

