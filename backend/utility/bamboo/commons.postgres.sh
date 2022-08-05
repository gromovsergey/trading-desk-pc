#!/bin/bash

. $WORKING_DIR/commons.util.sh
. $WORKING_DIR/commons.svn.sh
. $WORKING_DIR/commons.cluster.sh

### Patch a Postgres Database (Replication project)
patch_pg_repl() {
  local HOST=$1
  local PORT=$2
  local DB=$3
  local USER=$4
  local TO_VERSION=$5

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }
  [ -z "$USER" ] && { echo ": $FUNCNAME: Undefined USER" ; exit 1 ; }
  [ -z "$TO_VERSION" ] && TO_VERSION="trunk"

  [ "$TO_VERSION" = "S" ] && return 0
  [ "$TO_VERSION" != "trunk" ] && { echo "Could not patch replication to $TO_VERSION. To trunk only."; return 0; }

execute_remote stat-dev0 $HOST $PORT $DB $USER $TO_VERSION <<-"EOF"
  WORKING_DIR=$(cd `dirname $0`; pwd)
  . $WORKING_DIR/commons.svn.sh

  HOST="$1"
  PORT="$2"
  DB="$3"
  USER="$4"
  TO_VERSION="$5"

  svn_export_folder "svn+ssh://svn.ocslab.com/home/svnroot/oix/db/trunk/util/patch_db"
  PATCH_DB_DIR=$CHECKOUT_FOLDER

  echo ": Patching -h $HOST -d $DB -U $USER, product = replication till $TO_VERSION"
  $PATCH_DB_DIR/apply_patch.php -i "/home/maint/.ssh/id_rsa" \
    -statdb \
    -r svn+ssh://svn.ocslab.com/home/svnroot/oix/streams-replication \
    -to $TO_VERSION -host $HOST -port $PORT -db $DB -u $USER \
    -p adserver -prefix statdb -product replication \
    --compareRevisions=no \
    -l $WORKING_DIR/patch_pg_repl.$DB.log || exit 1

  exit 0
EOF
  return $?
}

### Patch a Postgres Database
### TO_VERSION - trunk or branches/X.X.X or tags/X.X.X.X or branches/dev/PGDB-XXXX
patch_pg_statdb() {
  local HOST=$1
  local PORT=$2
  local DB=$3
  local USER=$4
  local TO_VERSION=$5

  [ "$TO_VERSION" = "S" ] && return 0

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }
  [ -z "$USER" ] && { echo ": $FUNCNAME: Undefined USER" ; exit 1 ; }
  [ -z "$TO_VERSION" ] && TO_VERSION="trunk"

  # lock database as there can be running jobs (that lead to deadlocks)
  lock_postgres_db $HOST $DB

execute_remote stat-dev0 $HOST $PORT $DB $USER $TO_VERSION <<-"EOF"
  WORKING_DIR=$(cd `dirname $0`; pwd)
  . $WORKING_DIR/commons.svn.sh

  HOST="$1"
  PORT="$2"
  DB="$3"
  USER="$4"
  TO_VERSION="$5"

  svn_export_folder "svn+ssh://svn.ocslab.com/home/svnroot/oix/db/trunk/util/patch_db"
  PATCH_DB_DIR=$CHECKOUT_FOLDER

  grep -q "compareRevisions" $PATCH_DB_DIR/apply_patch.php && compare_revisions="--compareRevisions=no"
  ARGS="-statdb $compare_revisions -r svn+ssh://svn/home/svnroot/oix/pgdb -to $TO_VERSION -host $HOST -port $PORT -db $DB -u $USER -p adserver -l $WORKING_DIR/patch_pg_statdb.$DB.log"

  echo ": $PATCH_DB_DIR/apply_patch.php $ARGS"
  $PATCH_DB_DIR/apply_patch.php -i "/home/maint/.ssh/id_rsa" $ARGS || exit 1

  exit 0
EOF
  result=$?

  # unlock database
  unlock_postgres_db $HOST $DB

  return $result
}

### Patch BI space
patch_pg_bi() {
  local HOST=$1
  local PORT=$2
  local DB=$3
  local USER=$4
  local TO_VERSION=$5

  [ "$TO_VERSION" = "S" ] && return 0

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }
  [ -z "$USER" ] && { echo ": $FUNCNAME: Undefined USER" ; exit 1 ; }
  [ -z "$TO_VERSION" ] && TO_VERSION="trunk"

