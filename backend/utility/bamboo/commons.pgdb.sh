#!/bin/bash

pgdb_download_colocation_xml() {
  local COLO_NAME=$1
  local VERSION=$2

  [ -z "$VERSION" ] && { echo ": $FUNCNAME: Undefined VERSION" ; exit 1 ; }
  [ -z "$COLO_NAME" ] && { echo ": $FUNCNAME: Undefined COLO_NAME" ; exit 1 ; }

  download_plugin_colocation_xml "$COLO_NAME" "FOROS-PGDB" "$VERSION" "trunk"
  return $?
}

pgdb_create_colocation_xml() {
  local HOST=$1
  local VERSION=$2
  local BRANCH=$3
  local REPLICATION_ENABLED=$4

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$VERSION" ] && { echo ": $FUNCNAME: Undefined VERSION" ; exit 1 ; }
  [ -z "$BRANCH" ] && { echo ": $FUNCNAME: Undefined BRANCH" ; exit 1 ; }
  [ -z "$REPLICATION_ENABLED" ] && { echo ": $FUNCNAME: Undefined REPLICATION_ENABLED" ; exit 1 ; }

  local PARAMS="
    CMS_ORA_HOST=$ORA_DBHOST
    CMS_ORA_DB=$ORA_DB
    CMS_ORA_SCHEMA=$ORA_DBUSER
    CMS_ORA_USER=$ORA_DBUSER
    CMS_PG_HOST=$PG_DBHOST
    CMS_PG_DB=$PG_DB
    CMS_CURRENT_VERSION=$VERSION
    CMS_REPLICATION_ENABLED=$REPLICATION_ENABLED
  "

  create_plugin_colocation_xml "$HOST" "FOROS-PGDB" "$VERSION" "$BRANCH" "$PARAMS"
  local result=$?

  local i
  for i in `seq 13 20`; do
    sed -i "s|UI_DEV_${i}_RW|UI_DEV_${i}|g" $COLO_XML_FILE
  done

  return $result
}

pgdb_create_config_rpms() {
  local VERSION=$1
  local BRANCH=$2
  local COLO_XML_FILE=$3

  [ -z "$BRANCH" ] && { echo ": $FUNCNAME: Undefined BRANCH" ; exit 1 ; }
  [ -z "$VERSION" ] && { echo ": $FUNCNAME: Undefined VERSION" ; exit 1 ; }
  [ -z "$COLO_XML_FILE" ] && { echo ": $FUNCNAME: Undefined COLO_XML_FILE" ; exit 1 ; }

  create_config_rpms "FOROS-PGDB" "$VERSION" "$BRANCH" "$COLO_XML_FILE" || \
    { echo ": $FUNCNAME: Could not create config rpms for FOROS-PGDB" ; exit 1 ; }


  rm -rf $WORKING_DIR/pgdb_rpms && \
    mv $WORKING_DIR/rpms $WORKING_DIR/pgdb_rpms || \
    { echo ": $FUNCNAME: Could not move config rpms for FOROS-PGDB" ; exit 1 ; }
}

pgdb_install_config_rpms() {
  local HOST=$1

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }

  [ -d $WORKING_DIR/pgdb_rpms ] || { echo ": $FUNCNAME: Directory pgdb_rpms not found"; exit 1; }

  local rpms=`find $WORKING_DIR/pgdb_rpms -type f -name "*-mgr*.rpm" | sort`
  rpms="$rpms `find -L "$WORKING_DIR/pgdb_rpms" -name '*.rpm' ! -name '*-mgr*' ! -name '*-zenoss*'`"

  [ -z "$rpms" ] && { echo ": $FUNCNAME: Packages not found in directory"; exit 1; }

  install_packages "$HOST" $rpms

  return 0
}

# set global variable PG_DB_NAME
pgdb_get_db_name_from_host() {
  local host=$1
  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host "; exit 1; }
  local result
  local db_name=`ssh -o BatchMode=yes $host -- ls /opt/foros/pgdb/etc/conf`
  result="$?"

  [ "$result" != "0" ] && { echo ": $FUNCNAME: could not get db_name from $host"; exit $result; }
  [ -z "$db_name" ] && { echo ": $FUNCNAME: could not get db_name from $host"; exit 1; }

  PG_DB_NAME=$db_name
}

pgdb_stop() {
  local host="$1"
  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host "; exit 1; }
  stop_cluster "$host" "pgdb-moscow"
}

pgdb_start() {
  local host="$1"
  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host "; exit 1; }
  start_cluster "$host" "pgdb-moscow"
}

