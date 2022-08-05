#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.bi.sh

BI_HOST="${bamboo__01_Host}" # pentaho-dev
BI_SVNPATH="${bamboo__02_SvnPath}" # trunk, branch(e.g.: 3.1.0), tag(e.g.: 3.1.0.0) or dev branch
BI_PGDB_HOST=${bamboo__03_PgdbHost} # oix-dev[0-9] | skip
BI_DB_NAME="${bamboo__04_DB_Name}" # ui_dev_X | auto | skip

get_svn_path $BI_SVNPATH
BI_SVNPATH=$SVN_PATH

[ -z "$BI_PGDB_HOST" ] && BI_PGDB_HOST="skip"
[ -z "$BI_HOST" ] && BI_HOST="pentaho-dev"
[ -z "$BI_DB_NAME" ] && BI_DB_NAME="auto"

if [ "$BI_DB_NAME" == "auto" ]; then
  if [ "$BI_PGDB_HOST" == "skip" ]; then
    BI_DB_NAME="skip"
  else
    pgdb_get_db_name_from_host $BI_PGDB_HOST
    BI_DB_NAME=$PG_DB_NAME
  fi
fi

check_globals BI_HOST BI_SVNPATH BI_PGDB_HOST BI_DB_NAME

case $BAMBOO_SUBTASK in
  BUILD)
    bi_custom_build $BI_SVNPATH $BI_HOST
    [ ! -z "$ABT_RPMS" ] && save_artifacts "rpms" $ABT_RPMS
    ;;
  UPDATEBIUI)
    get_pentaho_connection $BI_DB_NAME
    bi_update_ui $PENTAHO_CONNECTION `abt_get_custom_build_version oix/bi` $BI_HOST
    ;;
  UPDATEBIPGDB)
    bi_update_pgdb $BI_PGDB_HOST `abt_get_custom_build_version oix/bi`
    ;;
  START)
    bi_install_patches $BI_PGDB_HOST
    get_pentaho_connection $BI_DB_NAME
    bi_install_analisys_datasources $BI_HOST $PENTAHO_CONNECTION
    ;;
  CONFIGBI)
    # TODO: remove it
    echo ": DEPRECATED"
    bi_custom_build $BI_SVNPATH $BI_HOST
    get_pentaho_connection $BI_DB_NAME
    bi_update_ui $PENTAHO_CONNECTION `abt_get_custom_build_version oix/bi` $BI_HOST
    bi_update_pgdb $BI_PGDB_HOST `abt_get_custom_build_version oix/bi`
    exit 0
    ;;
  *)
    echo "Unknown subtask '$BAMBOO_SUBTASK'"; exit 1;;
esac

exit 0
