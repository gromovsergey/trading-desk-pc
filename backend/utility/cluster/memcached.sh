WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $CONF_DIR/memcached
operation=$1

[ -z "$hosts_and_ports" ] && { echo ": $0: Undefined hosts_and_ports" ; exit -1 ; }
[ -z "$cache_size" ] && { echo ": $0: Undefined cache_size" ; exit -1 ; }
[ -z "$value_size" ] && { echo ": $0: Undefined value_size" ; exit -1 ; }
[ -z "$connections_count" ] && { echo ": $0: Undefined connections_count" ; exit -1 ; }
[ -z "$effective_user" ] && { echo ": $0: Undefined effective_user" ; exit -1 ; }
[ -z "$pid_file" ] && { echo ": $0: Undefined pid_file" ; exit -1 ; }
[ -z "$log_folder" ] && { echo ": $0: Undefined log_folder" ; exit -1 ; }

get_port() {
  local hosts_n_ports=$1
  local host=$2
  port=`echo $hosts_n_ports | sed -e "s/.*$host:\([0-9]\+\).*/\1/g" | sed -e "s/[^0-9]\+//g"`
  if [ "$port" = "" ]; then
    log "Invalid configuration. Have no port for host $host"
    return 1
  fi
}

start() {
  rm -f $pid_file 2>/dev/null
  get_port $hosts_and_ports `hostname` || return 1
  /usr/bin/memcached -d -P "$pid_file" -p "$port" -U 0 \
    -u "$effective_user" -m "$cache_size" -I "${value_size}m" \
    -c "$connections_count" "$verbosity" >>$log_folder/memcached.log 2>&1 &
  wait_file "$pid_file" && log_silent "PID: `cat $pid_file`"
}

stop() {
  local PID=`cat $pid_file 2>/dev/null`
  log_silent "PID: $PID"
  kill "$PID" && wait_pid "$PID" && rm -f "$pid_file"
}

status() {
  local PID=`cat $pid_file 2>/dev/null`
  log_silent "PID: $PID"
  kill -s 0 $PID 2>/dev/null
}

log_before $log_folder $@
check_user $effective_user
check_operation $operation

[ "$operation" = "start" ] && { start ; result=$? ; }
[ "$operation" = "status" ] && { status ; result=$? ; }
[ "$operation" = "stop" ] && { stop ; result=$? ; }

log_after $result

exit $result
