#!/bin/bash

PATH=$PATH:/sbin:/usr/sbin:/opt/oracle/product/10g/instantclient/bin/
export LD_LIBRARY_PATH=/opt/oracle/product/10g/instantclient/lib

. $WORKING_DIR/commons.util.sh
. $WORKING_DIR/commons.svn.sh

### Patch an Oracle schema till trunk
### TO_VERSION - trunk or branches/X.X.X or X.X.X.X or branches/dev/ADDB-XXXX
patch_schema() {
  local HOST=$1
  local PORT=$2
  local DB=$3
  local SCHEMA=$4
  local TO_VERSION=$5
  local DEFAULT_VALIDATION=$6
  local STOP_REPL=$7

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }
  [ -z "$SCHEMA" ] && { echo ": $FUNCNAME: Undefined SCHEMA" ; exit 1 ; }
  [ -z "$TO_VERSION" ] && { echo ": $FUNCNAME: Undefined TO_VERSION" ; exit 1 ; }
  [ -z "$DEFAULT_VALIDATION" ] && { echo ": $FUNCNAME: Undefined DEFAULT_VALIDATION" ; exit 1 ; }

  [ "$TO_VERSION" = "S" ] && return 0

  local SVN_PATH="trunk"
  local SVN_PATH_BASE="svn+ssh://svn/home/svnroot/oix/db/"
  [ -z "$TO_VERSION" ] && TO_VERSION="trunk"

  if echo $TO_VERSION | grep -q trunk; then
    SVN_PATH="trunk"
  else
    DOTS=`echo -n $TO_VERSION | sed -n 's/[^\.]//gp' | wc -m`
    case $DOTS in
      2) # branch
        SVN_PATH="$TO_VERSION"
        ;;
      3) # tag
        local path="${SVN_PATH_BASE}tags/${TO_VERSION}"
        get_ancestor $path
        SVN_PATH=${SVN_ANCESTOR#$SVN_PATH_BASE}
        ;;
      *) # dev branch
        local path="${SVN_PATH_BASE}${TO_VERSION}"
        get_ancestor $path
        SVN_PATH=${SVN_ANCESTOR#$SVN_PATH_BASE}
        ;;
    esac
  fi

  patch_schema_using_branch $SVN_PATH $HOST $PORT $DB $SCHEMA $TO_VERSION $DEFAULT_VALIDATION $STOP_REPL
  return $?
}

patch_schema_using_branch() {
  local SVN_PATH=$1
  local HOST=$2
  local PORT=$3
  local DB=$4
  local SCHEMA=$5
  local TO_VERSION=$6
  local DEFAULT_VALIDATION=$7
  local STOP_REPL=$8

  [ -z "$SVN_PATH" ] && { echo ": $FUNCNAME: Undefined SVN_PATH" ; exit 1 ; }
  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }
  [ -z "$SCHEMA" ] && { echo ": $FUNCNAME: Undefined SCHEMA" ; exit 1 ; }
  [ -z "$TO_VERSION" ] && { echo ": $FUNCNAME: Undefined TO_VERSION" ; exit 1 ; }
  [ -z "$DEFAULT_VALIDATION" ] && { echo ": $FUNCNAME: Undefined DEFAULT_VALIDATION" ; exit 1 ; }

  svn_export_folder "svn+ssh://svn.ocslab.com/home/svnroot/oix/db/$SVN_PATH/util/patch_db"

  local replication_flags=""
  replication_flags="--replicationUser=replication --replicationPassword=adserver"
  [ "X$STOP_REPL" = "Xyes" ] && replication_flags="$replication_flags -stopRepl yes"
  [ "X$STOP_REPL" = "Xno" ] && replication_flags="$replication_flags -stopRepl no"

  local flag="--defaultValidation=$DEFAULT_VALIDATION"
  grep -q "compareRevisions" $CHECKOUT_FOLDER/apply_patch.php && local compare_revisions="--compareRevisions=no"

  local args="-forosdb -r svn+ssh://svn/home/svnroot/oix/db -to $TO_VERSION -host $HOST -port $PORT -db $DB -u $SCHEMA -p adserver $flag $replication_flags $compare_revisions -l $WORKING_DIR/patch_schema.$SCHEMA.log"
  echo ": $FUNCNAME: $args"
  $CHECKOUT_FOLDER/apply_patch.php $args || return 1

  return 0
}