execute_remote stat-dev0 $HOST $PORT $DB $USER $TO_VERSION <<-"EOF"
  WORKING_DIR=$(cd `dirname $0`; pwd)
  . $WORKING_DIR/commons.svn.sh

  HOST="$1"
  PORT="$2"
  DB="$3"
  USER="$4"
  TO_VERSION="$5"

  svn_export_folder "svn+ssh://svn.ocslab.com/home/svnroot/oix/db/trunk/util/patch_db"
  PATCH_DB_DIR=$CHECKOUT_FOLDER

  grep -q "compareRevisions" $PATCH_DB_DIR/apply_patch.php && compare_revisions="--compareRevisions=no"
  ARGS="-statdb $compare_revisions -product bi -r svn+ssh://svn/home/svnroot/oix/bi -to $TO_VERSION -host $HOST -port $PORT -db $DB -u $USER -p adserver -l $WORKING_DIR/patch_pg_bi.$DB.log"

  echo ": $PATCH_DB_DIR/apply_patch.php $ARGS"
  $PATCH_DB_DIR/apply_patch.php -i "/home/maint/.ssh/id_rsa" $ARGS || exit 1

  exit 0
EOF
return $?
}

### Patch a PostgreSQL database
patch_postgres() {
  local DB=$1                # ui_dev_0..14 or unittest_ui_0..9
  local TO_STATDB_VERSION=$2 # trunk or [SVN_BRANCH_NAME] or S[kip]
  local TO_REPL_VERSION=$3   # trunk or [SVN_BRANCH_NAME] or S[kip]
  local TO_BI_VERSION=$4     # trunk or [SVN_BRANCH_NAME] or S[kip]

  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }
  [ -z "$TO_STATDB_VERSION" ] && { echo ": $FUNCNAME: Undefined TO_STATDB_VERSION" ; exit 1 ; }
  [ -z "$TO_REPL_VERSION" ] && TO_REPL_VERSION="S"
  [ -z "$TO_BI_VERSION" ] && TO_BI_VERSION="S"
  [ "$TO_REPL_VERSION" != "S" ] && TO_REPL_VERSION="trunk"

  local PG_HOST=stat-dev0

  if [ "$TO_REPL_VERSION" != "S" ] ; then
    get_svn_path "$TO_REPL_VERSION"
    doc patch_pg_repl $PG_HOST 5432 $DB "oix" "$SVN_PATH"
  fi

  if [ "$TO_STATDB_VERSION" != "S" ] ; then
    get_svn_path "$TO_STATDB_VERSION"
    if echo $SVN_PATH | grep -qE '^tags'; then
      doc patch_pg_statdb $PG_HOST 5432 "$DB" "oix" "$TO_STATDB_VERSION"
    else
      doc patch_pg_statdb $PG_HOST 5432 "$DB" "oix" "$SVN_PATH"
    fi
  fi

  if [ "$TO_BI_VERSION" != "S" ] ; then
    get_svn_path "$TO_BI_VERSION"
    if echo $SVN_PATH | grep -qE '^tags'; then
      doc patch_pg_bi $PG_HOST 5432 "$DB" "bi" "$TO_BI_VERSION"
    else
      doc patch_pg_bi $PG_HOST 5432 "$DB" "bi" "$SVN_PATH"
    fi
  fi

  return 0
}

### Returns current version of the databases
### Looks into SCHEMA_APPLIED_PATCHES table and get the latest BUILD_NUM from it
### Returns
###    PGDB_VERSION (for example, '3.0.0')
get_pgdb_version() {
  local HOST=$1
  local PORT=$2
  local DB=$3
  local USER=$4

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }
  [ -z "$USER" ] && { echo ": $FUNCNAME: Undefined USER" ; exit 1 ; }

  apply_pg_sql_command "$HOST" "$PORT" "$DB" "$USER" \
    "select build_num from schema_applied_patches where product='statdb' order by apply_date desc limit 1;" \
    -A -t || exit 1

  PGDB_VERSION=`cat $PG_SQL_FILE.log`
  PGDB_VERSION=${PGDB_VERSION%.*}

  return 0
}

get_pgdb_bi_version() {
  local HOST=$1
  local PORT=$2
  local DB=$3
  local USER=$4

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }
  [ -z "$USER" ] && { echo ": $FUNCNAME: Undefined USER" ; exit 1 ; }

  apply_pg_sql_command "$HOST" "$PORT" "$DB" "$USER" \
    "select build_num from schema_applied_patches where product='bi' order by apply_date desc limit 1;" \
    -A -t || exit 1

  BI_VERSION=`cat $PG_SQL_FILE.log`
  BI_VERSION=${BI_VERSION%.*}

  return 0
}

