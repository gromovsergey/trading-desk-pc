#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)

. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.jira.sh
. $WORKING_DIR/commons.addb.sh
. $WORKING_DIR/commons.release.sh
. $WORKING_DIR/commons.ui.sh
. $WORKING_DIR/commons.pgdb.sh
. $WORKING_DIR/commons.pgadm.sh
. $WORKING_DIR/commons.bi.sh
. $WORKING_DIR/commons.biadm.sh
. $WORKING_DIR/commons.dstr.sh

COLO="`echo ${bamboo__01_Colocation} | tr '[:upper:]' '[:lower:]'`"
UI_VERSION="`echo ${bamboo__02_UiTag} | tr '[:upper:]' '[:lower:]'`"
PGDB_VERSION="`echo ${bamboo__03_PgDbTag} | tr '[:upper:]' '[:lower:]'`"
ADDB_VERSION="`echo ${bamboo__04_AddbTag} | tr '[:upper:]' '[:lower:]'`"
PGADM_VERSION="`echo ${bamboo__05_PgAdmTag}${bamboo__04_StopPostgreSQL}${bamboo__02_StartPostgreSQL} | tr '[:upper:]' '[:lower:]'`"
BI_VERSION="`echo ${bamboo__06_BiTag} | tr '[:upper:]' '[:lower:]'`"
BIADM_VERSION="`echo ${bamboo__07_BiAdmTag} | tr '[:upper:]' '[:lower:]'`"
DSTR_VERSION="`echo ${bamboo__08_DatastoreTag} | tr '[:upper:]' '[:lower:]'`"
UI_LDAP_USER="oix.project.coordinator"
UI_LDAP_PASSWORD="${bamboo__09_UiReleaser_password}${bamboo__03_UiReleaser_password}"

[ -n "$UI_VERSION" ] && [ "$UI_VERSION" = "skip" ] && UI_VERSION=
[ -n "$PGDB_VERSION" ] && [ "$PGDB_VERSION" = "skip" ] && PGDB_VERSION=
[ -n "$ADDB_VERSION" ] && [ "$ADDB_VERSION" = "skip" ] && ADDB_VERSION=
[ -n "$PGADM_VERSION" ] && [ "$PGADM_VERSION" = "skip" ] && PGADM_VERSION=
[ -n "$PGADM_VERSION" ] && [ "$PGADM_VERSION" = "no" ] && PGADM_VERSION=
[ -n "$BI_VERSION" ] && [ "$BI_VERSION" = "skip" ] && BI_VERSION=
[ -n "$BIADM_VERSION" ] && [ "$BIADM_VERSION" = "skip" ] && BIADM_VERSION=
[ -n "$DSTR_VERSION" ] && [ "$DSTR_VERSION" = "skip" ] && DSTR_VERSION=

[ -z $BAMBOO_SUBTASK ] && { echo "Undefined SUBTASK"; exit 1; }
[ -z "$COLO" ] && { echo "Undefined _01_Colocation" ; exit 1 ; }
[ "$COLO" != "moscow-test-central" ] && \
  [ "$COLO" != "moscow-stage-central" ] && \
  [ "$COLO" != "moscow-emergency-central" ] && { echo ": Unsupported colo: $COLO"; exit 1; }

[ "$COLO" = "moscow-emergency-central" ] && [ -n "$BIADM_VERSION" ] && \
  { echo ": Could not release BIADM to $COLO."; \
    echo ":     $COLO uses pentaho-test host."; \
    echo ":     So release BIADM to moscow-test-central.";  exit 1; }

