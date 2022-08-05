#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.dstr.sh

CLUSTER=${bamboo__01_Cluster}
SVNPATH=${bamboo__02_SvnPath}
VERSION=${bamboo__03_Version}

[ -z "$SVNPATH" ] && SVNPATH="trunk"
[ -z "$CLUSTER" ] && CLUSTER="moscow-hadoop-central"

get_svn_path $SVNPATH
SVNPATH=$SVN_PATH

if [ "$CLUSTER" = "production-uk-central" ]; then
  BUILDONLY="yes"
fi

[ -z "$VERSION" ] && VERSION=`abt_get_cb_version`

check_globals CLUSTER SVNPATH VERSION

case $BAMBOO_SUBTASK in
  BUILD)
    dstradm_custom_build $SVNPATH $VERSION
    [ ! -z "$ABT_RPMS" ] && save_artifacts "rpms" $ABT_RPMS
    abt_save_custom_build_version foros/datastore-adm $VERSION
    ;;
  STOP)
    [ -z "$BUILDONLY" ] && dstradm_stop `dstradm_get_manager_host $CLUSTER`
    ;;
  UPDATE)
    VERSION=`abt_get_custom_build_version foros/datastore-adm`
    [ -z "$BUILDONLY" ] && dstradm_update $CLUSTER $VERSION
    ;;
  START)
    [ -z "$BUILDONLY" ] && dstradm_start `dstradm_get_manager_host $CLUSTER`
    ;;
  *)
    echo "Unknown subtask '$BAMBOO_SUBTASK'"; exit 1;;
esac

exit 0
