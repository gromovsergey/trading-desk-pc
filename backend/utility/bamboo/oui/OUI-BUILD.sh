#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.ui.sh

VERSION_FILE=$WORKING_DIR/foros-ui-version

[ -z $BAMBOO_SUBTASK ] &&  { echo "Undefined BAMBOO_SUBTASK"; exit 1; }
echo ": SUBTASK: $BAMBOO_SUBTASK"

case $BAMBOO_SUBTASK in
  PREPARE)
    doc repo_clean_local_repo

    echo `abt_get_nb_version` > $VERSION_FILE
    create_store "foros-ui-version"
    put_to_store "foros-ui-version" $VERSION_FILE
    ;;
  BUILDPGDB)
    export BAMBOO_MOCK_NO="nb"
    abt_setup_custom_build_mock_config "foros/pgdb" "el7"
    abt_build_trunk "foros/pgdb" `cat_from_store foros-ui-version $VERSION_FILE` -m $ABT_MOCK_CONFIG
    ;;
  BUILDUI)
    export BAMBOO_MOCK_NO="nb"
    abt_setup_custom_build_mock_config "foros/ui" "el7"
    abt_build_trunk "foros/ui" `cat_from_store foros-ui-version $VERSION_FILE` -D '"%rsClientTests true"' -m $ABT_MOCK_CONFIG
    ;;
  BUILDRS)
    doc ui_install_rs_client "trunk" "trunk-SNAPSHOT"
    ;;
  *)
    echo "Unknown subtask $BAMBOO_SUBTASK"
    exit 1
    ;;
esac

exit 0

