#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.release.sh
. $WORKING_DIR/commons.pgdb.sh
. $WORKING_DIR/commons.ui.sh
. $WORKING_DIR/commons.bi.sh

LDAP_USER=${bamboo_login}
LDAP_PASSWORD=${bamboo_password}
[ -z "$LDAP_USER" ] && { echo ": Empty login" ; exit 1 ; }
[ -z "$LDAP_PASSWORD" ] && { echo ": Empty password" ; exit 1 ; }

case $BAMBOO_SUBTASK in
  sync_files)
    echo ": $BAMBOO_SUBTASK:"
    doc print_variables
    doc sync_oui_files "voix0" "oui-attest0" "uiuser"
    ;;
  stop_cluster)
    echo ": $BAMBOO_SUBTASK:"
    doc print_variables

    for host in "oui-attest0" "oui-attest1" "stat-attest" ; do
      doc set_zenoss_device_state "zenoss" $host $ZENOSS_MAINTENANCE_STATE
    done

    doc stop_product "ui" "moscow-test-ui-at" "oui-attest0"
    doc stop_product "pgdb" "moscow-test-ui-at" "stat-attest"
    doc stop_product "pgadm" "moscow-test-ui-at" "stat-attest"
    ;;
  upgrade_packages)
    echo ": $BAMBOO_SUBTASK:"
    doc print_variables

    echo ": $BAMBOO_SUBTASK: product FOROS-UI"
    UI_VERSION=`ssh -o 'BatchMode yes' voix0 rpm -q --qf '%{version}' foros-ui`
    doc get_app_version "FOROS-UI" "moscow-test-ui-at"
    doc update_app_version $APP_NAME $UI_VERSION "moscow-test-ui-at"
    doc build_and_deploy_config "FOROS-UI" $UI_VERSION "moscow-test-ui-at"
    UI_CMS_RELEASE=$CMS_RELEASE

    echo ": $BAMBOO_SUBTASK: product FOROS-PGDB"
    PGDB_VERSION=`ssh -o 'BatchMode yes' stat-test rpm -q --qf '%{version}' foros-pgdb`
    BI_VERSION=`ssh -o 'BatchMode yes' stat-test rpm -q --qf '%{version}' foros-bi-pgdb`
    doc get_app_version "FOROS-PGDB" "moscow-test-ui-at"
    doc update_app_version $APP_NAME $PGDB_VERSION "moscow-test-ui-at"
    doc build_and_deploy_config "FOROS-PGDB" $PGDB_VERSION "moscow-test-ui-at"
    PGDB_CMS_RELEASE=$CMS_RELEASE

    echo "Waiting 10 min for repository to update index"
    sleep 600

    echo "Upgrading PG DB packages"
    update_product "pgdb" "moscow-test-ui-at" "stat-attest" -v "$PGDB_VERSION" -r "$PGDB_CMS_RELEASE" -i
    update_product "bi" "moscow-test-ui-at" "stat-attest" -v "$BI_VERSION" -i -S

    echo "Upgrading UI  packages"
    update_product "ui" "moscow-test-ui-at" "oui-attest0 oui-attest1" -v "$UI_VERSION" -r "$UI_CMS_RELEASE" -i

    doc ssh pentaho-test "sudo yum install -y foros-bi-mondrian-moscow-test-ui-at"
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
    doc copy_statdb "stat-attest" "stat" "stat-attest" "test_copy"

    echo ": $BAMBOO_SUBTASK: fixing jobs settings"
    doc apply_pg_sql "stat-attest" "5432" "stat" "oix" $WORKING_DIR/jobs_settings.sql
    ;;
  update_zenforos)
    echo ": $BAMBOO_SUBTASK:"

    PGDB_VERSION="`ssh stat-attest rpm -q --qf '%{version}' foros-pgdb`"
    UI_VERSION="`ssh oui-attest0 rpm -q --qf '%{version}' foros-ui`"
    doc print_variables

    doc zenforos_erase "zenoss" "moscow-test-ui-at" "forospgdb"
    doc install_packages "zenoss" "foros-config-pgdb-moscow-test-ui-at-zenoss-${PGDB_VERSION}*.rpm"
    doc zenforos_install "zenoss" "moscow-test-ui-at" "forospgdb"

    doc zenforos_erase "zenoss" "moscow-test-ui-at" "forosui"
    doc install_packages "zenoss" "foros-config-ui-moscow-test-ui-at-zenoss-${UI_VERSION}*.rpm"
    doc zenforos_install "zenoss" "moscow-test-ui-at" "forosui"
    ;;
  start_cluster)
    echo ": $BAMBOO_SUBTASK:"
    doc print_variables

    doc start_product "pgadm" "moscow-test-ui-at" "stat-attest"
    doc start_product "pgdb" "moscow-test-ui-at" "stat-attest"
    doc start_product "ui" "moscow-test-ui-at" "oui-attest0"
    doc start_product "mondrian" "moscow-test-ui-at" "pentaho-test"

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