pgdb_remove_old_packages() {
  local branch="$1"
  local config_branch="$2"
  local host="$3"

  [ -z "$branch" ] && { echo ": $FUNCNAME: undefined branch "; exit 1; }
  [ -z "$config_branch" ] && { echo ": $FUNCNAME: undefined config_branch "; exit 1; }
  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host "; exit 1; }

  if [ "$branch" != "none" ]; then
    uninstall_packages $host `pgdb_get_package_list -c moscow-dev-ui*`
  elif [ "$config_branch" != "none" ]; then
    uninstall_packages $host `pgdb_get_package_list -c moscow-dev-ui* -C`
  fi
}

pgdb_custom_build() {
  local pgdb_svnpath="$1"
  local db_svnpath="$2"
  local repl_svnpath="$3"
  local target="$4"

  [ -z "$pgdb_svnpath" ] && { echo ": $FUNCNAME: undefined pgdb_svnpath"; exit 1; }
  [ -z "$db_svnpath" ] && { echo ": $FUNCNAME: undefined db_svnpath "; exit 1; }
  [ -z "$repl_svnpath" ] && { echo ": $FUNCNAME: undefined repl_svnpath "; exit 1; }
  [ -z "$target" ] && target="el6"
  local centos_release=""

  if [ "${target%%[0-9]}" = "el" ]; then
    abt_setup_custom_build_mock_config "oix/pgdb" $target
    centos_release="$target"
  else
    abt_setup_custom_build_mock_config_with_host "oix/pgdb" $target
    centos_release="$CENTOS_RELEASE"
  fi

  if [ ! -z "$pgHost" ] && [ ! -z "$pgPort" ] && \
     [ ! -z "$pgDB" ] && [ ! -z "$pgUser" ]; then
    get_pgdb_version $pgHost $pgPort $pgDB $pgUser
    PGDB_VERSION=`cat $PG_SQL_FILE.log`
  else
    PGDB_VERSION="unknown"
  fi
  echo ": $FUNCNAME: current PGDB_VERSION = $PGDB_VERSION"

  abt_custom_build_with_cache "oix/pgdb" "foros-pgdb" \
    -A "oix/pgdb oix/pgdb/$pgdb_svnpath" \
    -A "oix/db/util oix/db/$db_svnpath/util" \
    -A "oix/streams-replication/utility oix/streams-replication/$repl_svnpath/utility" \
    -F "fake/centos:$centos_release" \
    -F "fake/db_pgdb:$PGDB_VERSION" \
    -c "abt-cb" \
    -m "$ABT_MOCK_CONFIG"
  abt_save_custom_build_version "oix/pgdb" "$ABT_VERSION"
  return $?
}

pgdb_update() {
  local version="$1"
  local host="$2"

  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host "; exit 1; }
  [ -z "$version" ] && { echo ": $FUNCNAME: undefined version "; exit 1; }

  if is_package_installed "$host" "foros-pgdb" "$version"; then
    echo ": $FUNCNAME: package 'foros-pgdb = $version' is installed to '$host'"
    return 0
  fi

  local package=`repo_get_packages -r local "foros-pgdb-$version*.rpm"`
  # Starting from PGDB-1654 part 1 commit, PGDB >= 3.5.0.0 doesn't require Java
  # So, let's install Java when UI builds
  local java_version=`get_required_java_version $package`
  downgrade_packages "$host" java-1.7.0-oracle-$java_version java-1.7.0-oracle-devel-$java_version

  install_packages "$host" -r local "foros-pgdb-$version*.rpm"
  return $?
}

pgdb_setup_jobs() {
  local branch="$1"; shift
  local pg_dbhost="$1"; shift
  local pg_dbport="$1"; shift
  local pg_db="$1"; shift
  local params="$@"


  [ -z "$branch" ] && { echo ": $FUNCNAME: undefined branch "; exit 1; }
  [ -z "$pg_dbhost" ] && { echo ": $FUNCNAME: undefined pg_dbhost "; exit 1; }
  [ -z "$pg_dbport" ] && { echo ": $FUNCNAME: undefined pg_dbport "; exit 1; }
  [ -z "$pg_db" ] && { echo ": $FUNCNAME: undefined pg_db "; exit 1; }

  local jobs_settings="svn+ssh://svn/home/svnroot/oix/pgdb/$branch/cms-plugin/configs/dev/dev-pgdb-jobs-config-template.sql"
  doc svn export $jobs_settings $WORKING_DIR/jobs_settings.sql
  local pair
  for pair in $params; do
    local key="${pair%%=*}"
    local value="${pair##*=}"
    doc "sed -i -e 's|##$key##|$value|g' $WORKING_DIR/jobs_settings.sql"
  done

  doc apply_pg_sql $pg_dbhost $pg_dbport $pg_db "oix" $WORKING_DIR/jobs_settings.sql
  return 0
}

pgdb_get_package_list() {
  product_get_package_list "pgdb" $@
  return $?
}