### Execute given SQL file an a Postgres database using PSQL
apply_pg_sql() {
  local HOST=$1 ; shift
  local PORT=$1 ; shift
  local DB=$1; shift
  local USER=$1; shift
  local SQL_FILE=$1; shift
  local PARAMS=$@

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }
  [ -z "$USER" ] && { echo ": $FUNCNAME: Undefined USER" ; exit 1 ; }
  [ -z "$SQL_FILE" ] && { echo ": $FUNCNAME: Undefined SQL_FILE" ; exit 1 ; }

  export PGPASSFILE=$WORKING_DIR/.pgpass
  echo $HOST":"$PORT":*:"$USER":adserver" > $PGPASSFILE
  chmod 0600 $PGPASSFILE

  echo ": psql -h "$HOST" -p "$PORT" -d "$DB" -U "$USER" -f "$SQL_FILE" "$PARAMS
  psql -h $HOST -p $PORT -d $DB -U $USER -f $SQL_FILE $PARAMS &> $SQL_FILE.log
  local res="$?"

  # if the given SQL file contain several commands (like jobs_settings.sql)
  # then return code can be 0 while the last command fails
  # need the additional check
  if [ "$res" = "0" ] ; then
    cat $SQL_FILE.log | grep 'ERROR:'
    [ "$?" = "0" ] && res=1
  fi

  if [ "$res" != "0" ] ; then
    cat $SQL_FILE
    echo ": Output"
    cat $SQL_FILE.log
    return 1
  fi

  cat $SQL_FILE.log
  return 0
}

### Executes given SQL command on an PostgreSQL database
### To get rid of header etc, set PARAMS="-A -t"
### Returns
###   $PG_SQL_FILE.log - a file with results (of SELECT, for example)
PG_SQL_COMMAND_COUNT=0
apply_pg_sql_command() {
  local HOST=$1 ; shift
  local PORT=$1 ; shift
  local DB=$1; shift
  local USER=$1; shift
  local SQL_CMD=$1; shift
  local PARAMS=$@

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }
  [ -z "$USER" ] && { echo ": $FUNCNAME: Undefined USER" ; exit 1 ; }
  [ -z "$SQL_CMD" ] && { echo ": $FUNCNAME: Undefined SQL_CMD" ; exit 1 ; }

  (( PG_SQL_COMMAND_COUNT += 1 ))
  PG_SQL_FILE=$WORKING_DIR/pg_command$PG_SQL_COMMAND_COUNT.sql
  echo "\set ON_ERROR_STOP 1" > $PG_SQL_FILE
  echo -e "$SQL_CMD" >> $PG_SQL_FILE
  echo "\q" >> $PG_SQL_FILE

  apply_pg_sql $HOST $PORT $DB $USER $PG_SQL_FILE $PARAMS
  return $?
}

### Executes the given sql file as "uiuser" on the given PostgreSQL host
apply_pg_dba_sql() {
  local HOST=$1 ; shift
  local PORT=$1 ; shift
  local DB=$1; shift
  local SQL_FILE=$1; shift
  local PARAMS=$@

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }
  [ -z "$SQL_FILE" ] && { echo ": $FUNCNAME: Undefined SQL_FILE" ; exit 1 ; }

  (( PG_SQL_COMMAND_COUNT += 1 ))
  PG_SQL_FILE=$WORKING_DIR/pg_dba_sql$PG_SQL_COMMAND_COUNT.sql
  echo "\set ON_ERROR_STOP 1" > $PG_SQL_FILE
  cat $SQL_FILE >> $PG_SQL_FILE
  echo "\q" >> $PG_SQL_FILE

  execute_remote_ex "uiuser" $HOST "*.sh *.sql" $PORT $DB $PG_SQL_FILE $PARAMS <<-"EOF"
    WORKING_DIR=`pwd`
    . $WORKING_DIR/commons.sh

    PORT=$1 ; shift
    DB=$1; shift
    PG_SQL_FILE=$1; shift
    PARAMS=$@

    echo ": psql -p "$PORT" -d "$DB" -f "$PG_SQL_FILE" "$PARAMS
    psql -p $PORT -d $DB -f $PG_SQL_FILE $PARAMS &> $PG_SQL_FILE.log
    res="$?"
    cat $PG_SQL_FILE.log

    if [ "$res" != "0" ] ; then
      doc cat $PG_SQL_FILE
      exit 1
    fi
