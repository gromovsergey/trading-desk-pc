#!/bin/bash

. $WORKING_DIR/commons.util.sh
. $WORKING_DIR/commons.svn.sh
. $WORKING_DIR/commons.cluster.sh

### Patch a PostgreSQL database
### Patches databases on stat-dev0
### To patch a database on another host, use patch_database
patch_postgres() {
  local DB=$1                # ui_dev_0..14 or unittest_ui_0..9
  local TO_STATDB_VERSION=$2 # "trunk" or "3.5.0" or S[kip]
  local TO_BI_VERSION=$3     # "trunk" or "3.5.0" or S[kip]

  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }
  [ -z "$TO_STATDB_VERSION" ] && { echo ": $FUNCNAME: Undefined TO_STATDB_VERSION" ; exit 1 ; }
  [ -z "$TO_BI_VERSION" ] && TO_BI_VERSION="S"

  local PG_HOST=stat-dev0

  if [ "${TO_STATDB_VERSION:0:1}" != "S" ] ; then
    get_svn_path "$TO_STATDB_VERSION"
    if echo $SVN_PATH | grep -qE '^tags'; then
      doc patch_database $PG_HOST 5432 "$DB" "oix" "$TO_STATDB_VERSION"
    else
      doc patch_database $PG_HOST 5432 "$DB" "oix" "$SVN_PATH"
    fi
  fi

  if [ "${TO_BI_VERSION:0:1}" != "S" ] ; then
    get_svn_path "$TO_BI_VERSION"
    if echo $SVN_PATH | grep -qE '^tags'; then
      doc patch_database $PG_HOST 5432 "$DB" "bi" "$TO_BI_VERSION"
    else
      doc patch_database $PG_HOST 5432 "$DB" "bi" "$SVN_PATH"
    fi
  fi

  return 0
}

### Patch a Postgres Database using SVN branch
patch_database() {
  local HOST=$1
  local PORT=$2
  local DB=$3    
  local USER=$4       # "oix" to patch STATDB or "bi" to patch BI
  local TO_VERSION=$5 # trunk or branches/X.X.X or tags/X.X.X.X or branches/dev/PGDB-XXXX or S[kip]

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }
  [ -z "$USER" ] && { echo ": $FUNCNAME: Undefined USER" ; exit 1 ; }
  [ -z "$TO_VERSION" ] && { echo ": $FUNCNAME: Undefined TO_VERSION" ; exit 1 ; }

  [ "${TO_VERSION:0:1}" = "S" -o "${TO_VERSION:0:1}" = "s" ] && return 0

  # lock database as there can be running jobs (that leads to deadlocks)
  [ "$USER" = "oix" ] && lock_postgres_db $HOST $DB

  # doing the patching remotely to use latest psql client
execute_remote stat-dev0 $HOST $PORT $DB $USER $TO_VERSION <<-"EOF"
  WORKING_DIR=$(cd `dirname $0`; pwd)
  . $WORKING_DIR/commons.svn.sh

  HOST="$1"
  PORT="$2"
  DB="$3"
  USER="$4"
  TO_VERSION="$5"

  svn_export_folder "svn+ssh://svn.ocslab.com/home/svnroot/oix/pgdb/trunk/utility/patch_db"
  PATCH_DB_DIR=$CHECKOUT_FOLDER

  USER_ARGS="-r svn+ssh://svn/home/svnroot/oix/pgdb"
  [ "$USER" = "bi" ] && USER_ARGS="-product bi -r svn+ssh://svn/home/svnroot/oix/bi"
  ARGS="-statdb --compareRevisions=no $USER_ARGS -to $TO_VERSION -host $HOST -port $PORT -db $DB -u $USER -p adserver -l $WORKING_DIR/patch_pg_$USER.$DB.log"

  echo ": $PATCH_DB_DIR/apply_patch.php $ARGS"
  $PATCH_DB_DIR/apply_patch.php -i "/home/maint/.ssh/id_rsa" $ARGS || exit 1

  exit 0
EOF
  result=$?

  # unlock database
  [ "$USER" = "oix" ] && unlock_postgres_db $HOST $DB

  return $result
}