patch_schema_replication() {
  local HOST=$1
  local PORT=$2
  local DB=$3
  local SCHEMA=$4
  local TO_VERSION=$5

  [ -z "$TO_VERSION" ] && TO_VERSION="trunk"
  [ "$TO_VERSION" = "S" ] && return 0
  [ "$TO_VERSION" != "trunk" ] && { echo "$FUNCNAME: Could not patch replication to $TO_VERSION. To trunk only."; return 0; }

  patch_schema_replication_using_branch "trunk" $HOST $PORT $DB $SCHEMA $TO_VERSION
  return $?
}

patch_schema_replication_using_branch() {
  local SVN_PATH=$1
  local HOST=$2
  local PORT=$3
  local DB=$4
  local SCHEMA=$5
  local TO_VERSION=$6

  [ -z "$SVN_PATH" ] && { echo ": $FUNCNAME: Undefined SVN_PATH" ; exit 1 ; }
  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }
  [ -z "$SCHEMA" ] && { echo ": $FUNCNAME: Undefined SCHEMA" ; exit 1 ; }
  [ -z "$TO_VERSION" ] && TO_VERSION="$SVN_PATH"

  svn_export_folder "svn+ssh://svn.ocslab.com/home/svnroot/oix/db/$SVN_PATH/util/patch_db"
  grep -q "compareRevisions" $CHECKOUT_FOLDER/apply_patch.php && local compare_revisions="--compareRevisions=no"

  echo ": $FUNCNAME: Patching Oracle "$SCHEMA"/"$HOST":"$PORT"/"$DB", product = replication"
  $CHECKOUT_FOLDER/apply_patch.php -forosdb -r svn+ssh://svn/home/svnroot/oix/streams-replication -to $TO_VERSION -host $HOST -port $PORT -db $DB -u $SCHEMA \
    -p adserver -prefix forosdb/schemas/forosuser -product replication $compare_revisions \
    -l $WORKING_DIR/patch_schema.$SCHEMA.replication.log || return 1

  return 0
}

patch_ora_replication() {
  local HOST=$1
  local PORT=$2
  local DB=$3
  local SCHEMA=$4
  local TO_VERSION=$5

  [ -z "$TO_VERSION" ] && TO_VERSION="trunk"
  [ "$TO_VERSION" = "S" ] && return 0
  [ "$TO_VERSION" != "trunk" ] && { echo "$FUNCNAME: Could not patch replication to $TO_VERSION. To trunk only."; return 0; }

  patch_ora_replication_using_branch "trunk" $HOST $PORT $DB $SCHEMA $TO_VERSION
  return $?
}

patch_ora_replication_using_branch() {
  local SVN_PATH=$1
  local HOST=$2
  local PORT=$3
  local DB=$4
  local SCHEMA=$5
  local TO_VERSION=$6

  [ -z "$SVN_PATH" ] && { echo ": $FUNCNAME: Undefined SVN_PATH" ; exit 1 ; }
  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }
  [ -z "$SCHEMA" ] && { echo ": $FUNCNAME: Undefined SCHEMA" ; exit 1 ; }
  [ -z "$TO_VERSION" ] && TO_VERSION="$SVN_PATH"

  svn_export_folder "svn+ssh://svn.ocslab.com/home/svnroot/oix/db/$SVN_PATH/util/patch_db"
  grep -q "compareRevisions" $CHECKOUT_FOLDER/apply_patch.php && local compare_revisions="--compareRevisions=no"

  echo ": $FUNCNAME: Patching Oracle REPLICATION/"$HOST":"$PORT"/"$DB", product = replication"
  $CHECKOUT_FOLDER/apply_patch.php -forosdb -r svn+ssh://svn/home/svnroot/oix/streams-replication -to $TO_VERSION -host $HOST -port $PORT -db $DB -u replication \
    -p adserver -prefix forosdb/schemas/replication -product replication $compare_revisions \
    -l $WORKING_DIR/patch_schema.replication.replication.log || return 1

  return 0
}

