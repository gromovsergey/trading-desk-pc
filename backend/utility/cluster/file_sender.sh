#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $CONF_DIR/file_sender
operation=$1

[ -z "$file_sender_conf" ] && { echo ": $0: Undefined file_sender_conf" ; exit -1 ; }
[ -z "$pid_file" ] && { echo ": $0: Undefined pid_file" ; exit -1 ; }
[ -z "$effective_user" ] && { echo ": $0: Undefined effective_user" ; exit -1 ; }
[ -z "$log_folder" ] && { echo ": $0: Undefined log_folder" ; exit -1 ; }

start() {
  rm -f $pid_file 2>/dev/null
  /usr/bin/rsync --daemon --config=$file_sender_conf </dev/null >/dev/null 2>/dev/null
  log_silent "PID: `cat $pid_file 2>/dev/null`"
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