### Applying patches from the svn working copy
### Keep in mind, you must patch the database till the needed branch (tag|trunk) first
patch_database_using_working_copy() {
  local HOST=$1
  local PORT=$2
  local DB=$3    
  local USER=$4         # "oix" to patch STATDB or "bi" to patch BI
  local TO_VERSION=$5   # trunk or branches/X.X.X or tags/X.X.X.X or branches/dev/PGDB-XXXX or S[kip]
  local WORKING_COPY=$6 # full path to SVN's working copy to apply patches from

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }
  [ -z "$USER" ] && { echo ": $FUNCNAME: Undefined USER" ; exit 1 ; }
  [ -z "$TO_VERSION" ] && { echo ": $FUNCNAME: Undefined TO_VERSION" ; exit 1 ; }
  [ -z "$WORKING_COPY" ] && { echo ": $FUNCNAME: Undefined WORKING_COPY" ; exit 1 ; }

  # lock database as there can be running jobs (that leads to deadlocks)
  [ "$USER" = "oix" ] && lock_postgres_db $HOST $DB

  # doing the patching remotely to use latest psql client
  svn_export_folder "svn+ssh://svn.ocslab.com/home/svnroot/oix/pgdb/trunk/utility/patch_db"
  PATCH_DB_DIR=$CHECKOUT_FOLDER

  ARGS="-statdb -b $WORKING_COPY -host $HOST -port $PORT -db $DB -u $USER -p adserver -devel -to $TO_VERSION -l $WORKING_DIR/patch_pg_$USER.$DB.log"

  echo ": $PATCH_DB_DIR/apply_patch.php $ARGS"
  $PATCH_DB_DIR/apply_patch.php -i "/home/maint/.ssh/id_rsa" $ARGS || exit 1

  # unlock database
  [ "$USER" = "oix" ] && unlock_postgres_db $HOST $DB

  return $result
}

### Returns current version of the databases
### Looks into SCHEMA_APPLIED_PATCHES table and get the latest BUILD_NUM from it
### Returns
###    PGDB_VERSION (for example, '3.0.0')
###    BI_VERSION (for example, '3.0.0')
get_pgdb_version() {
  local HOST=$1
  local PORT=$2
  local DB=$3

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }
  [ -z "$USER" ] && { echo ": $FUNCNAME: Undefined USER" ; exit 1 ; }

  apply_pg_sql_command "$HOST" "$PORT" "$DB" "oix" \
    "select get_version('statdb');" -A -t || exit 1
  PGDB_VERSION=`cat $PG_SQL_FILE.log`
  PGDB_VERSION=${PGDB_VERSION%.*}

  apply_pg_sql_command "$HOST" "$PORT" "$DB" "oix" \
    "select get_version('bi');" -A -t || exit 1
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
  # Do an additional check
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

  # do not use -e (escape) here
  # as '\timing off' won't work
  echo "$SQL_CMD" >> $PG_SQL_FILE
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
  
  # do not use -e (escape) here
  # as '\timing off' won't work
  echo "$SQL_CMD" > $PG_SQL_FILE

  apply_pg_dba_sql $HOST $PORT $DB $PG_SQL_FILE $PARAMS
  return $?
}

### Returns
###   0 - there is no connected users
###   1 - otherwise
get_pg_connected_users() {
  local HOST="$1"
  local PORT="$2"
  local DBNAME="$3"

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DBNAME" ] && { echo ": $FUNCNAME: Undefined DBNAME" ; exit 1 ; }

  echo ": $FUNCNAME: get users connected to $DBNAME"
  apply_pg_dba_sql_command "$HOST" "$PORT" "postgres" \
    "\timing off
    select * from pg_stat_activity where datname = '$DBNAME';" -A -t -q
  result=$?
  [ "$result" != "0" ] && { echo ": $FUNCNAME: could not get connected users"; exit 1; }

  local count=`ssh $HOST -- cat $PG_SQL_FILE.log | wc -l`
  [ "$count" = "0" ] && return 0
  return 1
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

  # stopping the dev hosts connected to the destination database
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
    doc checkout_file "svn+ssh://svn/home/svnroot/oix/pgdb/trunk/utility/misc/lock_database.sh" && mv $CHECKOUT_FILE $CHECKOUT_FOLDER
    doc checkout_file "svn+ssh://svn/home/svnroot/oix/pgdb/trunk/utility/misc/unlock_database.sh" && mv $CHECKOUT_FILE $CHECKOUT_FOLDER
    doc sudo -Hiu uiuser $CHECKOUT_FOLDER/db-copy.sh -U "oix" --src-host=$SRC_HOST --src-db=$SRC_DB --dest-db=$DST_DB $PARAMS
    sudo chown -R maint:maint $WORKING_DIR
EOF
  result="$?"

  # unlock database
  doc unlock_postgres_db $SRC_HOST $SRC_DB

  [ "$result" != "0" ] && return 1

  # skip following for read only databases
  [ "$DST_DB" = "test_copy" ] && return 0
  [ "$DST_DB" = "test_full_copy" ] && return 0
  [ "$DST_DB" = "emergency_copy" ] && return 0
  [ "$DST_DB" = "emergency_full_copy" ] && return 0

  echo ": Allow permissions for Merger"
  apply_pg_dba_sql_command "$DST_HOST" 5432 "$DST_DB" \
    "grant usage on foreign server file_reader to merger;" || return 1

  echo ": Truncate colocation-dependend tables"
  apply_pg_sql_command "$DST_HOST" 5432 "$DST_DB" "oix" \
    "truncate table jobs.jobs;
     truncate table public.foros_timed_services;" || return 1
  return 0
}