EOF
  [ "$?" != "0" ] && return 1
  return 0
}

### Executes the given command as "uiuser" on the given PostgreSQL host
apply_pg_dba_sql_command() {
  local HOST=$1 ; shift
  local PORT=$1 ; shift
  local DB=$1; shift
  local SQL_CMD=$1; shift
  local PARAMS=$@

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }
  [ -z "$SQL_CMD" ] && { echo ": $FUNCNAME: Undefined SQL_CMD" ; exit 1 ; }

  (( PG_SQL_COMMAND_COUNT += 1 ))
  PG_SQL_FILE=$WORKING_DIR/pg_dba_sql_command$PG_SQL_COMMAND_COUNT.sql
  echo -e "$SQL_CMD" > $PG_SQL_FILE

  apply_pg_dba_sql $HOST $PORT $DB $PG_SQL_FILE $PARAMS
  return $?
}

get_pg_connected_users() {
  local HOST="$1"
  local PORT="$2"
  local DBNAME="$3"

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DBNAME" ] && { echo ": $FUNCNAME: Undefined DBNAME" ; exit 1 ; }

  echo ": $FUNCNAME: get users connected to $DBNAME"
  apply_pg_dba_sql_command "$HOST" "$PORT" "postgres" \
    "select * from pg_stat_activity where datname = '$DBNAME';"
  result=$?
  [ "$result" != "0" ] && { echo ": $FUNCNAME: could not get connected users"; exit 1; }

  local users=`ssh $HOST -- cat $PG_SQL_FILE.log`
  [ "`echo $users | wc -l`" -gt "3" ]
  return $?
}

### Copy (from a source to a dest) a Postgres database
### On the dest host /etc/sudoers must contain "maint  ALL=(uiuser)   NOPASSWD:ALL"
copy_statdb() {
  local DST_HOST="$1"; shift     # a destination host
  local DST_DB="$1"; shift       # a destination database
  local SRC_HOST="$1"; shift     # a source host
  local SRC_DB="$1"; shift       # a source database
  local PARAMS="$@"              # additional parameters for db-copy.sh
  local result="0"

  # Get list of blocker hosts
  export PGDATABASE="postgres"
  PGPASSFILE=`mktemp /tmp/.pgpass.XXXXXXXX`
  echo $DST_HOST":5432:*:oix:adserver" > $PGPASSFILE
  chmod 600 $PGPASSFILE
  export PGPASSFILE=$PGPASSFILE

  local blocker_hosts=`psql -p 5432 -U oix -h $DST_HOST -d $DST_DB -c \
    "select client_addr from pg_stat_activity where datname = '$DST_DB' " \
    | grep '^ [1-9]' | sort | uniq`

  rm $PGPASSFILE

  local my_hostname=`hostname`
  for host in $blocker_hosts; do
    local hostname=`host $host | sed -n -e 's/^.*pointer \(.*\).ocslab.com./\1/p'`
    if [ "$my_hostname" != "$hostname" ]; then
      if echo "$hostname" | grep -qE '(^oix-dev[0-9]$)'; then
        echo ": Stopping cluster at host $hostname"
        stop_cluster $hostname "pgdb-moscow"
        stop_cluster $hostname "ui-moscow"
      fi
    fi
  done

  # lock database
  doc lock_postgres_db $SRC_HOST $SRC_DB

  # The copying script must executes on the DST_HOST
  execute_remote $DST_HOST $SRC_HOST $SRC_DB $DST_HOST $DST_DB $PARAMS <<-"EOF"
    WORKING_DIR=`pwd`
    . $WORKING_DIR/commons.sh

    SRC_HOST=$1; shift
    SRC_DB=$1; shift
    DST_HOST=$1; shift
    DST_DB=$1; shift

    PARAMS="$@"

    echo ": Copying '$SRC_HOST/$SRC_DB' to '$DST_HOST/$DST_DB' PG DB and updating dbi_link.dbi_connection"
    doc svn_export_folder "svn+ssh://svn/home/svnroot/oix/pgdb/trunk/utility/db-copy"
    doc sudo -u uiuser $CHECKOUT_FOLDER/db-copy.sh -U "oix" --src-host=$SRC_HOST --src-db=$SRC_DB --dest-db=$DST_DB $PARAMS
    sudo chown -R maint:maint $WORKING_DIR
EOF
  result="$?"

  # unlock database
  doc unlock_postgres_db $SRC_HOST $SRC_DB

  [ "$result" != "0" ] && return 1

  # skip following for read only databases
  [ "$DST_DB" = "nb_copy" ] && return 0
  [ "$DST_DB" = "nb_full_copy" ] && return 0
  [ "$DST_DB" = "test_copy" ] && return 0
  [ "$DST_DB" = "test_full_copy" ] && return 0
  [ "$DST_DB" = "emergency_copy" ] && return 0
  [ "$DST_DB" = "emergency_full_copy" ] && return 0

  echo ": Allow permissions for Merger"
  apply_pg_dba_sql_command "$DST_HOST" 5432 "$DST_DB" \
    "grant usage on foreign server csv_reader to merger;" || return 1

  echo ": Truncate table jobs.jobs"
  apply_pg_sql_command "$DST_HOST" 5432 "$DST_DB" "oix" \
    "truncate table jobs.jobs;" || return 1
  return 0
}

