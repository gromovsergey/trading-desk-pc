#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.ui.sh

[ -z $BAMBOO_SUBTASK ] &&  { echo "Undefined BAMBOO_SUBTASK"; exit 1; }
case $BAMBOO_SUBTASK in
  TRUNK)
    BRANCH=${bamboo_ouiBranch}
    [ -z $BRANCH ] && BRANCH="trunk"
    ;;
  TEST)
    get_latests_branch 1 "svn+ssh://svn/home/svnroot/oix/ui/branches"
    BRANCH="$LATESTS_BRANCH"
    ;;
  EMERGENCY)
    get_latests_branch 2 "svn+ssh://svn/home/svnroot/oix/ui/branches"
    BRANCH="$LATESTS_BRANCH"
    ;;
  *)
    echo "Unknown subtask $BAMBOO_SUBTASK"
    exit 1
    ;;
esac

get_svn_path $BRANCH
BRANCH=$SVN_PATH

ORA_DBHOST="oradev.ocslab.com"
ORA_DB="addbtc.ocslab.com"
ORA_DBPORT="1521"
ORA_DBUSER="UI_DEV_0"

PG_DBHOST="stat-dev0"
PG_DB="ui_dev_0"
PG_DBPORT="5432"

VERSION="5000.5.5."`date +%Y%m%d%H%M%S`
doc ui_create_colocation_xml "oix-dev0" $VERSION $BRANCH
doc ui_create_config_rpms $VERSION $BRANCH $COLO_XML_FILE
