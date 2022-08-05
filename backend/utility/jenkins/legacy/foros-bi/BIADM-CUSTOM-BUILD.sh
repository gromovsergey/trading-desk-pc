#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.biadm.sh

BI_HOST="${bamboo__01_Host}" # stat-dev2 | pentaho-test
BI_SVNPATH="${bamboo__02_SvnPath}" # trunk, branch(e.g.: 3.1.0), tag(e.g.: 3.1.0.0) or dev branch

get_svn_path $BI_SVNPATH
BI_SVNPATH=$SVN_PATH

check_globals BI_HOST BI_SVNPATH

case $BAMBOO_SUBTASK in
  BUILD)
    biadm_custom_build $BI_SVNPATH $BI_HOST
    [ ! -z "$ABT_RPMS" ] && save_artifacts "rpms" $ABT_RPMS
    ;;
  STOP)
    biadm_stop $BI_HOST
    ;;
  UPDATE)
    biadm_update $BI_HOST `abt_get_custom_build_version oix/biadm`
    ;;
  START)
    biadm_start $BI_HOST
    ;;
  *)
    echo "Unknown subtask '$BAMBOO_SUBTASK'"
    exit 1
    ;;
esac

exit 0

