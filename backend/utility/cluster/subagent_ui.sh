#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $CONF_DIR/subagent_ui
operation=$1

[ -z "$snmp_folder" ] && { echo ": $0: Undefined snmp_folder" ; exit -1 ; }
[ -z "$pid_file" ] && { echo ": $0: Undefined pid_file" ; exit -1 ; }
[ -z "$effective_user" ] && { echo ": $0: Undefined effective_user" ; exit -1 ; }
[ -z "$log_folder" ] && { echo ": $0: Undefined log_folder" ; exit -1 ; }

start() {
  /usr/bin/subagent-shell -b $snmp_folder &>$log_folder/run.log &
  local pid="$!"
  log_silent "PID: $pid"
  echo "$pid" >$pid_file
}

stop() {
  local pid=`cat $pid_file 2>/dev/null`
  log_silent "PID: $pid"
  kill "$pid" 2>/dev/null && wait_pid "$pid" && rm -f "$pid_file"
}

status() {
  local pid=`cat $pid_file 2>/dev/null`
  log_silent "PID: $pid"
  kill -s 0 "$pid" 2>/dev/null
}

log_before $log_folder $@
check_user $effective_user
check_operation $operation

[ "$operation" = "start" ] && { start ; result=$? ; }
[ "$operation" = "status" ] && { status ; result=$? ; }
[ "$operation" = "stop" ] && { stop ; result=$? ; }

log_after $result

exit $result