truncate_replication_marker() {
  local PG_HOST="$1"
  local PG_DB="$2"

  [ -z "$PG_HOST" ] && { echo ": $FUNCNAME: Undefined PG_HOST" ; exit 1 ; }
  [ -z "$PG_DB" ] && { echo ": $FUNCNAME: Undefined PG_DB" ; exit 1 ; }

  echo ": Truncate table replication_marker"
  apply_pg_sql_command "$PG_HOST" 5432 "$PG_DB" "repl" \
    "truncate table replication_marker;
     truncate table applied_scn;" || return 1
  return 0
}

### Fast copy copies tablespaces and doesn't use pg_dump / pg_restore
### Source database must be on the same host and created by usual copy_statdb
fast_copy_statdb() {
  local DST_HOST="$1"   # a destination host
  local DST_DB="$2"     # a destination database
  local SRC_DB=$3       # a source template database (created by copy_statdb)
  local ORA_HOST="$4"   # an Oracle host related to the destination
  local ORA_DB="$5"     # an Oracle instance related to the destination
  local ORA_PORT="$6"   # an Oracle port
  local ORA_REMOTE_SCHEMA="$DST_DB"
  local ORA_USER_NAME="$DST_DB"

  if echo $DST_DB | grep -q 'test_trunk'; then
    ORA_USER_NAME="ADSERVER_LC"
    ORA_REMOTE_SCHEMA="ADSERVER_LC"
  fi

  echo ": Check is $DST_DB tablespace exists"
  apply_pg_sql_command "$DST_HOST" "5432" "postgres" "adserver" \
    "select count(*) from pg_tablespace where spcname = '$DST_DB';" \
    -A -t 2>/dev/null
  local tablespace_exist=`cat $PG_SQL_FILE.log`
  if [ "$tablespace_exist" != "1" ] ; then
    echo ": There is no tablespace for $DST_DB, using copy_statdb function"
    copy_statdb "$DST_HOST" "$DST_DB" "$DST_HOST" "$SRC_DB" \
      --ora-host=$ORA_HOST --ora-db=$ORA_DB --ora-port=$ORA_PORT \
      --ora-username=$ORA_USER_NAME --ora-remote-schema=$ORA_REMOTE_SCHEMA || return 1
    truncate_replication_marker "$DST_HOST" "$DST_DB" || return 1
    return 0
  fi

  local attempt=0
  local max_attempt=10
  local interval=60
  local result=0

  while [ $attempt -lt $max_attempt ]; do
    get_pg_connected_users $DST_HOST 5432 $SRC_DB
    result=$?
    [ "$result" = "0" ] || break

    echo ": $FUNCNAME: '$SRC_DB' is accessed by users, waiting $interval seconds ..."
    sleep $interval
    ((attempt+=1))
  done
  [ "$result" = "0" ] && { echo ": $FUNCNAME: time is out to get an exclusive access to $SRC_DB"; exit 1; }

  echo ": $FUNCNAME: coping database from $SRC_DB"

  # do not check the exit status, as it is possible that the database was already deleted
  lock_postgres_db "$DST_HOST" "$DST_DB"

  apply_pg_dba_sql_command "$DST_HOST" "5432" "postgres" \
    "drop database if exists $DST_DB;
     select * from pg_stat_activity where datname = '$DST_DB';
     create database $DST_DB owner=oix template=$SRC_DB tablespace=$DST_DB;
     alter database $DST_DB set search_path to public,stat;"
  result=$?
  doc unlock_postgres_db "$DST_HOST" "$DST_DB"
  [ "$result" != "0" ] && exit 1

  echo ": $FUNCNAME: Add an info when a copy was made"
  apply_pg_dba_sql_command "$DST_HOST" "5432" "$DST_DB" \
    "insert into schema_applied_patches (patch_name, apply_date, product)
     values ('$DST_HOST.$SRC_DB->$DST_DB', now(), 'db_copy')" || exit 1

  echo ": $FUNCNAME: updating DBIlink"
  apply_pg_dba_sql_command "$DST_HOST" "5432" "$DST_DB" \
    "update dbi_link.dbi_connection set data_source = 'dbi:Oracle:host=$ORA_HOST;service_name=$ORA_DB;port=$ORA_PORT';
     update dbi_link.dbi_connection set user_name = '$ORA_USER_NAME', remote_schema = '$ORA_REMOTE_SCHEMA' where user_name not ilike 'replication%';" || exit 1;

  echo ": $FUNCNAME: allow permissions for Merger"
  apply_pg_dba_sql_command "$DST_HOST" 5432 "$DST_DB" \
    "grant usage on foreign server csv_reader to merger;" || return 1

  echo ": $FUNCNAME: truncate table replication_marker"
  truncate_replication_marker "$DST_HOST" "$DST_DB" || return 1

  echo ": Disable all jobs - set cron = '' in jobs.jobs"
  apply_pg_sql_command "$DST_HOST" 5432 "$DST_DB" "oix" \
    "update jobs.jobs set cron = '';" || return 1

  return 0
}