### Patch an Oracle schema
patch_oracle() {
  local SCHEMA=$1            # UI_DEV_0..14 or UNITTEST_UI_0..9
  local TO_SCHEMA_VERSION=$2 # trunk or [SVN_BRANCH_NAME] or S[kip]
  local TO_REPL_VERSION=$3   # trunk or [SVN_BRANCH_NAME] or S[kip]
  local DEFAULT_VALIDATION=$4
  local STOP_REPL=$5

  [ -z "$SCHEMA" ] && { echo ": $FUNCNAME: Undefined SCHEMA" ; exit 1 ; }
  [ -z "$TO_SCHEMA_VERSION" ] && { echo ": $FUNCNAME: Undefined TO_SCHEMA_VERSION" ; exit 1 ; }
  [ -z "$TO_REPL_VERSION" ] && { echo ": $FUNCNAME: Undefined TO_REPL_VERSION" ; exit 1 ; }
  [ -z "$DEFAULT_VALIDATION" ] && { echo ": $FUNCNAME: Undefined DEFAULT_VALIDATION" ; exit 1 ; }
  [ "$TO_REPL_VERSION" != "S" ] && TO_REPL_VERSION="trunk"

  get_oracle_host_and_instance $SCHEMA
  local HOST=$ORA_HOST
  local INSTANCE=$ORA_INSTANCE

  if [ "$TO_REPL_VERSION" != "S" ] ; then
    get_svn_path "$TO_REPL_VERSION"
    doc patch_schema_replication $HOST 1521 "$INSTANCE" "$SCHEMA" "$SVN_PATH"
  fi

  if [ "$TO_SCHEMA_VERSION" != "S" ] ; then
    get_svn_path "$TO_SCHEMA_VERSION"
    if ! echo $SVN_PATH | grep -qE '^tags'; then
      TO_SCHEMA_VERSION=$SVN_PATH
    fi
    doc patch_schema $HOST 1521 "$INSTANCE" "$SCHEMA" "$TO_SCHEMA_VERSION" "$DEFAULT_VALIDATION" "$STOP_REPL"
  fi
  return 0
}

patch_oracle_tag_by_tag() {
  local host=$1
  local port=$2
  local db=$3
  local schema=$4

  [ -z $host ] && { echo "$FUNCNAME: undefined host"; exit 1; }
  [ -z $port ] && { echo "$FUNCNAME: undefined port"; exit 1; }
  [ -z $db ] && { echo "$FUNCNAME: undefined db"; exit 1; }
  [ -z $schema ] && { echo "$FUNCNAME: undefined schema"; exit 1; }

  echo "$FUNCNAME: HOST=$host PORT=$port DB=$db SCHEMA=$schema"

  local tag=""
  local message=""
  local applicable=0
  local repository="svn+ssh://svn/home/svnroot/oix/db"
  local result=0
  local status="OK"

  local connection="$schema/adserver@//$host:$port/$db"
  get_oracle_db_tag "$connection" || { echo "$FUNCNAME: could not get current db tag"; exit 1; }
  local current=$ORA_DB_TAG
  echo "$FUNCNAME: current db tag: $current"

  for tag in `get_tags $repository`; do
    if [ "$current" = "$tag" ]; then
      applicable=1
    fi

    if [ "$applicable" != "1" ]; then
      # echo "$FUNCNAME: skipping $tag"
      continue
    fi

    get_oracle_db_tag "$connection" || { echo "$FUNCNAME: could not get current db tag"; exit 1; }
    local from_tag=$ORA_DB_TAG

    echo "$FUNCNAME: applying tag $tag (from $from_tag)"
    patch_schema_using_branch "tags/$tag" "$host" "$port" "$db" "$schema" "$tag" "yes"
    result=$?

    status=`[ "$result" = "0" ] && echo OK || ([ "$result" = "255" ] && echo CRITICAL || echo FAIL)`
    message="${message}${from_tag}:${tag}:${result}:${status}#"
    [ "$result" = "255" ] && echo "$FUNCNAME: CRITICAL" && break
  done

  if [ "$result" != "255" ]; then
    get_oracle_db_tag "$connection" || { echo "$FUNCNAME: could not get current db tag"; exit 1; }
    local from_tag=$ORA_DB_TAG

    echo "$FUNCNAME: applying trunk (from $from_tag)"

    patch_schema_using_branch "trunk" "$host" "$port" "$db" "$schema" "trunk" "no"
    result=$?

    status=`[ "$result" = "0" ] && echo OK || ([ "$result" = "255" ] && echo CRITICAL || echo FAIL)`
    message="${message}${from_tag}:trunk:${result}:${status}#"
    [ "$result" = "255" ] && { echo "$FUNCNAME: CRITICAL"; }
  fi

  echo "FROM:TAG:CODE:STATUS#--------------------------------------#$message" | \
    sed -e 's|:|\t\t|g' -e 's|#|\n|g'

  exit $result
}

