#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.dstr.sh

CLUSTER=${bamboo__01_Cluster}
SVNPATH=${bamboo__02_SvnPath}

[ -z "$SVNPATH" ] && SVNPATH="trunk"
[ -z "$CLUSTER" ] && CLUSTER="dev-hadoop-central"
VERSION=`abt_get_cb_version`

get_svn_path $SVNPATH
SVNPATH=$SVN_PATH

check_globals CLUSTER SVNPATH VERSION

case $BAMBOO_SUBTASK in
  BUILD)
    dstradm_custom_build $SVNPATH $VERSION
    [ ! -z "$ABT_RPMS" ] && save_artifacts "rpms" $ABT_RPMS
    abt_save_custom_build_version foros/datastore-adm $VERSION
    ;;
  BUILDC)
    VERSION=`abt_get_custom_build_version oix/datastore-adm`
    dstradm_create_colocation_xml $CLUSTER $VERSION $SVNPATH
    dstradm_create_config_rpms $VERSION $SVNPATH $COLO_XML_FILE
    create_store "$CLUSTER"
    put_to_store "$CLUSTER" $WORKING_DIR/dstradm_rpms
    ;;
  STOP)
    dstradm_stop `dstradm_get_manager_host $CLUSTER`
    ;;
  UPDATE)
    VERSION=`abt_get_custom_build_version oix/datastore-adm`
    get_from_store "$CLUSTER" $WORKING_DIR/dstradm_rpms
    dstradm_update $CLUSTER $VERSION
    dstradm_install_config_rpms $CLUSTER
    ;;
  START)
    doc dstradm_start `dstradm_get_manager_host $CLUSTER`
    ;;
  *)
    echo "Unknown subtask '$BAMBOO_SUBTASK'"; exit 1;;
esac

exit 0
