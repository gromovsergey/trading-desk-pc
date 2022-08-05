#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.mock.sh
. $WORKING_DIR/commons.ui.sh
. $WORKING_DIR/commons.pgdb.sh
. $WORKING_DIR/commons.bi.sh
. $WORKING_DIR/commons.adclient.sh
. $WORKING_DIR/commons.selenium.sh

UI_HOSTS="${bamboo__01_Hosts}"
PGDB_HOST=`echo ${bamboo__01_Hosts%%,*}`

if [ "`echo ${bamboo__04_PGDBBranch}`" != "none" ]; then
  get_svn_path ${bamboo__04_PGDBBranch}
  PGDB_BRANCH=$SVN_PATH
else
  PGDB_BRANCH="none"
fi

if [ "`echo ${bamboo__05_PGDBConfigBranch}`" = "none" ]; then
  PGDB_CONFIG_BRANCH="none"
elif [ "`echo ${bamboo__05_PGDBConfigBranch}`" = "same" ]; then
  PGDB_CONFIG_BRANCH=$PGDB_BRANCH
else
  get_svn_path ${bamboo__05_PGDBConfigBranch}
  PGDB_CONFIG_BRANCH=$SVN_PATH
fi

if [ "`echo ${bamboo__02_UIBranch}`" != "none" ]; then
  get_svn_path ${bamboo__02_UIBranch}
  UI_BRANCH=$SVN_PATH
else
  UI_BRANCH="none"
fi

if [ "`echo ${bamboo__03_UIConfigBranch}`" = "none" ]; then
  UI_CONFIG_BRANCH="none"
elif [ "`echo ${bamboo__03_UIConfigBranch}`" = "same" ]; then
  UI_CONFIG_BRANCH=$UI_BRANCH
else
  get_svn_path ${bamboo__03_UIConfigBranch}
  UI_CONFIG_BRANCH=$SVN_PATH
fi

PG_DBHOST="stat-dev0"
PG_DB=${bamboo__07_PostgresDB}
PG_DBPORT="5432"

if [ "`echo ${bamboo__08_SeleniumGroupList}`" !=  "none" ]; then
  SELENIUM_GROUP_LIST="${bamboo__08_SeleniumGroupList}"
  SELENIUM_SUITE_LIST="${bamboo__10_SeleniumSuiteList}"
else
  SELENIUM_GROUP_LIST="none"
  SELENIUM_SUITE_LIST="none"
fi
SELENIUM_RECREATE_DB=${bamboo__09_SeleniumRecreateDB}

UNIXCOMMONS_BRANCH=${bamboo__17_UnixcommonsBranch}
PATCHDB_BRANCH=${bamboo__15_PatchDBBranch}

NOTBUILD=""
if [ "$UNIXCOMMONS_BRANCH" = "none" ]; then
  NOTBUILD="unixcommons"
  UNIXCOMMONS_BRANCH="trunk"
fi

get_svn_path $PATCHDB_BRANCH
PATCHDB_BRANCH=$SVN_PATH

CREATIVES_CONFIG_BRANCH=${bamboo__13_CreativesConfigBranch}
[ -z $CREATIVES_CONFIG_BRANCH ] && CREATIVES_CONFIG_BRANCH="trunk"
[ "$CREATIVES_CONFIG_BRANCH" == "none" ] && CREATIVES_CONFIG_BRANCH="trunk"
get_svn_path $CREATIVES_CONFIG_BRANCH
CREATIVES_CONFIG_BRANCH=$SVN_PATH

if [ -z "$NOTBUILD" ]; then
  NOTBUILD=NONE
fi

SELENIUM_BRANCH=${bamboo__19_SeleniumBranch}
[ -z "$SELENIUM_BRANCH" ] && SELENIUM_BRANCH=trunk

BI_BRANCH="${bamboo__20_BIBranch}"
[ -z "$BI_BRANCH" ] && BI_BRANCH="auto"

if [ "$BI_BRANCH" = "auto" ]; then
  get_ancestor "svn+ssh://svn/home/svnroot/oix/pgdb/$PGDB_BRANCH"
  BI_BRANCH=$SVN_ANCESTOR_BRANCH_NAME
elif [ "$BI_BRANCH" != "none" ]; then
  get_svn_path $BI_BRANCH
  BI_BRANCH=$SVN_PATH
fi

REDEPLOY_EAR_ONLY=${bamboo__6_RedeployEAROnly}
[ -z "$REDEPLOY_EAR_ONLY" ] && REDEPLOY_EAR_ONLY=no

# all input parameters
check_globals UI_HOSTS PGDB_HOST \
              PGDB_BRANCH PGDB_CONFIG_BRANCH \
              UI_BRANCH UI_CONFIG_BRANCH \
              CREATIVES_CONFIG_BRANCH \
              PG_DBHOST PG_DBPORT PG_DB \
              SELENIUM_GROUP_LIST SELENIUM_SUITE_LIST SELENIUM_RECREATE_DB \
              BAMBOO_MOCK_NO \
              PATCHDB_BRANCH \
              UNIXCOMMONS_BRANCH SELENIUM_BRANCH NOTBUILD \
              BI_BRANCH REDEPLOY_EAR_ONLY