### Initialize the replication without Streams re-creating
ora_replication_init() {
  local host=$1
  local port=$2
  local db=$3
  local schema=$4

  echo "Delete REPLICATION_DATA (REPL-213)"
  apply_sql_command $host $port $db $schema \
    "delete from replication_data" || exit 1
  return 0
}

### Re-create Oracle Streams
ora_recreate_streams() {
  local HOST=$1
  local PORT=$2
  local DB=$3

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }

  doc svn_export_folder "svn+ssh://svn.ocslab.com/home/svnroot/oix/streams-replication/trunk/utility/sync_all_tables"
  apply_sql "$HOST" "$PORT" "$DB" "replication" $CHECKOUT_FOLDER/recreate_streams.sql || exit 1

  return 0
}

### Executes a script on Oracle to resync all replicated tables
resync_replicated_tables() {
  local ORA_HOST=$1
  local ORA_PORT=$2
  local ORA_DB=$3
  local ORA_USER=$4
  local REPLICATED_SCHEMA=$5

  [ -z "$ORA_HOST" ] && { echo ": $FUNCNAME: Undefined ORA_HOST" ; exit 1 ; }
  [ -z "$ORA_PORT" ] && { echo ": $FUNCNAME: Undefined ORA_PORT" ; exit 1 ; }
  [ -z "$ORA_DB" ] && { echo ": $FUNCNAME: Undefined ORA_DB" ; exit 1 ; }
  [ -z "$ORA_USER" ] && { echo ": $FUNCNAME: Undefined ORA_USER" ; exit 1 ; }

  echo ": $FUNCNAME: Resync Oracle's replicated tables"
  doc checkout_file "svn+ssh://svn.ocslab.com/home/svnroot/oix/streams-replication/trunk/utility/sync_all_tables/resync.sql"
  doc apply_sql $ORA_HOST $ORA_PORT $ORA_DB $ORA_USER $CHECKOUT_FILE $REPLICATED_SCHEMA

  return 0
}

### Execute given SQL file on an Oracle schema using SQLPLUS
apply_sql() {
  local HOST=$1 ; shift
  local PORT=$1 ; shift
  local DB=$1; shift
  local SCHEMA=$1; shift
  local SQL_FILE=$1; shift
  local PARAMS=$@

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }
  [ -z "$SCHEMA" ] && { echo ": $FUNCNAME: Undefined SCHEMA" ; exit 1 ; }
  [ -z "$SQL_FILE" ] && { echo ": $FUNCNAME: Undefined SQL_FILE" ; exit 1 ; }

  echo ": Command: sqlplus -L -S "$SCHEMA"/adserver@//"$HOST":"$PORT"/"$DB" @"$SQL_FILE" "$PARAMS
  sqlplus -L -S $SCHEMA/adserver@//$HOST:$PORT/$DB 2>&1 @$SQL_FILE $PARAMS > $SQL_FILE.log
  local res="$?"
  cat $SQL_FILE.log

  if [ "$res" != "0" ] ; then
    echo ": Error at running sqlplus !"
    return 1
  fi
  return 0
}

