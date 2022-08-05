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

selenium_recreate_ora_db() {
  local ora_dbuser="$1"
  local pgdb_branch="$2"

  [ -z "$ora_dbuser" ] && { echo ": $FUNCNAME: undefined ora_dbuser "; exit 1; }
  [ -z "$pgdb_branch" ] && { echo ": $FUNCNAME: undefined pgdb_branch "; exit 1; }

  if echo $ora_dbuser | grep -qE '^NB_COPY'; then
    local host_no=`echo $ora_dbuser | sed -e 's|NB_COPY||g'`
    echo ": $FUNCNAME: copying DB schema $ora_dbuser from trunk"

    doc refresh_oracle_from_nb_master $host_no
    doc patch_schema_replication "ora-nb.ocslab.com" 1521 "addbnba.ocslab.com" $ora_dbuser "trunk"
    doc patch_ora_replication "ora-nb.ocslab.com" 1521 "addbnba.ocslab.com" "S"
    doc patch_schema "ora-nb" 1521 "addbnba.ocslab.com" $ora_dbuser "trunk" "no" "no"

  elif echo $ora_dbuser | grep -qE '^UI_DEV_'; then
    local host_no=`echo $ora_dbuser | sed -e 's|UI_DEV_||g'`
    echo ": $FUNCNAME: copying DB schema $ora_dbuser from test"

    # doc refresh_oracle_from_test $host_no
    local major=`svn cat svn+ssh://svn/home/svnroot/oix/pgdb/$pgdb_branch/version.txt | sed -n -e 's|\([0-9]\+\.[0-9]\+\.[0-9]\+\).*|\1|p'`
    doc patch_schema_replication "oradev.ocslab.com" 1521 "addbtc.ocslab.com" $ora_dbuser "trunk"
    doc patch_ora_replication "oradev.ocslab.com" 1521 "addbtc.ocslab.com" "S"
    doc get_ancestor svn+ssh://svn/home/svnroot/oix/pgdb/$pgdb_branch
    if echo "$SVN_ANCESTOR" | grep -q "trunk"; then
      doc patch_schema "oradev" 1521 "addbtc.ocslab.com" $ora_dbuser "trunk" "no" "no"
    else
      doc patch_schema "oradev" 1521 "addbtc.ocslab.com" $ora_dbuser "branches/$major" "no" "no"
    fi
  else
    echo ": $FUNCNAME: unexpected schema $ora_dbuser"
    exit 1
  fi
}

selenium_recreate_pg_db() {
  local pg_dbhost="$1"
  local pg_dbport="$2"
  local pg_db="$3"
  local pgdb_branch="$4"

  [ -z "$pg_dbport" ] && { echo ": $FUNCNAME: undefined pg_dbport "; exit 1; }
  [ -z "$pg_dbhost" ] && { echo ": $FUNCNAME: undefined pg_dbhost "; exit 1; }
  [ -z "$pg_db" ] && { echo ": $FUNCNAME: undefined pg_db "; exit 1; }
  [ -z "$pgdb_branch" ] && { echo ": $FUNCNAME: undefined pgdb_branch "; exit 1; }

  if echo $pg_db | grep -qE '^nb_copy'; then
    local host_no=`echo $pg_db | sed -e 's|nb_copy||g'`
    echo ": $FUNCNAME: copying DB schema $pg_db from trunk"

    doc refresh_postgres_from_nb_master $host_no
    doc patch_pg_repl $pg_dbhost $pg_dbport $pg_db "oix" "trunk"

    # do not patch statdb here, it will be patched on PGDB start
    # in other case, PGDB start can fail (~5%) with 'fromVersion newer than toVersion' message
    # doc patch_pg_statdb $PG_DBHOST $PG_DBPORT $PG_DB "oix" "trunk"

  elif echo $pg_db | grep -qE '^ui_dev_'; then
    local host_no=`echo $pg_db | sed -e 's|ui_dev_||g'`
    echo ": $FUNCNAME: copying DB schema $pg_db from test"

    local major=`svn cat svn+ssh://svn/home/svnroot/oix/pgdb/$pgdb_branch/version.txt | sed -n -e 's|\([0-9]\+\.[0-9]\+\.[0-9]\+\).*|\1|p'`
    doc refresh_postgres_from_test $host_no
    doc patch_pg_repl $pg_dbhost $pg_dbport $pg_db "oix" "trunk"

    # see a comment above
    # doc patch_pg_statdb $PG_DBHOST $PG_DBPORT $PG_DB "oix" "branches/$MAJOR"
  else
    echo ": $FUNCNAME: unexpected db $pg_db"
    exit 1
  fi
}

