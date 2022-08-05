#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $CONF_DIR/apache_scp
operation=$1
name=$2

[ -z "$server_root" ] && { echo ": $0: Undefined server_root" ; exit -1 ; }
[ -z "$pid_file" ] && { echo ": $0: Undefined pid_file" ; exit -1 ; }
[ -z "$effective_user" ] && { echo ": $0: Undefined effective_user" ; exit -1 ; }
[ -z "$log_folder" ] && { echo ": $0: Undefined log_folder" ; exit -1 ; }
[ -z "$name" ] && { echo ": $0: Undefined name" ; exit -1 ; }

server_root=$server_root/$name
[ ! -d "$server_root/conf.d" ] && { echo ": $0: Unknown name: $name" ; exit -1 ; }
log_folder=`echo $log_folder | sed -e "s|%name%|$name|g"`
pid_file=`echo $pid_file | sed -e "s|%name%|$name|g"`

start() {
  $HTTPD_BIN -C "Include ${CENTOS_RELEASE}.conf" -e error -d $server_root -k graceful &
  wait_file "$pid_file" && log_silent "PID: `cat $pid_file`"
}

stop() {
  local PID=`cat $pid_file 2>/dev/null`
  log_silent "PID: $PID"
  $HTTPD_BIN -C "Include ${CENTOS_RELEASE}.conf" -e error -d $server_root -k stop && \
  wait_pid "$PID" && \
  rm -f "$pid_file" &
}

status() {
  local PID=`cat $pid_file 2>/dev/null`
  log_silent "PID: $PID"
  /usr/bin/test -n "$PID" && /bin/kill -s 0 "$PID"
}

log_before $log_folder $@
check_user $effective_user
check_operation $operation
get_centos_release
get_httpd_bin

[ "$operation" = "start" ] && { start ; result=$? ; }
[ "$operation" = "status" ] && { status ; result=$? ; }
[ "$operation" = "stop" ] && { stop ; result=$? ; }

log_after $result

exit $result