### Executes given SQL command on an Oracle schema
### Returns
###   $SQL_FILE.log - a file with results (of SELECT, for example)
SQL_COMMAND_COUNT=0
apply_sql_command() {
  local HOST=$1 ; shift
  local PORT=$1 ; shift
  local DB=$1; shift
  local SCHEMA=$1; shift
  local SQL_CMD=$1; shift
  local PARAMS=$@

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }
  [ -z "$SCHEMA" ] && { echo ": $FUNCNAME: Undefined SCHEMA" ; exit 1 ; }
  [ -z "$SQL_CMD" ] && { echo ": $FUNCNAME: Undefined SQL_CMD" ; exit 1 ; }

  (( SQL_COMMAND_COUNT += 1 ))
  SQL_FILE=$WORKING_DIR/command$SQL_COMMAND_COUNT.sql
  echo "set pagesize 0" > $SQL_FILE
  echo "set linesize 9999" >> $SQL_FILE
  echo -e "$SQL_CMD" >> $SQL_FILE
  echo "/" >> $SQL_FILE
  echo "exit" >> $SQL_FILE

  apply_sql $HOST $PORT $DB $SCHEMA $SQL_FILE
  return $?
}

### Refresh (copy from a source to a dest) an Oracle schema
refresh_schema() {
  local HOST=$1
  local PORT=$2
  local DB=$3
  local SCHEMA=$4
  local CMD=$5

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PORT" ] && { echo ": $FUNCNAME: Undefined PORT" ; exit 1 ; }
  [ -z "$DB" ] && { echo ": $FUNCNAME: Undefined DB" ; exit 1 ; }
  [ -z "$SCHEMA" ] && { echo ": $FUNCNAME: Undefined SCHEMA" ; exit 1 ; }
  [ -z "$CMD" ] && { echo ": $FUNCNAME: Undefined CMD" ; exit 1 ; }

  local REFRESH_SQL_FILE=$WORKING_DIR/refresh.sql
  echo -e $CMD " exit \n" > $REFRESH_SQL_FILE

  local result
  local attemp=0
  local interval=60   # 1 min
  local max_attemp=40 # average execution time of REFRESH procedure
  while [ "$attemp" -lt "$max_attemp" ]; do
    echo "Refreshing an Oracle schema. Attemp: $attemp"

    apply_sql_command "$HOST" $PORT "$DB" REPLICATION \
      "select schemaname, osuser, machine, module
       from v\$session
       order by schemaname, osuser" > $WORKING_DIR/connected_users.log

    echo "Command: sqlplus -L /nolog 2>&1"
    cat $REFRESH_SQL_FILE
    sqlplus -L /nolog 2>&1 @$REFRESH_SQL_FILE > $REFRESH_SQL_FILE.log
    result=$?

    if [ "$result" = "0" ] ; then
      # OUI-23912 drop quick search related indexes as there is a bug on Oracle side
      [ "$SCHEMA" = "ADSERVER_TC" ] && break
      [ "$SCHEMA" = "ADSERVER_LC" ] && break
      [ "$SCHEMA" = "ADSERVER_EC" ] && break
      [ "$SCHEMA" = "NB_COPY" ] && break
      doc checkout_file "svn+ssh://svn/home/svnroot/oix/db/trunk/util/drop_domain_indexes/drop_domain_indexes.sql"
      doc apply_sql $HOST $PORT $DB $SCHEMA $CHECKOUT_FILE

      break
    fi

    # an error or another refresh is running
    if cat $REFRESH_SQL_FILE.log | grep "Another REFRESH procedure" ; then
      echo "Waiting $interval seconds..."; echo
      sleep $interval
      ((attemp=attemp+1))
      result=2
    elif cat $REFRESH_SQL_FILE.log | grep -E '^ORA-20000:.*is not completed yet.$'; then
      echo "Waiting $interval seconds..."; echo
      sleep $interval
      ((attemp=attemp+1))
      result=2
    else
      cat $REFRESH_SQL_FILE.log
      echo "Connected users:"
      cat $WORKING_DIR/connected_users.log
      result=1
      break
    fi
  done

  return $result
}