### Fast copy copies tablespaces and doesn't use pg_dump / pg_restore
### Source database must be on the same host and created by usual copy_statdb
fast_copy_statdb() {
  local DST_HOST="$1"   # a destination host
  local DST_DB="$2"     # a destination database
  local SRC_DB=$3       # a source template database (created by copy_statdb)

  echo ": Check is $DST_DB tablespace exists"
  apply_pg_sql_command "$DST_HOST" "5432" "postgres" "adserver" \
    "select count(*) from pg_tablespace where spcname = '$DST_DB';" \
    -A -t 2>/dev/null
  local tablespace_exist=`cat $PG_SQL_FILE.log`
  if [ "$tablespace_exist" != "1" ] ; then
    echo ": There is no tablespace for $DST_DB, using copy_statdb function"
    copy_statdb "$DST_HOST" "$DST_DB" "$DST_HOST" "$SRC_DB" || return 1
    return 0
  fi

  local attempt=0
  local result=0
  while [ $attempt -lt "10" ]; do
    get_pg_connected_users $DST_HOST 5432 $SRC_DB
    result=$?
    [ "$result" = "0" ] && break

    local interval=60
    echo ": $FUNCNAME: '$SRC_DB' is accessed by users, waiting $interval seconds ..."
    sleep $interval
    ((attempt+=1))
  done
  [ "$result" != "0" ] && { echo ": $FUNCNAME: time is out to get an exclusive access to $SRC_DB"; exit 1; }

  echo ": $FUNCNAME: coping database from $SRC_DB"

  # do not check the exit status, as it is possible that the database was already deleted
  lock_postgres_db "$DST_HOST" "$DST_DB"

  echo ": $FUNCNAME: Active sessions in $DST_DB"
  apply_pg_dba_sql_command "$DST_HOST" "5432" "postgres" \
    "select * from pg_stat_activity where datname = '$DST_DB';"

  echo ": $FUNCNAME: Re-creating $DST_DB"
  apply_pg_dba_sql_command "$DST_HOST" "5432" "postgres" \
    "drop database if exists $DST_DB;
     create database $DST_DB owner=oix template=$SRC_DB tablespace=$DST_DB;
     alter database $DST_DB set search_path to public,stat;"
  result=$?
  doc unlock_postgres_db "$DST_HOST" "$DST_DB"
  [ "$result" != "0" ] && exit 1

  echo ": $FUNCNAME: Add an info when a copy was made"
  apply_pg_dba_sql_command "$DST_HOST" "5432" "$DST_DB" \
    "insert into schema_applied_patches (patch_name, apply_date, product)
     values ('$DST_HOST.$SRC_DB->$DST_DB', now(), 'db_copy')" || exit 1

  echo ": $FUNCNAME: allow permissions for Merger"
  apply_pg_dba_sql_command "$DST_HOST" 5432 "$DST_DB" \
    "grant usage on foreign server file_reader to merger;" || return 1

  echo ": $FUNCNAME: Disable the jobs"
  apply_pg_sql_command "$DST_HOST" 5432 "$DST_DB" "oix" \
    "update jobs.jobs set enabled = false;" || return 1

  return 0
}

