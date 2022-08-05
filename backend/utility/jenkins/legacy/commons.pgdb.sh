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

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$VERSION" ] && { echo ": $FUNCNAME: Undefined VERSION" ; exit 1 ; }
  [ -z "$BRANCH" ] && { echo ": $FUNCNAME: Undefined BRANCH" ; exit 1 ; }

  local PARAMS="
    CMS_ORA_HOST=dummy_ora_host
    CMS_ORA_DB=dummy_ora_db
    CMS_ORA_SCHEMA=dummy_ora_schema
    CMS_ORA_USER=dummy_ora_user
    CMS_PG_HOST=$PG_DBHOST
    CMS_PG_DB=$PG_DB
    CMS_REPLICATION_ENABLED=false
  "

  create_plugin_colocation_xml "$HOST" "FOROS-PGDB" "$VERSION" "$BRANCH" "$PARAMS"
  local result=$?

  return $result
}

pgdb_create_config_rpms() {
  local version=$1
  local branch=$2
  local colo_xml_file=$3

  [ -z "$branch" ] && { echo ": $FUNCNAME: Undefined branch" ; exit 1 ; }
  [ -z "$version" ] && { echo ": $FUNCNAME: Undefined version" ; exit 1 ; }
  [ -z "$colo_xml_file" ] && { echo ": $FUNCNAME: Undefined colo_xml_file" ; exit 1 ; }

  create_config_rpms "FOROS-PGDB" "$version" "$branch" "$colo_xml_file" || \
    { echo ": $FUNCNAME: Could not create config rpms for FOROS-PGDB" ; exit 1 ; }


  rm -rf $WORKING_DIR/pgdb_rpms && \
    mv $WORKING_DIR/rpms $WORKING_DIR/pgdb_rpms || \
    { echo ": $FUNCNAME: Could not move config rpms for FOROS-PGDB" ; exit 1 ; }

  return 0
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
  return 0
}

pgdb_remove_old_packages() {
  local branch="$1"
  local config_branch="$2"
  local host="$3"

  [ -z "$branch" ] && { echo ": $FUNCNAME: undefined branch "; exit 1; }
  [ -z "$config_branch" ] && { echo ": $FUNCNAME: undefined config_branch "; exit 1; }
  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host "; exit 1; }

  if [ "$branch" != "none" ]; then
    uninstall_packages $host `product_get_package_list "pgdb" -c moscow-dev-ui*`
  elif [ "$config_branch" != "none" ]; then
    uninstall_packages $host `product_get_package_list "pgdb" -c moscow-dev-ui* -C`
  fi
  return $?
}

pgdb_custom_build() {
  local pgdb_svnpath="$1"
  local db_svnpath="$2"
  local pgdb_host="$3"

  [ -z "$pgdb_svnpath" ] && { echo ": $FUNCNAME: undefined pgdb_svnpath"; exit 1; }
  [ -z "$db_svnpath" ] && { echo ": $FUNCNAME: undefined db_svnpath "; exit 1; }
  [ -z "$pgdb_host" ] && { echo ": $FUNCNAME: undefined pgdb_host "; exit 1; }
  local centos_release=""

  abt_setup_custom_build_mock_config_with_host "oix/pgdb" $pgdb_host
  centos_release="$CENTOS_RELEASE"

  if [ ! -z "$pgHost" ] && [ ! -z "$pgPort" ] && \
     [ ! -z "$pgDB" ] && [ ! -z "$pgUser" ]; then
    get_pgdb_version $pgHost $pgPort $pgDB $pgUser
  else
    PGDB_VERSION="unknown"
  fi
  echo ": $FUNCNAME: current PGDB_VERSION = $PGDB_VERSION"

  abt_custom_build_with_cache "oix/pgdb" "foros-pgdb" \
    -A "oix/pgdb oix/pgdb/$pgdb_svnpath" \
    -A "oix/db/util oix/db/$db_svnpath/util" \
    -A "oix/streams-replication/utility oix/streams-replication/tags/3.5.0.23/utility" \
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
  echo "checkpoint 3"
  echo "version = $version var_1 = $1"
  echo "host = $host var_2 = $2"

  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host "; exit 1; }
  [ -z "$version" ] && { echo ": $FUNCNAME: undefined version "; exit 1; }

  if is_package_installed "$host" "foros-pgdb" "$version"; then
    echo ": $FUNCNAME: package 'foros-pgdb = $version' is installed to '$host'"
    return 0
  fi

  # Starting from PGDB-1654 part 1 commit, PGDB >= 3.5.0.0 doesn't require Java
  # So, let's install Java when UI builds
  local package=`repo_get_packages -r local "foros-pgdb-$version*.rpm"`
  local java_version=`get_required_java_version $package`
  [ ! -z "$java_version" ] && downgrade_packages "$host" java-1.7.0-oracle-$java_version java-1.7.0-oracle-devel-$java_version

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

  doc apply_pg_sql $pg_dbhost $pg_dbport $pg_db "foros" $WORKING_DIR/jobs_settings.sql
  return 0
}

### PGDB Unit Test framework
pgdb_do_epic() {
  local command="$1"  # install.sh, update.sh etc, see epic SVN folder
  local database="$2" # unittest_db_1

  local _save_=$PGDB_VERSION
  get_pgdb_version "stat-dev0" 5432 "$database"
  local path="branches/$PGDB_VERSION"
  if is_trunk "pgdb" "$PGDB_VERSION" ; then path="trunk" ; fi
  PGDB_VERSION=$_save_

  svn_export_folder "svn+ssh://svn/home/svnroot/oix/pgdb/$path/tests" $WORKING_DIR/"tests"
  export PGPASSFILE=$WORKING_DIR/.pgpass
  echo "stat-dev0:5432:*:oix:adserver" > $PGPASSFILE
  chmod 0600 $PGPASSFILE

  doc $CHECKOUT_FOLDER/$command -h "stat-dev0" -p 5432 -d "$database" -U "oix"
  return 0
}

pgdb_get_package_list() {
  product_get_package_list "pgdb" $@
  return $?
}
