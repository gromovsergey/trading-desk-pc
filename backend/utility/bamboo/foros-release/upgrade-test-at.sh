#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.release.sh

LDAP_USER=${bamboo_login}
LDAP_PASSWORD=${bamboo_password}
[ -z "$LDAP_USER" ] && { echo ": Empty login" ; exit 1 ; }
[ -z "$LDAP_PASSWORD" ] && { echo ": Empty password" ; exit 1 ; }

case $BAMBOO_SUBTASK in
  sync_files)
    echo ": $BAMBOO_SUBTASK:"
    doc print_variables

    doc sync_oui_files "voix0" "oui-attest0" "adkey" "uiuser"
    ;;
  update_cms)
    echo ": $BAMBOO_SUBTASK:"
    doc get_pgdb_version "stat-attest" "5432" "stat" "oix"
    doc print_variables

    for PRODUCT in "FOROS-UI" "FOROS-PGDB" ; do
      echo ": $BAMBOO_SUBTASK: product $PRODUCT"
      SVN=${PRODUCT/-//}
      #doc get_latests_tag $PGDB_VERSION "svn+ssh://svn/home/svnroot/${SVN,,}/tags"
      case $PRODUCT in
        FOROS-UI) LATESTS_TAG=`ssh -o 'BatchMode yes' voix0 rpm -q --qf '%{version}' foros-ui`;;
        FOROS-PGDB) LATESTS_TAG=`ssh -o 'BatchMode yes' stat-test rpm -q --qf '%{version}' foros-pgdb`;;
        *) echo "Unknown product $PRODUCT"; exit 1;;
      esac
      doc get_app_version $PRODUCT "moscow-test-ui-at"
      doc update_app_version $APP_NAME $LATESTS_TAG "moscow-test-ui-at"
      doc build_and_deploy_config $PRODUCT $LATESTS_TAG "moscow-test-ui-at"
    done
    ;;
  stop_cluster)
    echo ": $BAMBOO_SUBTASK:"
    doc print_variables

    for host in "oui-attest0" "oui-attest1" "stat-attest" ; do
      doc set_zenoss_device_state "zenoss" $host $ZENOSS_MAINTENANCE_STATE
    done

    doc ssh uiuser@oui-attest0 "/opt/foros/manager/bin/cmgr stop"
    doc ssh uiuser@stat-attest "/opt/foros/manager/bin/cmgr -f pgdb stop"
    doc ssh uiuser@stat-attest "/opt/foros/manager/bin/cmgr -f pgadm stop"
    ;;
  upgrade_packages)
    echo ": $BAMBOO_SUBTASK:"
    doc print_variables

    echo "Upgrading FOROS UI and PG DB"
    for host in "oui-attest0" "oui-attest1" "stat-attest" ; do
      doc ssh $host "sudo yum -y upgrade -x 'java-1.7.0-oracle' -x 'java-1.7.0-oracle-devel'"
    done

    doc ssh pentaho-test "sudo yum install -y foros-bi-mondrian-moscow-test-ui-at"
    ;;
  refresh_oracle)
    echo ": $BAMBOO_SUBTASK:"
    doc print_variables

    echo ": Refreshing AT TEST colo schema from TEST colo"
    refresh_schema "oradev" 1521 "addbtc.ocslab.com" "AUTOTEST_TC" "
     whenever sqlerror exit failure \n
     connect sys_attc_refresh/.ora_123@oradev/addbtc.ocslab.com \n
     set serveroutput on size 1000000 lines 999 \n
     exec sys.ATTC_REFRESH \n" || exit 1
    ;;
  prepare_oracle)
    echo ": $BAMBOO_SUBTASK:"
    doc print_variables

    doc patch_schema_replication "oradev.ocslab.com" 1521 "addbtc.ocslab.com" "AUTOTEST_TC"
    doc get_ora_version "oracle.ocslab.com" 1521 "addbtest.ocslab.com" "ADSERVER_RO" "forosdb"
    doc patch_schema "oradev.ocslab.com" 1521 "addbtc.ocslab.com" "AUTOTEST_TC" "$ORA_VERSION" "yes" "no"
    ;;
  refresh_postgres)
    echo ": $BAMBOO_SUBTASK:"
    doc print_variables

    # get PGDB version from Test colo (as AT Test is a copy from Test)
    doc get_pgdb_version "stat-test" "5432" "stat_test" "ro"
    doc get_latests_tag $PGDB_VERSION "svn+ssh://svn/home/svnroot/oix/pgdb/tags"
    jobs_settings="svn+ssh://svn/home/svnroot/oix/pgdb/tags/$LATESTS_TAG/cms-plugin/configs/moscow-test-ui-at/jobs_settings.sql"
    doc svn export $jobs_settings $WORKING_DIR/jobs_settings.sql

    echo "Starting PostgreSQL and refresh the database"
    doc ssh uiuser@stat-attest "/opt/foros/manager/bin/cmgr -f pgadm start"

    apply_pg_sql_command "stat-test" "5432" "stat_test" "foros" \
      "select 1 from pg_catalog.pg_class c left join pg_catalog.pg_namespace n on n.oid = c.relnamespace where c.relname = 'jobs' and n.nspname = 'jobs';" \
      -A -t || exit 1
     truncateit=""
    [ "`cat $PG_SQL_FILE.log`" = "1" ] && truncateit="--truncate-tables=jobs.jobs"

    doc copy_statdb "stat-attest.ocslab.com" "stat" "stat-dev0.ocslab.com" "test_copy" \
      "--ora-host=oradev.ocslab.com" "--ora-db=addbtc.ocslab.com" "--ora-port=1521" \
      "--ora-username=AUTOTEST_TC" "--ora-remote-schema=AUTOTEST_TC" $truncateit
    doc truncate_replication_marker "stat-attest.ocslab.com" "stat"

    echo ": $BAMBOO_SUBTASK: fixing jobs settings"
    doc apply_pg_sql "stat-attest" "5432" "stat" "foros" $WORKING_DIR/jobs_settings.sql
    ;;
  prepare_postgres)
    echo ": $BAMBOO_SUBTASK:"
    doc print_variables

    doc ora_replication_init "oradev" 1521 "addbtc.ocslab.com" "AUTOTEST_TC"
    doc pg_replication_init "stat-attest.ocslab.com" 5432 "stat"
    ;;
  update_zenforos)
    echo ": $BAMBOO_SUBTASK:"

    PGDB_VERSION="`ssh stat-attest rpm -q --qf '%{version}' foros-pgdb`"
    UI_VERSION="`ssh oui-attest0 rpm -q --qf '%{version}' foros-ui`"
    doc print_variables

    doc zenforos_erase "zenoss" "moscow-test-ui-at" "forospgdb"
    doc install_packages_from_repo "zenoss" "foros-config-pgdb-moscow-test-ui-at-zenoss" $PGDB_VERSION
    doc zenforos_install "zenoss" "moscow-test-ui-at" "forospgdb"

    doc zenforos_erase "zenoss" "moscow-test-ui-at" "forosui"
    doc install_packages_from_repo "zenoss" "foros-config-ui-moscow-test-ui-at-zenoss" $UI_VERSION
    doc zenforos_install "zenoss" "moscow-test-ui-at" "forosui"
    ;;
  start_cluster)
    echo ": $BAMBOO_SUBTASK:"
    doc print_variables

    doc ssh uiuser@stat-attest "/opt/foros/manager/bin/cmgr -f pgdb start"
    doc ssh uiuser@stat-attest "/opt/foros/manager/bin/cmgr -f pgdb bi start"
    doc ssh uiuser@oui-attest0 "/opt/foros/manager/bin/cmgr start"
    doc ssh bi@pentaho-test "/opt/foros/manager/bin/cmgr -f foros-bi InstallAnalisysDatasources connection=moscow-test-ui-at"

    for host in "oui-attest0" "oui-attest1" "stat-attest"; do
      doc set_zenoss_device_state "zenoss" $host $ZENOSS_PRODUCTION_STATE
    done
    ;;
  model_zenoss)
    echo ": $BAMBOO_SUBTASK:"
    doc print_variables
    doc zenoss_model_colocation "zenoss" "moscow-test-ui-at"
    ;;
  *)
    echo "Unknown subtask $BAMBOO_SUBTASK"
    exit 1
    ;;
esac

exit 0