### Refresh PostgreSQL database
refresh_postgres() {
  local SRC_HOST=$1
  local SRC_DB=$2
  local DST_DB=$3          # "ui_dev_0" or 0..nn or S[kip]
  local FULL_COPY=${4:0:1} # Y or N

  [ -z "$DST_DB" ] && { echo "Undefined DST_DB" ; exit 1 ; }
  [ -z "$FULL_COPY" ] && FULL_COPY="N"
  [ "${DST_DB:0:1}" = "S" ] && return 0

  [[ "$DST_DB" == ?(-)+([0-9]) ]] && DST_DB="ui_dev_$DST_DB"
  if [ "${FULL_COPY^}" = "Y" ]; then
    fast_copy_statdb "stat-dev0" "$DST_DB" "${SRC_DB%%_copy}_full_copy" || return 1
  else
    fast_copy_statdb "stat-dev0" "$DST_DB" "$SRC_DB" || return 1
  fi

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
      docr refresh_postgres "stat-test.ocslab.com" "test_trunk_copy" "$dbn" "$full"
      result=$?
    ;;
    test_force_tc)
      docr refresh_postgres_from_test "$dbn" "$full"
      result=$?
    ;;
    test*)
      if [ "X$patch_till" = "Xtrunk" ]; then
        docr refresh_postgres "stat-test.ocslab.com" "test_trunk_copy" "$dbn" "$full"
        result=$?
      else
        docr refresh_postgres "stat-test.ocslab.com" "test_copy" "$dbn" "$full"
        result=$?
      fi
    ;;
    unittest)
      docr ast_copy_statdb "stat-dev0" "unittest_ui_$dbn" "adserver_empty"
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
  [ -z "$database" ] && { echo "$0: Undefined database"; exit 1; }

  case $database in
    test) local host="stat-test"; local port="5432"; local db="stat_test";;
    emergency) local host="epostdb00"; port="5432"; local db="stat";;
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
  local host=`echo $1 | cut -d. -f1`
  local db="$2"
  
  [ -z "$host" ] && { echo "$0: Undefined host"; exit 1; }
  [ -z "$db" ] && { echo "$0: Undefined db"; exit 1; }
  
  echo ": $FUNCNAME: locking $db on $host"

  # Next hosts to be locked not in working hours
  if [ "$host" = "epostdb00" -o "$host" = "stat-test" ] ; then
    ssh -o 'BatchMode yes' -- $host /opt/foros/manager/bin/cmgr -f pgdb :LockDatabase start || \
      { echo "$FUNCNAME: could not lock database $db@$host"; return 1; }
    return 0
  fi

  execute_remote $host $db <<-"EOF"
    WORKING_DIR=`pwd`
    . $WORKING_DIR/commons.sh

    DB=$1

    doc checkout_file "svn+ssh://svn/home/svnroot/oix/pgdb/trunk/utility/misc/lock_database.sh"
    doc sudo -Hiu uiuser $CHECKOUT_FILE $DB
EOF
  [ "$?" != "0" ] && return 1
  echo " $FUNCNAME: done"

  return 0
}

unlock_postgres_db() {
  local host=`echo $1 | cut -d. -f1`
  local db="$2"

  [ -z "$host" ] && { echo "$0: Undefined host"; exit 1; }
  [ -z "$db" ] && { echo "$0: Undefined db"; exit 1; }
  
  echo ": $FUNCNAME: unlocking $db on $host"

  # Next hosts to be locked not in working hours
  if [ "$host" = "epostdb00" -o "$host" = "stat-test" ] ; then
    ssh -o 'BatchMode yes' -- $host /opt/foros/manager/bin/cmgr -f pgdb :UnlockDatabase start || \
      { echo "$FUNCNAME: could not lock database $db@$host"; return 1; }
    return 0
  fi

  execute_remote $host $db <<-"EOF"
    WORKING_DIR=`pwd`
    . $WORKING_DIR/commons.sh

    DB=$1

    doc checkout_file "svn+ssh://svn/home/svnroot/oix/pgdb/trunk/utility/misc/unlock_database.sh"
    doc sudo -Hiu uiuser $CHECKOUT_FILE $DB
EOF
  [ "$?" != "0" ] && return 1
  echo " $FUNCNAME: done"

  return 0
}