[ -z $BAMBOO_SUBTASK ] &&  { echo "Undefined BAMBOO_SUBTASK"; exit 1; }
echo ": SUBTASK: $BAMBOO_SUBTASK"
case $BAMBOO_SUBTASK in
  STOP)
    ui_stop ${UI_HOSTS//,/ }
    ui_umount_distributed_fs ${UI_HOSTS//,/ }
    if [ "$REDEPLOY_EAR_ONLY" != "yes" ]; then
      stop_cluster "$PGDB_HOST" "pgdb-moscow"
      ui_remove_old_packages "$UI_BRANCH" "$UI_CONFIG_BRANCH" ${UI_HOSTS//,/ }
      pgdb_remove_old_packages "$PGDB_BRANCH" "$PGDB_CONFIG_BRANCH" "$PGDB_HOST"
    else
      ui_check_installed_version "$UI_BRANCH" ${UI_HOSTS//,/ }
    fi
    ;;
  BUILDPGDB)
    [ "$PGDB_BRANCH" = "none" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }
    [ "$REDEPLOY_EAR_ONLY" = "yes" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    export pgDB="$PG_DB"
    export pgUser="oix"
    export pgPassword="adserver"
    export pgHost="$PG_DBHOST"
    export pgPort="$PG_DBPORT"
    doc pgdb_custom_build "$PGDB_BRANCH" "$PATCHDB_BRANCH" "$PGDB_HOST"
    doc pgdb_update `abt_get_custom_build_version oix/pgdb` "$PGDB_HOST"
    ;;
  BUILDUI)
    [ "$UI_BRANCH" = "none" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    if [ "$REDEPLOY_EAR_ONLY" = "yes" ] ; then
      doc ui_build_ear "$UI_BRANCH" "$WORKING_DIR/foros-ui.ear" "${UI_HOSTS%%,*}"
      doc ui_update_ear "$WORKING_DIR/foros-ui.ear" ${UI_HOSTS//,/ }
    else
      export notBuild="$NOTBUILD"
      doc ui_custom_build "$UI_BRANCH" "$UNIXCOMMONS_BRANCH" ${UI_HOSTS%%,*}
      doc ui_update `abt_get_custom_build_version oix/ui` ${UI_HOSTS//,/ }
    fi
    ;;
  CONFIGPGDB)
    [ "$PGDB_CONFIG_BRANCH" = "none" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }
    [ "$REDEPLOY_EAR_ONLY" = "yes" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    version=`ssh $PGDB_HOST "rpm -q --qf %{Version} foros-pgdb"`
    pgdb_create_colocation_xml $PGDB_HOST $version $PGDB_CONFIG_BRANCH
    pgdb_create_config_rpms $version $PGDB_CONFIG_BRANCH $COLO_XML_FILE
    pgdb_install_config_rpms $PGDB_HOST
    ;;
  CONFIGUI)
    [ "$UI_CONFIG_BRANCH" = "none" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }
    [ "$REDEPLOY_EAR_ONLY" = "yes" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    version=`ssh ${UI_HOSTS%%,*} "rpm -q --qf %{Version} foros-ui"`
    ui_create_colocation_xml ${UI_HOSTS// /} $version $UI_CONFIG_BRANCH
    ui_create_config_rpms $version $UI_CONFIG_BRANCH $COLO_XML_FILE
    for host in ${UI_HOSTS//,/ }; do
      ui_install_config_rpms $host
      ui_update_static_content_provider $host
    done
    ;;
  BUILDBI)
    [ "$BI_BRANCH" = "none" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }
    [ "$REDEPLOY_EAR_ONLY" = "yes" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    bi_custom_build "$BI_BRANCH" "pentaho-dev"
    get_pentaho_connection "$PG_DB"
    bi_update_ui $PENTAHO_CONNECTION `abt_get_custom_build_version oix/bi` "pentaho-dev"
    bi_update_pgdb "$PGDB_HOST" `abt_get_custom_build_version oix/bi`
    ;;
  REFRESHDB)
    [ "$REDEPLOY_EAR_ONLY" = "yes" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    if [ "$SELENIUM_RECREATE_DB" = "yes" ]; then
      doc refresh_postgres "stat-test.ocslab.com" "test_copy" "$PG_DB"
    fi
    ;;
  START)
    if [ "$REDEPLOY_EAR_ONLY" != "yes" ]; then
      mainhost="${UI_HOSTS%%,*}"
      doc pgdb_setup_jobs "$PGDB_CONFIG_BRANCH" "$PG_DBHOST" "$PG_DBPORT" "$PG_DB" \
        "host=ui-${mainhost##oix-dev}.oix-dev.ocslab.com"
      doc start_cluster "$PGDB_HOST" "pgdb-moscow"
    fi
    doc ui_mount_distributed_fs ${UI_HOSTS//,/ }
    doc start_cluster "${UI_HOSTS%%,*}" "ui-moscow"
    ;;
  STARTBI)
    [ "$BI_BRANCH" = "none" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }
    [ "$REDEPLOY_EAR_ONLY" = "yes" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    bi_install_patches "$PGDB_HOST"
    get_pentaho_connection "$PG_DB"
    bi_install_analisys_datasources "pentaho-dev" $PENTAHO_CONNECTION
    ;;
  SELENIUM)
    [ "$SELENIUM_GROUP_LIST" = "none" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc selenium_start_tests "${UI_HOSTS%%,*}" "$PG_DB" "$SELENIUM_BRANCH" "$SELENIUM_GROUP_LIST"  "$SELENIUM_SUITE_LIST"
    ;;
  *)
    echo "Unknown subtask $BAMBOO_SUBTASK"
    exit 1
    ;;
esac

exit 0