### Copy Oracle schema from Emergency colocation
refresh_oracle_from_emergency() {
  local SCHEMA_NO=$1 # 0..nn or S[kip]

  [ -z "$SCHEMA_NO" ] && { echo "Undefined SCHEMA_NO" ; exit 1 ; }

  if [ "$SCHEMA_NO" != "S" ] ; then
    local ORA_SCHEMA="UI_DEV_$SCHEMA_NO"

    refresh_schema "oradev" 1521 "addbtc.ocslab.com" $ORA_SCHEMA "
      whenever sqlerror exit failure \n
      connect sys_ud_refresh/.ora_123@oradev/addbtc.ocslab.com \n
      set serveroutput on size 1000000 lines 999 \n
      exec sys.UD_REFRESH('E', $SCHEMA_NO) \n" || return 1
  fi

  return 0
}

### Copy Oracle schema from Test colocation
refresh_oracle_from_test() {
  local SCHEMA_NO=$1 # 0..nn or S[kip]

  [ -z "$SCHEMA_NO" ] && { echo "Undefined SCHEMA_NO" ; exit 1 ; }

  if [ "$SCHEMA_NO" != "S" ] ; then
    local ORA_SCHEMA="UI_DEV_$SCHEMA_NO"

    refresh_schema "oradev" 1521 "addbtc.ocslab.com" $ORA_SCHEMA "
      whenever sqlerror exit failure \n
      connect sys_ud_refresh/.ora_123@oradev/addbtc.ocslab.com \n
      set serveroutput on size 1000000 lines 999 \n
      exec sys.UD_REFRESH('T', $SCHEMA_NO) \n" || return 1
  fi

  return 0
}

### Copy Oracle schema from Test colocation patched till trunk
refresh_oracle_from_test_patched_till_trunk() {
  local SCHEMA_NO=$1 # 0..nn or S[kip]

  [ -z "$SCHEMA_NO" ] && { echo "Undefined SCHEMA_NO" ; exit 1 ; }

  if [ "$SCHEMA_NO" != "S" ] ; then
    local ORA_SCHEMA="UI_DEV_$SCHEMA_NO"

    refresh_schema "oradev" 1521 "addbtc.ocslab.com" $ORA_SCHEMA "
      whenever sqlerror exit failure \n
      connect sys_ud_refresh/.ora_123@oradev/addbtc.ocslab.com \n
      set serveroutput on size 1000000 lines 999 \n
      exec sys.UD_REFRESH('L', $SCHEMA_NO) \n" || return 1
  fi

  return 0
}

### Copy NB Master Oracle schema to a destination
refresh_oracle_from_nb_master() {
  local SCHEMA_NO=$1 # 0..nn or S[kip]

  [ -z "$SCHEMA_NO" ] && { echo "Undefined SCHEMA_NO" ; exit 1 ; }

  if [ "$SCHEMA_NO" != "S" ] ; then
    local ORA_SCHEMA="NB_COPY$SCHEMA_NO"

    refresh_schema "ora-nb.ocslab.com" 1521 "addbnba.ocslab.com" $ORA_SCHEMA "
      whenever sqlerror exit failure \n
      connect sys_nb_refresh/.ora_123@ora-nb/addbnba.ocslab.com \n
      set serveroutput on size 1000000 lines 999 \n
      exec sys.NBC_REFRESH($SCHEMA_NO) \n" || return 1
  fi

  return 0
}

### Refresh Oracle Java UnitTest schema
refresh_oracle_unittest() {
  local SCHEMA_NO=$1 # Schema, 0..9

  [ -z "$SCHEMA_NO" ] && { echo "Undefined SCHEMA_NO" ; exit 1 ; }

  refresh_schema "ora-nb" 1521 "addbnba.ocslab.com" "UNITTEST_UI_$SCHEMA_NO" "
    whenever sqlerror exit failure \n
    connect sys_unittest_ui_refresh/.ora_123@ora-nb/addbnba.ocslab.com \n
    set serveroutput on size 1000000 lines 999 \n
    exec sys.UNITTEST_UI_REFRESH_PROC(${SCHEMA_NO}) \n" || return 1

  return 0
}

