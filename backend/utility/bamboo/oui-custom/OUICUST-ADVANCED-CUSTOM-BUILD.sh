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

if echo ${bamboo__06_OracleDB} | grep -qE "^UI_DEV_"; then
  ORA_DBHOST="oradev.ocslab.com"
  ORA_DB="addbtc.ocslab.com"
elif echo ${bamboo__06_OracleDB} | grep -qE "^NB_COPY"; then
  ORA_DBHOST="ora-nb.ocslab.com"
  ORA_DB="addbnba.ocslab.com"
fi
ORA_DBUSER=${bamboo__06_OracleDB}
ORA_DBPORT="1521"

if echo ${bamboo__07_PostgresDB} | grep -qE "^nb_copy"; then
  PG_DBHOST="stat-dev0"
elif echo ${bamboo__07_PostgresDB} | grep -qE "^ui_dev_"; then
  PG_DBHOST="stat-dev0"
fi
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

ENABLE_MIGRATIONS=${bamboo__11_Migrations}
ENABLE_REPLICATION=${bamboo__12_Replication}

UNIXCOMMONS_BRANCH=${bamboo__17_UnixcommonsBranch}
JSPWIKI_BRANCH=${bamboo__14_JspWikiBranch}
PATCHDB_BRANCH=${bamboo__15_PatchDBBranch}
REPLICATION_BRANCH=${bamboo__16_ReplicationBranch}

NOTBUILD=""
if [ "$UNIXCOMMONS_BRANCH" = "none" ]; then
  NOTBUILD="unixcommons"
  UNIXCOMMONS_BRANCH="trunk"
fi

if [ "$JSPWIKI_BRANCH" = "none" ]; then
  NOTBUILD="$NOTBUILD jspwiki"
  JSPWIKI_BRANCH="trunk"
else
  get_svn_path $JSPWIKI_BRANCH
  JSPWIKI_BRANCH=$SVN_PATH
fi

get_svn_path $PATCHDB_BRANCH
PATCHDB_BRANCH=$SVN_PATH

get_svn_path $REPLICATION_BRANCH
REPLICATION_BRANCH=$SVN_PATH

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
  if echo $SVN_ANCESTOR | grep -q trunk; then
    BI_BRANCH=trunk
  else
    BI_BRANCH=branches/`echo $SVN_ANCESTOR | sed -n -e "s|^.*/\(.*\$\)|\1|p"`
  fi
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
              ORA_DBHOST ORA_DBPORT ORA_DB ORA_DBUSER \
              PG_DBHOST PG_DBPORT PG_DB \
              SELENIUM_GROUP_LIST SELENIUM_SUITE_LIST SELENIUM_RECREATE_DB \
              ENABLE_MIGRATIONS ENABLE_REPLICATION BAMBOO_MOCK_NO \
              REPLICATION_BRANCH PATCHDB_BRANCH \
              JSPWIKI_BRANCH UNIXCOMMONS_BRANCH SELENIUM_BRANCH NOTBUILD \
              BI_BRANCH REDEPLOY_EAR_ONLY


