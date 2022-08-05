#!/bin/bash

selenium_start_tests() {
  local host="$1"
  local db="$2"
  local branch="$3"
  local group_list="$4"
  local suite_list="$5"

  [ -z "$group_list" ] && { echo ": $FUNCNAME: undefined group_list "; exit 1; }
  [ -z "$suite_list" ] && { echo ": $FUNCNAME: undefined suite_list "; exit 1; }
  [ -z "$branch" ] && { echo ": $FUNCNAME: undefined branch "; exit 1; }
  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host "; exit 1; }
  [ -z "$db" ] && { echo ": $FUNCNAME: undefined db "; exit 1; }

  local branch_param=""
  [ "$branch" != "trunk" ] && branch_param="BRANCH_NAME=$branch"

  echo ": $FUNCNAME: selenium tests have started on $host with parameters $suite_list"
  ssh selenium-hub.ocslab.com "( REPORT_MAIL_TO=$BAMBOO_USER_MAIL \
    EXECUTER_BAMBOO_USER="$BAMBOO_USER" \
    TEST_ENV="$host" \
    TEST_ENV_DB="$db" \
    TEST_SUITE_LIST="$suite_list" \
    TEST_GROUP_LIST="$group_list" $branch_param \
    ~/nightly-build/foros-ui-test-source/server/nb/src/main/shell/run-tests.sh ~/nightly-build/foros-ui-test-source/server/nb/config/custom-build-st.settings)" 2>&1
}