### Refresh PostgreSQL database
refresh_postgres() {
  local SRC_HOST=$1
  local SRC_DB=$2
  local SRC_TEMPLATE=$3
  local ORA_HOST=$4
  local ORA_DB=$5
  local ORA_PORT=$6
  local DB_NO=$7       # 0..nn or S[kip]
  local FULL_COPY=$8   # Y or N

  [ -z "$DB_NO" ] && { echo "Undefined DB_NO" ; exit 1 ; }
  [ -z "$FULL_COPY" ] && FULL_COPY="N"
  [ "$DB_NO" = "S" ] && return 0

  local DST_DB="ui_dev_$DB_NO"
  local DBI_LINK_ORA_SCHEMA="UI_DEV_$DB_NO"
  if [ "$SRC_TEMPLATE" = "nb_copy" ]; then
     DST_DB="nb_copy$DB_NO"
     DBI_LINK_ORA_SCHEMA="NB_COPY$DB_NO"
  fi

  if [ "$FULL_COPY" = "Y" ]; then
    fast_copy_statdb "stat-dev0" "$DST_DB" "${SRC_TEMPLATE%%_copy}_full_copy" "$ORA_HOST" "$ORA_DB" "$ORA_PORT" || return 1
  else
    fast_copy_statdb "stat-dev0" "$DST_DB" "$SRC_TEMPLATE" "$ORA_HOST" "$ORA_DB" "$ORA_PORT" || return 1
  fi

  return 0
}

refresh_postgres_common_db () {
  local DB="$1"; shift
  local SRC_HOST="$1"; shift
  local SRC_DB="$1"; shift
  local PARAMS="$@"

  doc copy_statdb "stat-dev0" "${DB}_full_copy" "$SRC_HOST" "$SRC_DB" --full-copy
  doc copy_statdb "stat-dev0" "${DB}_copy" "stat-dev0" "${DB}_full_copy" $PARAMS

  return 0
}

### Copy PostgreSQL database from Emergency colocation
refresh_postgres_from_emergency() {
  local DBN="$1"
  local FULL="$2"

  refresh_postgres "epostgres.ocslab.com" "stat" "emergency_copy" "oradev.ocslab.com" "addbtc.ocslab.com" "1521" $DBN $FULL
}

### Copy PostgreSQL database from Test colocation
refresh_postgres_from_test() {
  local DBN="$1"
  local FULL="$2"

  refresh_postgres "stat-test.ocslab.com" "stat_test" "test_copy" "oradev.ocslab.com" "addbtc.ocslab.com" "1521" $DBN $FULL
}

refresh_postgres_from_test_patched_till_trunk() {
  local DBN="$1"
  local FULL="$2"

  refresh_postgres "stat-test.ocslab.com" "stat_test" "test_trunk_copy" "oradev.ocslab.com" "addbtc.ocslab.com" "1521" $DBN $FULL
}