case $BAMBOO_SUBTASK in
  send_notification)
    echo ": $BAMBOO_SUBTASK:"

    doc setup_ldap_credentials
    doc setup_hosts $COLO

    [ -n "$UI_VERSION" ] && doc setup_next_version "UI" && \
       doc check_tag_version "UI" "$UI_VERSION" && \
       doc check_major_version "foros-ui" ${UI_HOSTS%% *} $UI_VERSION

    [ -n "$PGDB_VERSION" ] && doc setup_next_version "PGDB" && \
       doc check_tag_version "PGDB" "$PGDB_VERSION" && \
       doc check_major_version "foros-pgdb" ${PG_HOSTS%% *} $PGDB_VERSION

    [ -n "$PGADM_VERSION" ] && doc setup_next_version "PGADM" && \
       doc check_tag_version "PGADM" "$PGADM_VERSION" && \
       doc check_major_version "foros-pgadm" ${PG_HOSTS%% *} $PGADM_VERSION

    [ -n "$ADDB_VERSION" ] && doc setup_next_version "ADDB" && \
       doc check_tag_version "ADDB" "$ADDB_VERSION"

    [ -n "$BI_VERSION" ] && doc setup_next_version "BI" && \
       doc check_tag_version "BI" "$BI_VERSION" && \
       doc check_major_version "foros-bi-mondrian-$COLO" ${BI_HOSTS%% *} $BI_VERSION

    [ -n "$BIADM_VERSION" ] && doc setup_next_version "BIADM" && \
       doc check_tag_version "BIADM" "$BIADM_VERSION" && \
       doc check_major_version "foros-biadm" ${BI_HOSTS%% *} $BIADM_VERSION

    [ -n "$DSTR_VERSION" ] && doc setup_next_version "DSTR" && \
       doc check_tag_version "DSTR" "$DSTR_VERSION" && \
       doc check_major_version "foros-config-datastore-$COLO" ${DSTR_HOSTS%% *} $DSTR_VERSION
    doc print_variables

    MESSAGE=""
    [ -n "$UI_VERSION" ] && MESSAGE="$MESSAGE [FOROS-UI $UI_VERSION|`get_jira_filter_url OUI $UI_VERSION`],"
    [ -n "$PGDB_VERSION" ] && MESSAGE="$MESSAGE [FOROS-PGDB $PGDB_VERSION|`get_jira_filter_url PGDB $PGDB_VERSION`], REPL $PGDB_VERSION,"
    [ -n "$ADDB_VERSION" ] && MESSAGE="$MESSAGE [ADDB $ADDB_VERSION|`get_jira_filter_url ADDB $ADDB_VERSION`],"
    [ -n "$PGADM_VERSION" ] && MESSAGE="$MESSAGE [FOROS-PGADM $PGADM_VERSION|`get_jira_filter_url PGADM $PGADM_VERSION`],"
    [ -n "$BI_VERSION" ] && MESSAGE="$MESSAGE [BIDEV $BI_VERSION|`get_jira_filter_url BIDEV $BI_VERSION`],"
    [ -n "$BIADM_VERSION" ] && MESSAGE="$MESSAGE [BIADM $BIADM_VERSION|`get_jira_filter_url BIADM $BIADM_VERSION`],"
    [ -n "$DSTR_VERSION" ] && MESSAGE="$MESSAGE [DSTR $DSTR_VERSION|`get_jira_filter_url DSTR $DSTR_VERSION`],"
    SUMMARY=`echo "$MESSAGE" | sed -e "s#\[##g" -e "s#\]##g" -e "s#|[^,]\+,#,#g"`

    doc create_jira_issue "ENVDEV" "3" "'Deploy ${SUMMARY%?} to $COLO'" "Deployment" "'Deploy ${MESSAGE%?} to $COLO'"

    if [ -n "$CREATED_JIRA_ISSUE" ]; then
      docl mkdir -p "/tmp/OUI-COMMON/release/$COLO/"
      echo $CREATED_JIRA_ISSUE > /tmp/OUI-COMMON/release/$COLO/issue || \
        { echo "Could not save issue number: '$CREATED_JIRA_ISSUE'"; exit 1; }
    fi
    ;;
  send_start_stop_notification)
    echo ": $BAMBOO_SUBTASK:"

    DURATION=${bamboo__02_MaintenanceDuration}
    [ -z "$DURATION" ] && { echo "Undefined _02_MaintenanceDuration"; exit 1; }

    doc setup_ldap_credentials

    start_time=`date -d "+ 5 minutes"`
    if [ "$DURATION" = "unknown" ]; then
      DESCRIPTION="Services will be stopped at $start_time"
    else
      end_time=`date -d "+ 5 minutes + $DURATION"` || { echo "Invalid duration: $DURATION"; exit 1; }
      DESCRIPTION="Planned maintenance window: $start_time - $end_time"
    fi
    doc create_jira_issue "ENVDEV" "3" "'FOROS UI and PGDB will be stopped in $COLO for maintenance, NoDB mode will be set for AdServer'" "Deployment" "'$DESCRIPTION'"

    if [ -n "$CREATED_JIRA_ISSUE" ]; then
      docl mkdir -p "/tmp/OUI-COMMON/release/$COLO/"
      echo $CREATED_JIRA_ISSUE > /tmp/OUI-COMMON/release/$COLO/start-stop-issue || \
        { echo "Could not save issue number: '$CREATED_JIRA_ISSUE'"; exit 1; }
    fi
    ;;
  release_ui_cms)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$UI_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_next_version "UI"
    doc setup_ldap_credentials
    doc print_variables

    doc checkout_project "/svnroot/oix/ui/branches/$UI_BRANCH"
    doc update_foros_ui_svn "$PROJECT_FOLDER" $UI_VERSION
    doc upload_cms_plugin "$PROJECT_FOLDER/cms-plugin"
    ;;
  release_pgdb_cms)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$PGDB_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_next_version "PGDB"
    doc setup_ldap_credentials
    doc print_variables

    doc checkout_project "/svnroot/oix/pgdb/branches/$PGDB_BRANCH"
    doc update_foros_pgdb_svn "$PROJECT_FOLDER" $PGDB_VERSION
    doc upload_cms_plugin "$PROJECT_FOLDER/cms-plugin"
    ;;
  release_pgadm_cms)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$PGADM_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_next_version "PGADM"
    doc setup_ldap_credentials
    doc print_variables

    doc checkout_project "/svnroot/oix/pgadm/branches/$PGADM_BRANCH"
    doc update_foros_pgadm_svn "$PROJECT_FOLDER" $PGADM_VERSION
    doc upload_cms_plugin "$PROJECT_FOLDER/cms-plugin"
    ;;
  release_dstr_cms)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$DSTR_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_next_version "DSTR"
    doc setup_ldap_credentials
    doc print_variables

    doc checkout_project "/svnroot/oix/datastore/branches/$DSTR_BRANCH"
    doc update_foros_dstr_svn "$PROJECT_FOLDER" $DSTR_VERSION
    doc upload_cms_plugin "$PROJECT_FOLDER/cms-plugin"
    ;;
  release_ui_jira)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$UI_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_next_version "UI"
    doc setup_ldap_credentials
    UI_NEXT_VERSION="${UI_VERSION%.*}.$(( ${UI_VERSION##*.} + 1))"
    doc print_variables

    doc release_jira_version "OUI" $UI_VERSION $UI_NEXT_VERSION
    ;;
  release_pgdb_jira)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$PGDB_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_next_version "PGDB"
    doc setup_ldap_credentials
    PGDB_NEXT_VERSION="${PGDB_VERSION%.*}.$(( ${PGDB_VERSION##*.} + 1))"
    doc print_variables

    doc release_jira_version "PGDB" $PGDB_VERSION $PGDB_NEXT_VERSION
    doc release_jira_version "REPL" $PGDB_VERSION $PGDB_NEXT_VERSION
    ;;
  release_pgadm_jira)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$PGADM_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_next_version "PGADM"
    doc setup_ldap_credentials
    PGADM_NEXT_VERSION="${PGADM_VERSION%.*}.$(( ${PGADM_VERSION##*.} + 1))"
    doc print_variables

    doc release_jira_version "PGADM" $PGADM_VERSION $PGADM_NEXT_VERSION
    ;;
  release_addb_jira)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$ADDB_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_next_version "ADDB"
    doc setup_ldap_credentials
    ADDB_NEXT_VERSION="${ADDB_VERSION%.*}.$(( ${ADDB_VERSION##*.} + 1))"
    doc print_variables

    doc release_jira_version "ADDB" $ADDB_VERSION $ADDB_NEXT_VERSION
    ;;
  release_bi_jira)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$BI_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_next_version "BI"
    doc setup_ldap_credentials
    BI_NEXT_VERSION="${BI_VERSION%.*}.$(( ${BI_VERSION##*.} + 1))"
    doc print_variables

    doc release_jira_version "BIDEV" $BI_VERSION $BI_NEXT_VERSION
    ;;
  release_biadm_jira)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$BIADM_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_next_version "BIADM"
    doc setup_ldap_credentials
    BIADM_NEXT_VERSION="${BIADM_VERSION%.*}.$(( ${BIADM_VERSION##*.} + 1))"
    doc print_variables

    doc release_jira_version "BIADM" $BIADM_VERSION $BIADM_NEXT_VERSION
    ;;
  release_dstr_jira)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$DSTR_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_next_version "DSTR"
    doc setup_ldap_credentials
    DSTR_NEXT_VERSION="${DSTR_VERSION%.*}.$(( ${DSTR_VERSION##*.} + 1))"
    doc print_variables

    doc release_jira_version "DSTR" $DSTR_VERSION $DSTR_NEXT_VERSION
    ;;
  build_ui)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$UI_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_next_version "UI"
    doc setup_ldap_credentials
    doc print_variables

    doc create_foros_ui_tags $UI_BRANCH $UI_VERSION
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/oix/ui/tags/$UI_VERSION"
    doc build_project "$COLO" "foros/ui" "$UI_VERSION"
    doc ui_install_rs_client "$UI_VERSION" "$UI_VERSION"
    ;;
  build_pgdb)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$PGDB_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_next_version "PGDB"
    doc setup_ldap_credentials
    doc print_variables

    doc create_foros_pgdb_tags $PGDB_BRANCH $PGDB_VERSION
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/oix/pgdb/tags/$PGDB_VERSION"
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/oix/streams-replication/tags/$PGDB_VERSION"
    doc build_project "$COLO" "foros/pgdb" "$PGDB_VERSION"
    ;;
  build_pgadm)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$PGADM_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_next_version "PGADM"
    doc setup_ldap_credentials
    doc print_variables

    doc create_foros_pgadm_tags $PGADM_BRANCH $PGADM_VERSION
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/oix/pgadm/tags/$PGADM_VERSION"
    doc build_project "$COLO" "foros/pgadm" "$PGADM_VERSION"
    ;;
  build_addb)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$ADDB_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_next_version "ADDB"
    doc setup_ldap_credentials
    doc print_variables

    doc create_foros_addb_tags $ADDB_BRANCH $ADDB_VERSION
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/oix/db/tags/$ADDB_VERSION"
    ;;
  build_bi)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$BI_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_next_version "BI"
    doc setup_ldap_credentials
    doc print_variables

    doc create_foros_bi_tags $BI_BRANCH $BI_VERSION
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/oix/bi/tags/$BI_VERSION"
    doc build_project "$COLO" "foros/bi" "$BI_VERSION"
    ;;
  build_biadm)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$BIADM_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_next_version "BIADM"
    doc setup_ldap_credentials
    doc print_variables

    doc create_foros_bi_tags $BIADM_BRANCH $BIADM_VERSION "biadm"
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/oix/biadm/tags/$BIADM_VERSION"
    doc build_project "$COLO" "foros/biadm" "$BIADM_VERSION"
    ;;
  build_dstr)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$DSTR_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_next_version "DSTR"
    doc setup_ldap_credentials
    doc print_variables

    doc create_foros_dstr_tags $DSTR_BRANCH $DSTR_VERSION
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/oix/datastore/tags/$DSTR_VERSION"
    if [ "$DSTR_BRANCH" != "3.3.0" ]; then
      doc build_project "$COLO" "foros/datastore" "$DSTR_VERSION"
    fi
    ;;
  build_ui_config)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$UI_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_version "UI"
    doc setup_ldap_credentials
    doc print_variables

    doc get_app_version "FOROS-UI" $COLO
    doc update_app_version $APP_NAME $UI_VERSION $COLO
    doc build_and_deploy_config "FOROS-UI" $UI_VERSION $COLO
    docl mkdir -p "/tmp/OUI-COMMON/release/$COLO/"
    [ -z "$CMS_RELEASE" ] && { echo ": $BAMBOO_SUBTASK: undefined CMS_RELEASE"; exit 1; }
    echo $CMS_RELEASE > /tmp/OUI-COMMON/release/$COLO/ui.cms.release
    ;;
  build_pgdb_config)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$PGDB_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_version "PGDB"
    doc setup_ldap_credentials
    doc print_variables

    doc get_app_version "FOROS-PGDB" $COLO
    doc update_app_version $APP_NAME $PGDB_VERSION $COLO
    doc build_and_deploy_config "FOROS-PGDB" $PGDB_VERSION $COLO
    docl mkdir -p "/tmp/OUI-COMMON/release/$COLO/"
    [ -z "$CMS_RELEASE" ] && { echo ": $BAMBOO_SUBTASK: undefined CMS_RELEASE"; exit 1; }
    echo $CMS_RELEASE > /tmp/OUI-COMMON/release/$COLO/pgdb.cms.release
    ;;
  build_pgadm_config)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$PGADM_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_version "PGADM"
    doc setup_ldap_credentials
    doc print_variables

    if [ "$PGADM_BRANCH" = "3.3.0" ]; then
      doc get_app_version "PostgresDB" $COLO
    else
      doc get_app_version "PGADM" $COLO
    fi
    doc update_app_version $APP_NAME $PGADM_VERSION $COLO
    if [ "$PGADM_BRANCH" = "3.3.0" ]; then
      doc build_and_deploy_config "PostgresDB" $PGADM_VERSION $COLO
    else
      doc build_and_deploy_config "PGADM" $PGADM_VERSION $COLO
    fi
    docl mkdir -p "/tmp/OUI-COMMON/release/$COLO/"
    [ -z "$CMS_RELEASE" ] && { echo ": $BAMBOO_SUBTASK: undefined CMS_RELEASE"; exit 1; }
    echo $CMS_RELEASE > /tmp/OUI-COMMON/release/$COLO/pgadm.cms.release
    ;;
  build_dstr_config)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$DSTR_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_version "DSTR"
    doc setup_ldap_credentials
    doc print_variables

    if [ "$DSTR_BRANCH" = "3.3.0" ]; then
      doc get_app_version "FOROS-DS" $COLO
    else
      doc get_app_version "FOROS-DSTR" $COLO
    fi
    doc update_app_version $APP_NAME $DSTR_VERSION $COLO
    if [ "$DSTR_BRANCH" = "3.3.0" ]; then
      doc build_and_deploy_config "FOROS-DS" $DSTR_VERSION $COLO
    else
      doc build_and_deploy_config "FOROS-DSTR" $DSTR_VERSION $COLO
    fi
    docl mkdir -p "/tmp/OUI-COMMON/release/$COLO/"
    [ -z "$CMS_RELEASE" ] && { echo ": $BAMBOO_SUBTASK: undefined CMS_RELEASE"; exit 1; }
    echo $CMS_RELEASE > /tmp/OUI-COMMON/release/$COLO/dstr.cms.release
    ;;
  stop_cluster)
    echo ": $BAMBOO_SUBTASK:"

    doc setup_hosts $COLO
    doc print_variables

    for host in $UI_HOSTS $PG_HOSTS $BI_HOSTS; do
      doc set_zenoss_device_state $MONITORING_HOST $host $ZENOSS_MAINTENANCE_STATE
    done

    if [ -n "$ADDB_VERSION$PGADM_VERSION$PGDB_VERSION$BAMBOO_STOPSERVICES" ] ; then
      doc stop_product "ui" "$COLO" "$UI_HOSTS"
      doc stop_product "pgdb" "$COLO" "$PG_HOSTS"
      doc stop_product "server" "$COLO" "$AD_HOSTS"
      [[ -n "$PGADM_VERSION" ]] && doc stop_product "pgadm" "$COLO" "$PG_HOSTS"
    else
      [[ -n "$UI_VERSION" ]] && doc stop_product "ui" "$COLO" "$UI_HOSTS"
    fi
    [ -n "$BIADM_VERSION" ] && doc stop_product "biadm" "$COLO" "$BI_HOSTS"
    if [ ! -z "$DSTR_VERSION" ]; then
      doc setup_version "DSTR"
      doc stop_product "dstr" "$COLO" "$DSTR_HOSTS"
    fi
    ;;
  update_foros_packages)
    echo ": $BAMBOO_SUBTASK:"

    [ -n "$UI_VERSION" ] && doc setup_version "UI"
    [ -n "$PGDB_VERSION" ] && doc setup_version "PGDB"
    [ -n "$PGADM_VERSION" ] && doc setup_version "PGADM"
    [ -n "$BI_VERSION" ] && doc setup_version "BI"
    [ -n "$BIADM_VERSION" ] && doc setup_version "BIADM"
    [ -n "$DSTR_VERSION" ] && doc setup_version "DSTR"

    doc setup_hosts $COLO

    [ -n "$UI_VERSION" ] && UI_CMS_RELEASE=`cat /tmp/OUI-COMMON/release/$COLO/ui.cms.release`
    [ -n "$PGDB_VERSION" ] && PGDB_CMS_RELEASE=`cat /tmp/OUI-COMMON/release/$COLO/pgdb.cms.release`
    [ -n "$PGADM_VERSION" ] && PGADM_CMS_RELEASE=`cat /tmp/OUI-COMMON/release/$COLO/pgadm.cms.release`
    [ -n "$UI_VERSION" ] && [ -z "$UI_CMS_RELEASE" ] && { echo ": $BAMBOO_SUBTASK: Undefined UI_CMS_RELEASE"; exit 1; }
    [ -n "$PGDB_VERSION" ] && [ -z "$PGDB_CMS_RELEASE" ] && { echo ": $BAMBOO_SUBTASK: Undefined PGDB_CMS_RELEASE"; exit 1; }
    [ -n "$PGADM_VERSION" ] && [ -z "$PGADM_CMS_RELEASE" ] && { echo ": $BAMBOO_SUBTASK: Undefined PGADM_CMS_RELEASE"; exit 1; }
    doc print_variables

    [[ -n "$PGDB_VERSION" ]] && { update_product "pgdb" "$COLO" "$PG_HOSTS" -v "$PGDB_VERSION" -r "$PGDB_CMS_RELEASE" -i ; }
    [[ -n "$UI_VERSION" ]] && { update_product "ui" "$COLO" "$UI_HOSTS" -v "$UI_VERSION" -r "$UI_CMS_RELEASE" -i ; }
    [[ -n "$PGADM_VERSION" ]] && { update_product "pgadm" "$COLO" "$PG_HOSTS" -v "$PGADM_VERSION" -r "$PGADM_CMS_RELEASE" -i ; }

    # BIADM
    [[ -n "$BIADM_VERSION" ]] && { update_product "biadm" "$COLO" "$BI_HOSTS" -v "$BIADM_VERSION" -r "abt" -i ; }

    # BI
    [[ -n "$BI_VERSION" ]] && { update_product "bi" "$COLO" "$BI_HOSTS" -v "$BI_VERSION" -i; }
    [[ -n "$BI_VERSION" ]] && { update_product "bi" "$COLO" "$PG_HOSTS" -v "$BI_VERSION" -i -S ; }

    if [[ -n "$PGDB_VERSION" ]]; then
      doc zenforos_erase $MONITORING_HOST $COLO forospgdb
      update_product "pgdb" "$COLO" "$MONITORING_HOST" -v "$PGDB_VERSION" -r "$PGDB_CMS_RELEASE" -i -z
      doc zenforos_install $MONITORING_HOST $COLO forospgdb
      [ "$COLO" = "moscow-test-central" ] && update_product "pgdb" "$COLO" "$MONITORING_HOST" -v "$PGDB_VERSION" -i -Z
    fi

    if [[ -n "$UI_VERSION" ]]; then
      doc zenforos_erase $MONITORING_HOST $COLO forosui
      update_product "ui" "$COLO" "$MONITORING_HOST" -v "$UI_VERSION" -r "$UI_CMS_RELEASE" -i -z
      doc zenforos_install $MONITORING_HOST $COLO forosui
    fi

    if [[ -n "$PGADM_VERSION" ]]; then
      doc zenforos_erase $MONITORING_HOST $COLO forospgadm
      update_product "pgadm" "$COLO" "$MONITORING_HOST" -v "$PGADM_VERSION" -r "$PGADM_CMS_RELEASE" -i -z
      doc zenforos_install $MONITORING_HOST $COLO forospgadm
    fi

    if [[ -n "$BIADM_VERSION" ]] && [ "$COLO" = "moscow-test-central" ]; then
      update_product "biadm" "$COLO" "$MONITORING_HOST" -v "$BIADM_VERSION" -i -Z
    fi

    if [[ -n "$DSTR_VERSION" ]]; then
      update_product "dstr" "$COLO" "$DSTR_HOSTS" -v "$DSTR_VERSION" -i
      update_product "dstr" "$COLO" "$PG_HOSTS" -v "$DSTR_VERSION" -i -S
    fi
    ;;
  addb_block_schema_refresh)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$ADDB_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }
    [ "$COLO" != "moscow-test-central" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }
    doc print_variables

    doc addb_block_schema_refresh $COLO
    ;;
  addb_stop_standby)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$ADDB_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }
    [ "$COLO" != "moscow-test-central" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }
    doc print_variables

    doc addb_stop_standby $COLO
    ;;
  addb_create_restore_point)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$ADDB_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }
    doc print_variables

    doc addb_create_restore_point $COLO
    ;;
  addb_deploy_schema)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$ADDB_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }
    doc setup_version "ADDB"
    doc print_variables

    doc addb_deploy_schema $COLO $ADDB_VERSION

    # patch Streams Replication using ITS (latest) tag, not ADDB's
    readonly STREAMS_REPLICATION_BRANCH="$ADDB_BRANCH"
    doc get_latests_tag "$STREAMS_REPLICATION_BRANCH" "svn+ssh://svn/home/svnroot/oix/streams-replication/tags"
    readonly STREAMS_REPLICATION_VERSION="$LATESTS_TAG"

    # pass ADDB_VERSION to use apply_patch utility from ADDB's tag
    doc addb_deploy_schema_replication $COLO $ADDB_VERSION $STREAMS_REPLICATION_VERSION
    doc addb_deploy_replication $COLO $ADDB_VERSION $STREAMS_REPLICATION_VERSION
    ;;
  addb_drop_restore_point)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$ADDB_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }
    doc print_variables

    doc addb_drop_restore_point $COLO
    ;;
  addb_start_standby)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$ADDB_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }
    [ "$COLO" != "moscow-test-central" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }
    doc print_variables

    doc addb_start_standby $COLO
    ;;
  addb_unblock_schema_refresh)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$ADDB_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }
    [ "$COLO" != "moscow-test-central" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }
    doc print_variables

    doc addb_unblock_schema_refresh $COLO
    ;;
  update_system_packages)
    echo ": $BAMBOO_SUBTASK:"
    doc setup_hosts $COLO
    doc print_variables

    if [ -n "$UI_VERSION$PGDB_VERSION$PGADM_VERSION" ]; then
      for host in $UI_HOSTS $PG_HOSTS; do
        echo ": : updating system on $host"
        #TODO remove hadoop exclusion after DSTR-133
        doc ssh -o "'BatchMode yes'" $host -- sudo yum -y update \
          -x 'java-1.7.0-oracle' -x 'java-1.7.0-oracle-devel' -x 'hadoop'
      done
    fi

    [ -n "$BIADM_VERSION" ] &&  doc ssh -o "'BatchMode yes'" $BI_HOSTS -- sudo yum -y update \
      -x 'java-1.7.0-oracle' -x 'java-1.7.0-oracle-devel'
    ;;
  start_cluster)
    echo ": $BAMBOO_SUBTASK:"
    doc setup_hosts $COLO
    doc print_variables

    if [ -n "$ADDB_VERSION$PGADM_VERSION$PGDB_VERSION$BAMBOO_STOPSERVICES" ] ; then
      [[ -n "$PGADM_VERSION" ]] && doc start_product "pgadm" "$COLO" "$PG_HOSTS"

      # first of all, start the replication so AdServer won't lock it
      doc start_product "pgdb" "$COLO" "$PG_HOSTS"
      doc start_product "ui" "$COLO" "$UI_HOSTS"
      doc start_product "bi" "$COLO" "$PG_HOSTS"
      doc start_product "server" "$COLO" "$AD_HOSTS"
    else
      [[ -n "$UI_VERSION" ]] && doc start_product "ui" "$COLO" "$UI_HOSTS"
    fi
    [ -z "$ADDB_VERSION$PGADM_VERSION$PGDB_VERSION" ] && [ -n "$BI_VERSION" ] && doc start_product "bi" "$COLO" "$PG_HOSTS"
    if [ -n "$BIADM_VERSION" ]; then
      start_product "biadm" "$COLO" "$BI_HOSTS" || \
        create_jira_issue "BIADM" 1 "Could not start biadm" "-" "Could not start biadm, see [log|$BAMBOO_LINK]"
    fi
    if [ -n "$BI_VERSION" ]; then
      start_product "mondrian" "$COLO" "$BI_HOSTS" || \
        create_jira_issue "BIDEV" 1 "Could not deploy mondrian schemas" "-" "Could not deploy mondrian schemas, see [log|$BAMBOO_LINK]"
    fi

    if [ ! -z "$DSTR_VERSION" ]; then
      doc setup_version "DSTR"
      doc start_product "dstr" "$COLO" "$DSTR_HOSTS"
    fi

    for host in $UI_HOSTS $PG_HOSTS $BI_HOSTS; do
      doc set_zenoss_device_state $MONITORING_HOST $host $ZENOSS_PRODUCTION_STATE
    done

    sleep 60 # 30 sec is the limit for is_oui_alive.py + 30 sec for sure
    doc test_snmp_walk $MONITORING_HOST $COLO $BI_HOSTS
    doc test_snmp_walk $MONITORING_HOST $COLO $PG_HOSTS
    doc test_ui_snmp_walk $MONITORING_HOST $COLO $UI_HOSTS
    ;;
  model_zenoss)
    echo ": $BAMBOO_SUBTASK:"
    MONITORING_HOST="zenoss"
    doc print_variables

    doc zenoss_model_colocation $MONITORING_HOST $COLO
    ;;
  resolve_issue)
    echo ": $BAMBOO_SUBTASK:"
    doc setup_ldap_credentials
    doc print_variables

    if [ -e "/tmp/OUI-COMMON/release/$COLO/issue" ]; then
      JIRA_ISSUE=`cat /tmp/OUI-COMMON/release/$COLO/issue`
      doc resolve_jira_issue $JIRA_ISSUE
      docl rm /tmp/OUI-COMMON/release/$COLO/issue
    fi
    ;;
  resolve_start_stop_issue)
    echo ": $BAMBOO_SUBTASK:"
    doc setup_ldap_credentials
    doc print_variables

    if [ -e "/tmp/OUI-COMMON/release/$COLO/start-stop-issue" ]; then
      JIRA_ISSUE=`cat /tmp/OUI-COMMON/release/$COLO/start-stop-issue`
      doc resolve_jira_issue $JIRA_ISSUE
      docl rm /tmp/OUI-COMMON/release/$COLO/start-stop-issue
    fi
    ;;
  post_release)
    echo ": $BAMBOO_SUBTASK"
    doc setup_ldap_credentials
    doc print_variables

    if [ "$COLO" = "moscow-emergency-central" ]; then
      doc run_plan "Patch" "ODB-PATCHADSERVEREMPTY"
    fi
    ;;
  *)
    echo ": ERROR: unknown stage '$BAMBOO_SUBTASK'"
    exit 1
esac

exit 0