refresh_oracle_from_source() {
  local src="$1"
  local dbn="$2"
  local patch_till="$3"

  [ -z "$src" ] && { echo ": $FUNCNAME: undefined src "; exit 1; }
  [ -z "$dbn" ] && { echo ": $FUNCNAME: undefined dbn "; exit 1; }

  echo ": $FUNCNAME: "
  local result=0

  case $src in
    emergency)
      docr refresh_oracle_from_emergency "$dbn"
      result=$?
    ;;
    test)
      if [ "X$patch_till" = "Xtrunk" ]; then
        docr refresh_oracle_from_test_patched_till_trunk "$dbn"
        result=$?
      else
        docr refresh_oracle_from_test "$dbn"
        result=$?
      fi
    ;;
    test_force_tc)
      docr refresh_oracle_from_test "$dbn"
      result=$?
    ;;
    test_force_lc)
      docr refresh_oracle_from_test_patched_till_trunk "$dbn"
      result=$?
    ;;
    nb)
      docr refresh_oracle_from_nb_master "$dbn"
      result=$?
    ;;
    unittest)
      docr refresh_oracle_unittest "$dbn"
      result=$?
    ;;
    *)
      echo ": $FUNCNAME: unknown source '$dbn'"
      result=1
    ;;
  esac

  echo ": $FUNCNAME: done with $result"
  return $result
}

### Check is the refreshed Oracle schema have the same structure as a source schema
compare_schemas_structure() {
  local HOST1=$1
  local PORT1=$2
  local DB1=$3
  local SCHEMA1=$4

  local HOST2=$5
  local PORT2=$6
  local DB2=$7
  local SCHEMA2=$8

  [ -z "$HOST1" ] && { echo ": $FUNCNAME: Undefined HOST1" ; exit 1 ; }
  [ -z "$PORT1" ] && { echo ": $FUNCNAME: Undefined PORT1" ; exit 1 ; }
  [ -z "$DB1" ] && { echo ": $FUNCNAME: Undefined DB1" ; exit 1 ; }
  [ -z "$SCHEMA1" ] && { echo ": $FUNCNAME: Undefined SCHEMA1" ; exit 1 ; }

  [ -z "$HOST2" ] && { echo ": $FUNCNAME: Undefined HOST2" ; exit 1 ; }
  [ -z "$PORT2" ] && { echo ": $FUNCNAME: Undefined PORT2" ; exit 1 ; }
  [ -z "$DB2" ] && { echo ": $FUNCNAME: Undefined DB2" ; exit 1 ; }
  [ -z "$SCHEMA2" ] && { echo ": $FUNCNAME: Undefined SCHEMA2" ; exit 1 ; }

  doc checkout_file "svn+ssh://svn/home/svnroot/oix/ui/trunk/utility/db/check-copied-db/check-copied-db.sql"

  doc apply_sql $HOST1 $PORT1 $DB1 $SCHEMA1 $CHECKOUT_FILE
  local LOG1=$WORKING_DIR/$SCHEMA1.txt
  mv $CHECKOUT_FILE.log $LOG1
  echo "$SCHEMA1/adserver@//$HOST1/$DB1"
  cat $LOG1 ; echo

  doc apply_sql $HOST2 $PORT2 $DB2 $SCHEMA2 $CHECKOUT_FILE
  local LOG2=$WORKING_DIR/$SCHEMA2.txt
  mv $CHECKOUT_FILE.log $LOG2
  echo "$SCHEMA2/adserver@//$HOST2/$DB2"
  cat $LOG2 ; echo

  echo "Diff:"
  diff $LOG1 $LOG2 2>&1 && echo "DB schema has been copied correctly!" || { echo ": ERROR in schema copying!" && return 1 ; }

  return 0
}

