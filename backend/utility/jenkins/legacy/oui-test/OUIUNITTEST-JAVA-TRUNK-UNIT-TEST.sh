#!/bin/bash

WORKING_DIR=`pwd`
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.ui.sh

# Scheduled at
# Each Monday, Tuesday, Wednesday, Thursday and Friday every 3 hours from 10:00 am to 8:00 pm

if [ -n "${bamboo__01_UIBranch}" ]; then
  get_svn_path ${bamboo__01_UIBranch}
  UI_BRANCH=$SVN_PATH
else
  UI_BRANCH="trunk"

  # Patch the database before the test
  doc patch_postgres "unittest_ui_12" "trunk" "Skip"
fi

doc ui_unittest oix-dev5 $UI_BRANCH

exit 0