### Copy PostgreSQL database from NB Master colo
refresh_postgres_from_nb_master() {
  local DBN="$1"
  local FULL="$2"

  refresh_postgres "stat-nbmaster.ocslab.com" "nb_trunk_manual" "nb_copy" "ora-nb.ocslab.com" "addbnba.ocslab.com" "1521" $DBN $FULL
}

copy_postgres_unittest() {
  local SCHEMA_NO=$1 # Schema, 0..9

  [ -z "$SCHEMA_NO" ] && { echo "Undefined SCHEMA_NO" ; exit 1 ; }

  fast_copy_statdb "stat-dev0" "unittest_ui_$SCHEMA_NO" "adserver_empty" \
    "ora-nb.ocslab.com" "addbnba.ocslab.com" "1521" || return 1

  return 0
}

refresh_postgres_from_source() {
  local src="$1"
  local dbn="$2"
  local full="$3"
  local patch_till="$4"

  [ -z "$src" ] && { echo ": $FUNCNAME: undefined src "; exit 1; }
  [ -z "$dbn" ] && { echo ": $FUNCNAME: undefined dbn "; exit 1; }
  [ -z "$full" ] && { echo ": $FUNCNAME: undefined full "; exit 1; }

  echo ": $FUNCNAME: "
  local result=0

  case $src in
    emergency)
      docr refresh_postgres_from_emergency "$dbn" "$full"
      result=$?
    ;;
    test_force_lc)
      docr refresh_postgres_from_test_patched_till_trunk "$dbn" "$full"
      result=$?
    ;;
    test_force_tc)
      docr refresh_postgres_from_test "$dbn" "$full"
      result=$?
    ;;
    test*)
      if [ "X$patch_till" = "Xtrunk" ]; then
        docr refresh_postgres_from_test_patched_till_trunk "$dbn" "$full"
        result=$?
      else
        docr refresh_postgres_from_test "$dbn" "$full"
        result=$?
      fi
    ;;
    nb)
      docr refresh_postgres_from_nb_master "$dbn" "$full"
      result=$?
    ;;
    unittest)
      docr copy_postgres_unittest "$dbn"
      result=$?
    ;;
    *)
      echo ": $FUNCNAME: Unknown source '$src'"
      result=1
    ;;
  esac
  echo ": $FUNCNAME: done with $result"
  return $result

}

replicate_tables() {
  local HOST=$1
  local PORT=$2
  local DB=$3
  local LOCK_ORACLE=$4

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }
  [ -z "$LOCK_ORACLE" ] && LOCK_ORACLE="false"

  # wait till PGDB-1410 is released everywhere,
  # then remove the next check
  echo "Check that 'enabled' field is present in jobs table"
  apply_pg_sql_command $HOST $PORT $DB "ro" "\d jobs.jobs" -A -t &>/dev/null || return 1
  cat $PG_SQL_FILE.log | grep -q 'enabled|'
  local enable_exist=$?
  if [ "$enable_exist" = "0" ] ; then
    # disable jobs and wait till they are stopped
    apply_pg_sql_command $HOST $PORT $DB "oix" \
      "update jobs.jobs set enabled = false;" || return 1

    echo "Waiting for jobs to stop"
    while true ; do
      apply_pg_sql_command $HOST $PORT $DB "ro" \
        "select job_name, cron, current_status from jobs.jobs where current_status = 'STARTED';" \
        -A -t 2>/dev/null || return 1
      echo "Currently running:"
      cat $PG_SQL_FILE.log
      echo

      local job_count=`cat $PG_SQL_FILE.log | wc -l`
      [ "$job_count" = "0" ] && { echo "Nothing" ; break ; }

      sleep 7
    done
  fi

  # recreate tables
  apply_pg_sql_command $HOST $PORT $DB "repl" \
    "select replication.recreate_all_tables($LOCK_ORACLE);" || return 1

  # enable jobs
  if [ "$enable_exist" = "0" ] ; then
    apply_pg_sql_command $HOST $PORT $DB "oix" \
      "update jobs.jobs set enabled = true;" || return 1
  fi

  return 0
}

### Initialize the replication without Streams re-creating
### Works together with ora_replication_init
pg_replication_init() {
  local HOST=$1
  local PORT=$2
  local DB=$3

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }

  echo "Initialize replication marker (REPL-214)"
  apply_pg_sql_command $HOST $PORT $DB "oix" \
    "select replication.refill_one_table('replication_marker', '1=1');" || exit 1

  replicate_tables $HOST $PORT $DB || exit 1

  return 0
}

