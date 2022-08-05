#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.dstr.sh
. $WORKING_DIR/commons.impala.sh

COLOCATION="moscow-nb-oui-at"
STAT_HOST="stat-nbouiat"

VERSION=`ssh -o BatchMode=yes stat-nbmaster rpm -q --qf '%{version}' foros-datastore-hdfs-load`
[ -z "$VERSION" ] && { echo ": $FUNCNAME: undefined VERSION "; exit 1; }

echo ": Building configs"
dstr_download_colocation_xml "$COLOCATION" $VERSION
dstr_create_config_rpms $VERSION "trunk" $COLO_XML_FILE
create_store "$COLOCATION-dstr"
put_to_store "$COLOCATION-dstr" $WORKING_DIR/dstr_rpms

echo ": Stopping the product"
doc stop_product "dstr" "$COLOCATION" "hadoop0"

echo ": Installing packages"
get_from_store "$COLOCATION-dstr" $WORKING_DIR/dstr_rpms
dstr_install_hadoop_config_rpms "hadoop0"

echo ; echo ": $STAT_HOST"
uninstall_packages "$STAT_HOST" "foros-datastore-hdfs-load"
install_packages "$STAT_HOST" "foros-datastore-hdfs-load-${VERSION}*.rpm"
dstr_install_stat_config_rpms "$STAT_HOST"

echo; echo ": Starting the product"
doc start_product "dstr" "$COLOCATION" "hadoop0"

echo; echo ": Install zenforos package"
update_zenforos_plugin $ZENOSS_NB_HOST "dstr" -c "$COLOCATION" -v "$VERSION"
test_snmp_walk $ZENOSS_NB_HOST "$COLOCATION" "$STAT_HOST"
zenoss_model_colocation $ZENOSS_NB_HOST "$COLOCATION/DATASTORE"

exit 0
