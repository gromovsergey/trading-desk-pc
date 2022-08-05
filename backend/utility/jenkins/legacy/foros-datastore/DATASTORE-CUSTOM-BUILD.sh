#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.dstr.sh

HOST=${bamboo__01_Host}
SVNPATH=${bamboo__02_SvnPath}
CONFIG_ONLY=`echo ${bamboo__03_ConfigOnly:0:1} | tr [a-z] [A-Z]`
DO_START=`echo ${bamboo__04_DoStart:0:1} | tr [a-z] [A-Z]`

[ -z "$SVNPATH" ] && SVNPATH="trunk"
get_svn_path $SVNPATH
SVNPATH=$SVN_PATH
HADOOP_HOST="hadoop0"
COLOCATION="moscow-dev-ui-$HOST"

PG_HOST="stat-dev0"
PG_PORT="5432"
PG_DB=ui_dev_${HOST##oix-dev}

check_globals \
  HOST HADOOP_HOST SVNPATH COLOCATION PG_HOST PG_PORT PG_DB

if [ "$CONFIG_ONLY" = "N" ] ; then
    dstr_custom_build $SVNPATH $HADOOP_HOST
    [ ! -z "$ABT_RPMS" ] && save_artifacts "rpms" $ABT_RPMS

    doc stop_product "dstr" "$COLOCATION" "$HADOOP_HOST"

    echo ": Get list of RPMs to uninstall in colo $COLOCATION"
    uninstall_rpms=`dstr_get_package_list -c $COLOCATION -S`
    doc uninstall_packages "$HOST" "$uninstall_rpms"

    version=`abt_get_custom_build_version oix/datastore`
    echo ": foros/datastore version is $version"
    install_rpms=`dstr_get_package_list -v $version -S -i`
    doc install_packages "$HOST" -r local "$install_rpms"
fi

version=`abt_get_custom_build_version oix/datastore`
doc dstr_create_colocation_xml $HOST $version $SVNPATH $PG_DB \
  CMS_PG_DB="$PG_DB" CMS_PG_HOST="$PG_HOST" CMS_PG_PORT="$PG_PORT"
doc dstr_create_config_rpms $version $SVNPATH $COLO_XML_FILE
doc dstr_install_hadoop_config_rpms $HADOOP_HOST

[ "$CONFIG_ONLY" = "N" ] && doc dstr_install_stat_config_rpms $HOST
[ "$DO_START" = "Y" ] && doc start_product "dstr" "$COLOCATION" "$HADOOP_HOST"

exit 0