stop_postgres() {
  local host=$1
  [ -z "$host" ] && { echo ": $FUNCNAME: Undefined host" ; exit 1 ; }

  # remove when Test StanBy will be moved to another host
  local service="pgadm"
  [ "$host" = "stat-nbperf" ] && service="pgadm-moscow-nb-oui-perf"

  if ! ssh "$host" -o 'BatchMode yes' -- sudo -u uiuser /opt/foros/manager/bin/cmgr stop -f  "$service"; then
    apply_pg_dba_sql_command "$host" "5432" "postgres" \
      "select pid, datname, usename, application_name, client_addr, state, current_timestamp-query_start duration, query from pg_stat_activity;"
    #TODO remove after update
    return 0
  fi
  return 0
}

postgres_disconnect_all_users() {
  local HOST=$1
  local PORT=$2
  local DB=$3
  echo ": $FUNCNAME: disconnecting all users from $DB@$HOST:$PORT"
  apply_pg_dba_sql_command "$HOST" "$PORT" "postgres" \
    "select pid, (select pg_terminate_backend(pid)) as killed from pg_stat_activity where datname = '$DB' and pid <> pg_backend_pid();
     select pid, usename, application_name, query from pg_stat_activity where datname = '$DB';" || return 1
  return 0
}

# set POSTGRES_DB_BRANCH
get_postgres_db_branch() {
  local database=$1
  [ -z $database ] && { echo "$0: Undefined database"; exit 1; }
  case $database in
    test) local host="stat-test"; local port="5432"; local db="stat_test";;
    emergency) local host="epostgres"; port="5432"; local db="stat";;
    *) echo "$0: Unknown database $database"; exit 1;;
  esac

  apply_pg_sql_command $host $port $db "oix" \
    'select array_to_string((string_to_array(build_num, $$.$$))[1:3], $$.$$) from
     schema_applied_patches where product = $$statdb$$ order by apply_date desc
     limit 1' "-At"
  local result=$?
  local version=`cat ${PG_SQL_FILE}.log`
  if [ "$result" = "0" ]; then
    echo  $version > $WORKING_DIR/$database.postgres.version
    put_to_store "get_db_version" $WORKING_DIR/$database.postgres.version
  else
    get_from_store "get_db_version" $WORKING_DIR/$database.postgres.version
    local version=`cat $WORKING_DIR/$database.postgres.version 2>/dev/null`
  fi
  POSTGRES_DB_BRANCH=$version
}

lock_postgres_db() {
  local host="$1"
  local db="$2"
  echo ": $FUNCNAME: locking $db on $host"

  case $host in
    stat-dev*)
      ;;
    *)
      ssh -o 'BatchMode yes' -- $host /opt/foros/manager/bin/cmgr -f pgdb :LockDatabase start || \
        { echo "$FUNCNAME: could not lock database $db@$host"; return 1; }
      return 0
      ;;
  esac

  execute_remote $host $db <<-"EOF"
    WORKING_DIR=`pwd`
    . $WORKING_DIR/commons.sh

    DB=$1

    doc checkout_file "svn+ssh://svn/home/svnroot/oix/pgdb/trunk/utility/misc/lock_database.sh"
    doc sudo -u uiuser $CHECKOUT_FILE $DB
EOF
  [ "$?" != "0" ] && return 1
  echo " $FUNCNAME: done"

  return 0
}

unlock_postgres_db() {
  local host="$1"
  local db="$2"
  echo ": $FUNCNAME: unlocking $db on $host"

  case $host in
    stat-dev*)
      ;;
    *)
      ssh -o 'BatchMode yes' -- $host /opt/foros/manager/bin/cmgr -f pgdb :UnlockDatabase start || \
        { echo "$FUNCNAME: could not unlock database $db@$host"; return 1; }
      ssh -o 'BatchMode yes' -- $host /opt/foros/manager/bin/cmgr -f pgdb status
      return 0
      ;;
  esac

  execute_remote $host $db <<-"EOF"
    WORKING_DIR=`pwd`
    . $WORKING_DIR/commons.sh

    DB=$1

    doc checkout_file "svn+ssh://svn/home/svnroot/oix/pgdb/trunk/utility/misc/unlock_database.sh"
    doc sudo -u uiuser $CHECKOUT_FILE $DB
EOF
  [ "$?" != "0" ] && return 1
  echo " $FUNCNAME: done"

  return 0
}
