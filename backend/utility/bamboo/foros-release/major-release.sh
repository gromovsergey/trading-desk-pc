#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)

. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.jira.sh
. $WORKING_DIR/commons.release.sh
. $WORKING_DIR/commons.ui.sh

COLO="moscow-test-central"
SKIP_UI="`echo ${bamboo__1_SkipUiTag} | tr '[:upper:]' '[:lower:]'`"
SKIP_PGDB="`echo ${bamboo__2_SkipPgDbTag} | tr '[:upper:]' '[:lower:]'`"
SKIP_PGADM="`echo ${bamboo__3_SkipPgAdmTag} | tr '[:upper:]' '[:lower:]'`"
SKIP_BI="`echo ${bamboo__4_SkipBiTag} | tr '[:upper:]' '[:lower:]'`"
SKIP_BIADM="`echo ${bamboo__5_SkipBiAdmTag} | tr '[:upper:]' '[:lower:]'`"
SKIP_DSTR="`echo ${bamboo__6_SkipDatastoreTag} | tr '[:upper:]' '[:lower:]'`"
UI_LDAP_USER="oix.project.coordinator"
UI_LDAP_PASSWORD="${bamboo__7_UiReleaser_password}"

[ "$SKIP_UI" = "no" ] && doc setup_trunk_version "UI"
[ "$SKIP_PGDB" = "no" ] && doc setup_trunk_version "PGDB"
[ "$SKIP_PGADM" = "no" ] && doc setup_trunk_version "PGADM"
[ "$SKIP_BI" = "no" ] && doc setup_trunk_version "BI"
[ "$SKIP_BIADM" = "no" ] && doc setup_trunk_version "BIADM"
[ "$SKIP_DSTR" = "no" ] && doc setup_trunk_version "DSTR"

[ -z $BAMBOO_SUBTASK ] && { echo "Undefined SUBTASK"; exit 1; }