# determine ORA_HOST and ORA_INSTANCE by SCHEMA
get_oracle_host_and_instance() {
  local SCHEMA=$1

  ORA_HOST="oradev.ocslab.com"
  ORA_INSTANCE="addbtc.ocslab.com"
  if [ "${SCHEMA:0:11}" = "UNITTEST_UI" ] || [ "${SCHEMA:0:7}" = "NB_COPY" ] ; then
    ORA_HOST="ora-nb.ocslab.com"
    ORA_INSTANCE="addbnba.ocslab.com"
  fi
}

# set ORA_DB_BRANCH
get_oracle_db_branch() {
  local database=$1
  [ -z $database ] && { echo "$0: Undefined database"; exit 1; }
  case $database in
    test) local connection="ADSERVER_RO/adserver@//oracle.ocslab.com:1521/addbtest.ocslab.com";;
    emergency) local connection="ADSERVER_RO/adserver@//oradem.ocslab.com:1621/addbet.ocslab.com";;
    *) echo "$0: Unknown database $database"; exit 1;;
  esac

  local version=$(echo -e "SET TIMING OFF\nSET NEWPAGE 0\nSET SPACE 0\n
                           SET LINESIZE 80\nSET PAGESIZE 0\nSET ECHO OFF\n
                           SET FEEDBACK OFF\nSET VERIFY OFF\nSET HEADING OFF\n
                           SET MARKUP HTML OFF SPOOL OFF\n
                           select regexp_replace(build_num, '\.[0-9]*$', '')
                           from (select build_num from schema_applied_patches
                           order by apply_date desc) where rownum = 1;" |
                  sqlplus -SL $connection)
  local result=$?
  if [ "$result" = "0" ]; then
    echo  $version > $WORKING_DIR/$database.oracle.version
    put_to_store "get_db_version" $WORKING_DIR/$database.oracle.version
  else
    get_from_store "get_db_version" $WORKING_DIR/$database.oracle.version
    local version=`cat $WORKING_DIR/$database.oracle.version 2>/dev/null`
  fi
  ORA_DB_BRANCH=$version
}

get_oracle_db_tag() {
  local connection="$1"
  [ -z $connection ] && { echo "$0: Undefined connection"; exit 1; }

  local version=$(echo -e "SET TIMING OFF\nSET NEWPAGE 0\nSET SPACE 0\n
                           SET LINESIZE 80\nSET PAGESIZE 0\nSET ECHO OFF\n
                           SET FEEDBACK OFF\nSET VERIFY OFF\nSET HEADING OFF\n
                           SET MARKUP HTML OFF SPOOL OFF\n
                           select build_num
                           from (select build_num from schema_applied_patches
                           where product = 'forosdb'
                           order by apply_date desc) where rownum = 1;" |
                  sqlplus -SL $connection)
  echo $version | grep -qE '([0-9]+\.[0-9]+\.[0-9]\.+[0-9]+)'
  local result=$?
  ORA_DB_TAG=$version
  return $result
}

### RETURNS:
###   ORA_VERSION (for example, '3.1.0.4')
get_ora_version() {
  local host=$1
  local port=$2
  local db=$3
  local schema=$4
  local product=$5

  [ -z "$host" ] && { echo ": $FUNCNAME: Undefined host" ; exit 1 ; }
  [ -z "$port" ] && { echo ": $FUNCNAME: Undefined port" ; exit 1 ; }
  [ -z "$db" ] && { echo ": $FUNCNAME: Undefined db" ; exit 1 ; }
  [ -z "$schema" ] && { echo ": $FUNCNAME: Undefined schema" ; exit 1 ; }
  [ -z "$product" ] && { echo ": $FUNCNAME: Undefined product" ; exit 1 ; }

  apply_sql_command "$host" $port "$db" "$schema" \
    "select * from (select build_num from schema_applied_patches where product='$product'
     order by apply_date desc) t where rownum = 1" > $WORKING_DIR/ora_version.log || exit 1
  ORA_VERSION=`cat $SQL_FILE.log`

  return 0
}
