#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

OUI_UI_BRANCH=${bamboo__1_UISvnPath} # trunk, major(3.0.0), minor(3.1.0.4), branch
OUI_PGDB_BRANCH="${bamboo__2_PGDBSvnPath}" # trunk, major(3.0.0), minor(3.1.0.4), branch
OUI_HOSTS="${bamboo__3_Host}${bamboo__1_Host}" # oix-dev#
OUI_DB="${bamboo__4_Database}" # auto, trunk, test, emergency, branch
OUI_REDEPLOY_EAR="${bamboo__5_RedeployEAROnly}" # yes or no
OUI_BI_BRANCH="${bamboo__6_BISvnPath}" # auto or trunk, major(3.0.0), minor(3.1.0.4), branch
OUI_SELENIUM_GROUP_LIST="${bamboo__5_SeleniumGroupList}${bamboo__2_SeleniumGroupList}"
OUI_SELENIUM_SUITE_LIST="${bamboo__6_SeleniumSuiteList}${bamboo__3_SeleniumSuiteList}"
OUI_SELENIUM_BRANCH="${bamboo__8_SeleniumBranch}${bamboo__4_SeleniumBranch}"

[ -z "$OUI_BI_BRANCH" ] && OUI_BI_BRANCH="auto"

if [ ! -z "${bamboo__2_SeleniumGroupList}" ]; then
  OUI_DB="branch"
  OUI_UI_BRANCH="fake"
  OUI_PGDB_BRANCH="fake"
  OUI_REDEPLOY_EAR="no"
  OUI_BI_BRANCH="fake"
else
  get_svn_path $OUI_UI_BRANCH
  get_ancestor "svn+ssh://svn/home/svnroot/oix/ui/$SVN_PATH"
  OUI_UI_BRANCH_PARENT=`echo $SVN_ANCESTOR | sed -n -e "s|^.*/\(.*\$\)|\1|p"`

  get_svn_path $OUI_PGDB_BRANCH
  get_ancestor "svn+ssh://svn/home/svnroot/oix/pgdb/$SVN_PATH"
  OUI_PGDB_BRANCH_PARENT=`echo $SVN_ANCESTOR | sed -n -e "s|^.*/\(.*\$\)|\1|p"`
fi

if [ "$OUI_PGDB_BRANCH" = "auto" ]; then
  OUI_PGDB_BRANCH="$OUI_UI_BRANCH_PARENT"
fi

if [ "$OUI_DB" = "auto" ]; then
  if [ "$OUI_UI_BRANCH" = "$OUI_PGDB_BRANCH" ]; then
    get_latests_branch 1 "svn+ssh://svn/home/svnroot/oix/ui/branches"
    TEST_BRANCH="$LATESTS_BRANCH"
    get_latests_branch 2 "svn+ssh://svn/home/svnroot/oix/ui/branches"
    EMERGENCY_BRANCH="$LATESTS_BRANCH"
    case $OUI_UI_BRANCH in
      trunk)
        OUI_DB="trunk"
        ;;
      $TEST_BRANCH)
        OUI_DB="test"
        ;;
      $EMERGENCY_BRANCH)
        OUI_DB="emergency"
        ;;
      *)
        OUI_DB="branch"
        ;;
    esac
  else
    OUI_DB="branch"
  fi
fi

[ -z "$OUI_SELENIUM_GROUP_LIST" ] && OUI_SELENIUM_GROUP_LIST="none"
[ -z "$OUI_SELENIUM_SUITE_LIST" ] && OUI_SELENIUM_SUITE_LIST="none"
[ "$OUI_SELENIUM_SUITE_LIST" != "none" ] && \
  [ "$OUI_SELENIUM_GROUP_LIST" != "none" ] && \
  OUI_USE_SELENIUM="yes" || OUI_USE_SELENIUM="no"

PG_DBNAME="ui_dev_"
HOST=`echo ${OUI_HOSTS%%,*}`
case $OUI_DB in
branch)
  DBN=${HOST##oix-dev}
  ;;
trunk)
  DBN=12
  [ "$OUI_USE_SELENIUM" = "yes" ] && DBN=${HOST##oix-dev}
  ;;
test)
  DBN=11
  [ "$OUI_USE_SELENIUM" = "yes" ] && DBN=${HOST##oix-dev}
  ;;
emergency)
  DBN=10
  [ "$OUI_USE_SELENIUM" = "yes" ] && DBN=${HOST##oix-dev}
  ;;
ui_dev_[0-9]*)
  DBN=${OUI_DB##ui_dev_}
  ;;
*)
  echo "Unknown DB: $OUI_DB"
  exit 1
  ;;
esac

export bamboo__01_Hosts="$OUI_HOSTS"
export bamboo__04_PGDBBranch="$OUI_PGDB_BRANCH"
export bamboo__05_PGDBConfigBranch="same"
export bamboo__02_UIBranch="$OUI_UI_BRANCH"
export bamboo__03_UIConfigBranch="same"
export bamboo__6_RedeployEAROnly="$OUI_REDEPLOY_EAR"
export bamboo__07_PostgresDB="${PG_DBNAME}${DBN}"
export bamboo__08_SeleniumGroupList="$OUI_SELENIUM_GROUP_LIST"
export bamboo__10_SeleniumSuiteList="$OUI_SELENIUM_SUITE_LIST"
export bamboo__09_SeleniumRecreateDB="$OUI_USE_SELENIUM"
export bamboo__13_CreativesConfigBranch="trunk"
export bamboo__11_Migrations="no"
export bamboo__15_PatchDBBranch="trunk"
export bamboo__16_ReplicationBranch="trunk"
export bamboo__17_UnixcommonsBranch="none"
export bamboo__19_SeleniumBranch="$OUI_SELENIUM_BRANCH"
export bamboo__20_BIBranch="$OUI_BI_BRANCH"

SCRIPT=$WORKING_DIR/OUICUST-ADVANCED-CUSTOM-BUILD.sh
echo ": running: $SCRIPT"
exec $SCRIPT
