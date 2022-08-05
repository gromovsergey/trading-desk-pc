#!/bin/bash

WORKING_DIR=`pwd`
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.ui.build.sh

UI_BRANCH=${bamboo_01_UIBranch}
DO_TESTS=${bamboo_02_DoTests}

[ -z $UI_BRANCH ] && { echo "Undefined UI_BRANCH"; exit 1; }
[ -z $DO_TESTS ] && { echo "Undefined DO_TESTS"; exit 1; }

[ "$DO_TESTS" = "Yes" ] && doc ui_test_rs_client $UI_BRANCH
[ "$UI_BRANCH" != "trunk" ] && doc ui_install_rs_client $UI_BRANCH $UI_BRANCH-SNAPSHOT

exit 0