case $BAMBOO_SUBTASK in
  release_ui_cms)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$UI_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_ldap_credentials
    doc print_variables

    doc checkout_project "/svnroot/oix/ui/trunk"
    doc update_foros_ui_svn "$PROJECT_FOLDER" $UI_VERSION
    doc upload_cms_plugin "$PROJECT_FOLDER/cms-plugin"
    ;;
  release_pgdb_cms)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$PGDB_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_ldap_credentials
    doc print_variables

    doc checkout_project "/svnroot/oix/pgdb/trunk"
    doc update_foros_pgdb_svn "$PROJECT_FOLDER" $PGDB_VERSION
    doc upload_cms_plugin "$PROJECT_FOLDER/cms-plugin"
    doc checkout_project "/svnroot/oix/streams-replication/trunk"
    doc update_foros_repl_svn "$PROJECT_FOLDER" $PGDB_VERSION
    ;;
  release_pgadm_cms)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$PGADM_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_ldap_credentials
    doc print_variables

    doc checkout_project "/svnroot/oix/pgadm/trunk"
    doc update_foros_pgadm_svn "$PROJECT_FOLDER" $PGADM_VERSION
    doc upload_cms_plugin "$PROJECT_FOLDER/cms-plugin"
    ;;
  release_dstr_cms)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$DSTR_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_ldap_credentials
    doc print_variables

    doc checkout_project "/svnroot/oix/datastore/trunk"
    doc update_foros_dstr_svn "$PROJECT_FOLDER" $DSTR_VERSION
    doc upload_cms_plugin "$PROJECT_FOLDER/cms-plugin"
    ;;
  release_ui_jira)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$UI_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_ldap_credentials
    UI_NEXT_VERSION="${UI_VERSION%.*}.$(( ${UI_VERSION##*.} + 1))"
    doc setup_next_major_version "UI"
    doc print_variables

    doc release_jira_version "OUI" $UI_VERSION $UI_NEXT_VERSION $UI_NEXT_MAJOR_VERSION
    ;;
  release_pgdb_jira)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$PGDB_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_ldap_credentials
    PGDB_NEXT_VERSION="${PGDB_VERSION%.*}.$(( ${PGDB_VERSION##*.} + 1))"
    doc setup_next_major_version "PGDB"
    doc print_variables

    doc release_jira_version "PGDB" $PGDB_VERSION $PGDB_NEXT_VERSION $PGDB_NEXT_MAJOR_VERSION
    doc release_jira_version "REPL" $PGDB_VERSION $PGDB_NEXT_VERSION $PGDB_NEXT_MAJOR_VERSION
    ;;
  release_pgadm_jira)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$PGADM_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_ldap_credentials
    PGADM_NEXT_VERSION="${PGADM_VERSION%.*}.$(( ${PGADM_VERSION##*.} + 1))"
    doc setup_next_major_version "PGADM"
    doc print_variables

    doc release_jira_version "PGADM" $PGADM_VERSION $PGADM_NEXT_VERSION $PGADM_NEXT_MAJOR_VERSION
    ;;
  release_bi_jira)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$BI_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_ldap_credentials
    BI_NEXT_VERSION="${BI_VERSION%.*}.$(( ${BI_VERSION##*.} + 1))"
    doc setup_next_major_version "BI"
    doc print_variables

    doc release_jira_version "BIDEV" $BI_VERSION $BI_NEXT_VERSION $BI_NEXT_MAJOR_VERSION
    ;;
  release_biadm_jira)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$BIADM_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_ldap_credentials
    BIADM_NEXT_VERSION="${BIADM_VERSION%.*}.$(( ${BIADM_VERSION##*.} + 1))"
    doc setup_next_major_version "BIADM"
    doc print_variables

    doc release_jira_version "BIADM" $BIADM_VERSION $BIADM_NEXT_VERSION $BIADM_NEXT_MAJOR_VERSION
    ;;
  release_dstr_jira)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$DSTR_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_ldap_credentials
    DSTR_NEXT_VERSION="${DSTR_VERSION%.*}.$(( ${DSTR_VERSION##*.} + 1))"
    doc setup_next_major_version "DSTR"
    doc print_variables

    doc release_jira_version "DSTR" $DSTR_VERSION $DSTR_NEXT_VERSION $DSTR_NEXT_MAJOR_VERSION
    ;;
  build_ui)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$UI_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_ldap_credentials
    doc print_variables

    doc create_foros_ui_branch $UI_BRANCH
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/oix/ui/branches/$UI_BRANCH"
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/jspwiki/branches/$UI_BRANCH"

    doc fix_ui_jdbc_test_properties $UI_BRANCH 11
    doc fix_ui_config_parameters_java $UI_BRANCH 11
    doc fix_ui_jdbc_test_properties $UI_PREV_BRANCH 10
    doc fix_ui_config_parameters_java $UI_PREV_BRANCH 10
    
    doc create_foros_ui_tags $UI_BRANCH $UI_VERSION
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/oix/ui/tags/$UI_VERSION"
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/jspwiki/tags/$UI_VERSION"
    doc checkout_project "/svnroot/oix/ui/trunk"
    doc fix_cms_trunk_version "UI" $PROJECT_FOLDER
    doc build_project "$COLO" "foros/ui" "$UI_VERSION"
    doc ui_install_rs_client "$UI_VERSION" "$UI_VERSION"
    ;;
  build_pgdb)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$PGDB_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_ldap_credentials
    doc print_variables

    doc create_foros_pgdb_branch $PGDB_BRANCH
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/oix/pgdb/branches/$PGDB_BRANCH"
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/oix/streams-replication/branches/$PGDB_BRANCH"
    doc fix_pgdb_version_txt $PGDB_BRANCH
    doc create_foros_pgdb_tags $PGDB_BRANCH $PGDB_VERSION
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/oix/pgdb/tags/$PGDB_VERSION"
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/oix/streams-replication/tags/$PGDB_VERSION"
    doc checkout_project "/svnroot/oix/pgdb/trunk"
    doc fix_cms_trunk_version "PGDB" $PROJECT_FOLDER
    doc build_project "$COLO" "foros/pgdb" "$PGDB_VERSION"
    ;;
  build_pgadm)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$PGADM_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_ldap_credentials
    doc print_variables

    doc create_foros_pgadm_branch $PGADM_BRANCH
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/oix/pgadm/branches/$PGADM_BRANCH"
    doc create_foros_pgadm_tags $PGADM_BRANCH $PGADM_VERSION
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/oix/pgadm/tags/$PGADM_VERSION"
    doc checkout_project "/svnroot/oix/pgadm/trunk"
    doc fix_cms_trunk_version "PGADM" $PROJECT_FOLDER
    doc build_project "$COLO" "foros/pgadm" "$PGADM_VERSION"
    ;;
  build_bi)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$BI_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_ldap_credentials
    doc print_variables

    doc checkout_project "/svnroot/oix/bi/trunk"
    doc update_foros_bi_svn "$PROJECT_FOLDER" $BI_VERSION
    doc create_foros_bi_branch $BI_BRANCH
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/oix/bi/branches/$BI_BRANCH"
    doc fix_bi_version_txt $BI_BRANCH
    doc create_foros_bi_tags $BI_BRANCH $BI_VERSION
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/oix/bi/tags/$BI_VERSION"
    doc build_project "$COLO" "foros/bi" "$BI_VERSION"
    ;;
  build_biadm)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$BIADM_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_ldap_credentials
    doc print_variables

    doc create_foros_bi_branch $BIADM_BRANCH "biadm"
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/oix/biadm/branches/$BIADM_BRANCH"
    doc create_foros_bi_tags $BIADM_BRANCH $BIADM_VERSION "biadm"
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/oix/biadm/tags/$BIADM_VERSION"
    doc build_project "$COLO" "foros/biadm" "$BIADM_VERSION"
    ;;
  build_dstr)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$DSTR_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    doc setup_ldap_credentials
    doc print_variables

    doc create_foros_dstr_branch "$DSTR_BRANCH"
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/oix/datastore/branches/$DSTR_BRANCH"
    doc create_foros_dstr_tags $DSTR_BRANCH $DSTR_VERSION
    doc is_svn_path_exists "svn+ssh://svn/home/svnroot/oix/datastore/tags/$DSTR_VERSION"
    doc build_project "$COLO" "foros/datastore" "$DSTR_VERSION"
    ;;

  build_ui_config)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$UI_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    UI_PREV_VERSION="${UI_PREV_BRANCH}.0"
    doc setup_ldap_credentials
    doc print_variables

    doc get_app_version "FOROS-UI" $COLO
    doc update_app_version $APP_NAME $UI_PREV_VERSION $COLO
    doc build_and_deploy_config "FOROS-UI" $UI_PREV_VERSION $COLO
    ;;
  build_pgdb_config)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$PGDB_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    PGDB_PREV_VERSION="${PGDB_PREV_BRANCH}.0"
    doc setup_ldap_credentials
    doc print_variables

    doc get_app_version "FOROS-PGDB" $COLO
    doc update_app_version $APP_NAME $PGDB_PREV_VERSION $COLO
    doc build_and_deploy_config "FOROS-PGDB" $PGDB_PREV_VERSION $COLO
    ;;
  build_pgadm_config)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$PGADM_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    PGADM_PREV_VERSION="${PGADM_PREV_BRANCH}.0"
    doc setup_ldap_credentials
    doc print_variables

    doc get_app_version "PGADM" $COLO
    doc update_app_version $APP_NAME $PGADM_PREV_VERSION $COLO
    doc build_and_deploy_config "PGADM" $PGADM_PREV_VERSION $COLO
    ;;
  build_dstr_config)
    echo ": $BAMBOO_SUBTASK:"
    [ -z "$DSTR_VERSION" ] && { echo ": $BAMBOO_SUBTASK: skipped"; exit 0; }

    DSTR_PREV_VERSION="${DSTR_PREV_BRANCH}.0"
    doc setup_ldap_credentials
    doc print_variables

    doc get_app_version "FOROS-DSTR" $COLO
    doc update_app_version $APP_NAME $DSTR_PREV_VERSION $COLO
    doc build_and_deploy_config "DSTR" $DSTR_PREV_VERSION $COLO
    ;;

  *)
    echo ": ERROR: unknown stage '$BAMBOO_SUBTASK'"
    exit 1
esac

exit 0
