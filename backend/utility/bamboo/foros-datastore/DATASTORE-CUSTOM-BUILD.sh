#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.dstr.sh

HOST=${bamboo__01_Host}
SVNPATH=${bamboo__02_SvnPath}
USERNAME=${bamboo__03_Username}
ENTITY_DB=${bamboo__04_EntityDB}

[ -z "$SVNPATH" ] && SVNPATH="trunk"
get_svn_path $SVNPATH
SVNPATH=$SVN_PATH
HADOOP="hadoop1"
COLOCATION="moscow-dev-ui-$HOST"

case $ENTITY_DB in
  same*) PG_DB="$USERNAME"; PG_HOST="stat-dev0"; PG_PORT="5432";;
  stage) PG_DB="stat"; PG_HOST="spostdb0"; PG_PORT="5432";;
  test)  PG_DB="stat_test"; PG_HOST="stat-test"; PG_PORT="5432";;
  nbmaster) PG_DB="nb_trunk_manual"; PG_HOST="stat-nbmaster"; PG_PORT="5432";;
  ui_dev_*) PG_DB="$ENTITY_DB"; PG_HOST="stat-dev0"; PG_PORT="5432";;
  *) echo ": unknown db: $ENTITY_DB"; exit 1;;
esac

check_globals \
  HOST HADOOP SVNPATH USERNAME COLOCATION PG_DB PG_HOST PG_PORT

case $BAMBOO_SUBTASK in
  BUILD)
    dstr_custom_build $SVNPATH $HADOOP
    [ ! -z "$ABT_RPMS" ] && save_artifacts "rpms" $ABT_RPMS
    ;;
  STOP)
    dstr_stop $COLOCATION $HADOOP
    ;;
  UPDATE)
    doc uninstall_packages "$HOST" `dstr_get_package_list -c $COLOCATION -S`
    version=`abt_get_custom_build_version foros/datastore`
    doc install_packages "$HOST" -r local `dstr_get_package_list -v $version -S -i`
    ;;
  CONFIG)
    version=`abt_get_custom_build_version foros/datastore`
    doc dstr_create_colocation_xml $HOST $version $SVNPATH $USERNAME \
      CMS_PG_DB="$PG_DB" CMS_PG_HOST="$PG_HOST" CMS_PG_PORT="$PG_PORT"
    doc dstr_create_config_rpms $version $SVNPATH $COLO_XML_FILE
    doc dstr_install_stat_config_rpms $HOST
    doc dstr_install_hadoop_config_rpms $HADOOP
    ;;
  START)
    doc dstr_start $COLOCATION $HADOOP
    ;;
  *)
    echo "Unknown subtask '$BAMBOO_SUBTASK'"; exit 1;;
esac

exit 0