[ -z $BAMBOO_SUBTASK ] &&  { echo "Undefined BAMBOO_SUBTASK"; exit 1; }
echo ": SUBTASK: $BAMBOO_SUBTASK"
case $BAMBOO_SUBTASK in
  STOP)
    ui_stop ${UI_HOSTS//,/ }
    ui_umount_distributed_fs ${UI_HOSTS//,/ }
    if [ "$REDEPLOY_EAR_ONLY" != "yes" ]; then
      pgdb_stop "$PGDB_HOST"
      ui_remove_old_packages "$UI_BRANCH" "$UI_CONFIG_BRANCH" ${UI_HOSTS//,/ }
      adclient_remove_old_packages "$CREATIVES_CONFIG_BRANCH" ${UI_HOSTS//,/ }
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
    doc pgdb_custom_build "$PGDB_BRANCH" "$PATCHDB_BRANCH" "$REPLICATION_BRANCH" "$PGDB_HOST"
    doc pgdb_update `abt_get_custom_build_version oix/pgdb` "$PGDB_HOST"
    ;;
  BUILDUI)
    [ "$UI_BRANCH" = "none" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    if [ "$REDEPLOY_EAR_ONLY" = "yes" ] ; then
      doc ui_build_ear "$UI_BRANCH" "$WORKING_DIR/foros-ui.ear" "${UI_HOSTS%%,*}"
      doc ui_update_ear "$WORKING_DIR/foros-ui.ear" ${UI_HOSTS//,/ }
    else
      export notBuild="$NOTBUILD"
      doc ui_custom_build "$UI_BRANCH" "$JSPWIKI_BRANCH" "$UNIXCOMMONS_BRANCH" ${UI_HOSTS%%,*}
      doc ui_update `abt_get_custom_build_version oix/ui` "$ENABLE_MIGRATIONS" ${UI_HOSTS//,/ }
    fi
    ;;
  CREATIVESCONFIG)
    [ "$CREATIVES_CONFIG_BRANCH" = "none" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }
    [ "$REDEPLOY_EAR_ONLY" = "yes" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    version=`ssh ${UI_HOSTS%%,*} "rpm -q --qf %{Version} foros-ui"`
    adclient_create_colocation_xml ${UI_HOSTS%%,*} $version $UI_CONFIG_BRANCH
    adclient_create_config_rpms_with_cache $version $CREATIVES_CONFIG_BRANCH $COLO_XML_FILE
    adclient_install_config_rpms ${UI_HOSTS%%,*} "foros-creatives"
    ;;
  CONFIGPGDB)
    [ "$PGDB_CONFIG_BRANCH" = "none" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }
    [ "$REDEPLOY_EAR_ONLY" = "yes" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    version=`ssh $PGDB_HOST "rpm -q --qf %{Version} foros-pgdb"`
    [ "$ENABLE_REPLICATION" = "yes" ] && ENABLE_REPLICATION="true" || ENABLE_REPLICATION="false"
    pgdb_create_colocation_xml $PGDB_HOST $version $PGDB_CONFIG_BRANCH $ENABLE_REPLICATION
    pgdb_create_config_rpms $version $PGDB_CONFIG_BRANCH $COLO_XML_FILE
    pgdb_install_config_rpms $PGDB_HOST
    ;;
  CONFIGUI)
    [ "$UI_CONFIG_BRANCH" = "none" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }
    [ "$REDEPLOY_EAR_ONLY" = "yes" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    version=`ssh ${UI_HOSTS%%,*} "rpm -q --qf %{Version} foros-ui"`
    ui_create_colocation_xml ${UI_HOSTS// /} $version $UI_CONFIG_BRANCH "false"
    ui_create_config_rpms $version $UI_CONFIG_BRANCH $COLO_XML_FILE
    for host in ${UI_HOSTS//,/ }; do
      ui_install_config_rpms $host
      ui_update_static_content_provider $host
      update_saiku_schema $host $PG_DB
    done
    ;;
  BUILDBI)
    [ "$BI_BRANCH" = "none" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }
    [ "$REDEPLOY_EAR_ONLY" = "yes" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    bi_custom_build "$BI_BRANCH" "pentaho-test"
    get_pentaho_connection "$PG_DB"
    bi_update_ui $PENTAHO_CONNECTION `abt_get_custom_build_version oix/bi` "pentaho-test"
    bi_update_pgdb "$PGDB_HOST" `abt_get_custom_build_version oix/bi`
    ;;
  REFRESHDB)
    [ "$REDEPLOY_EAR_ONLY" = "yes" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    if [ "$SELENIUM_RECREATE_DB" = "yes" ]; then
      # doc selenium_recreate_ora_db "$ORA_DBUSER" "$PGDB_BRANCH"
      doc selenium_recreate_pg_db "$PG_DBHOST" "$PG_DBPORT" "$PG_DB" "$PGDB_BRANCH"
    fi
    ;;
  INITREPLICATION)
    [ "$REDEPLOY_EAR_ONLY" = "yes" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    if [ "$ENABLE_REPLICATION" = "yes" ] ; then
      doc patch_ora_replication "$ORA_DBHOST" "$ORA_DBPORT" "$ORA_DB" "$ORA_DBUSER"
      doc ora_replication_init "$ORA_DBHOST" "$ORA_DBPORT" "$ORA_DB" "$ORA_DBUSER"
      doc pg_replication_init "$PG_DBHOST" "$PG_DBPORT" "$PG_DB"
    fi
    ;;
  START)
    if [ "$REDEPLOY_EAR_ONLY" != "yes" ]; then
      mainhost="${UI_HOSTS%%,*}"
      pgdb_setup_jobs "$PGDB_CONFIG_BRANCH" "$PG_DBHOST" "$PG_DBPORT" "$PG_DB" \
        "host=ui-${mainhost##oix-dev}.oix-dev.ocslab.com"
      pgdb_start "$PGDB_HOST"
    fi
    ui_mount_distributed_fs ${UI_HOSTS//,/ }
    ui_start "${UI_HOSTS%%,*}"
    ;;
  STARTBI)
    [ "$BI_BRANCH" = "none" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }
    [ "$REDEPLOY_EAR_ONLY" = "yes" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    bi_install_patches "$PGDB_HOST"
    get_pentaho_connection "$PG_DB"
    bi_install_analisys_datasources "pentaho-test" $PENTAHO_CONNECTION
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
