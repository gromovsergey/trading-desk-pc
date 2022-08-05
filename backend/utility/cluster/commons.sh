#!/bin/bash

# Define configuration folder
CONF_DIR=/opt/foros/ui/etc/conf

### Executes a command and redirects
### ALL its output (out, err) to the log file
run() {
  [ -z "$log_folder" ] && { echo ": $0/$FUNCNAME: Undefined log_folder" ; exit -1 ; }

  local log_file=$log_folder/run.log
  local cmd="$@ >>$log_file 2>&1"

  echo `date` [`whoami`@`hostname` `pwd`]"\$ $cmd" >>$log_file
  eval $cmd
  local result=$?
  echo `date` [`whoami`@`hostname` `pwd`]"\$ result=$result" >>$log_file

  return $result
}

### Executes a command and redirects
### ONLY err to the log file
run_and_log_errors() {
  [ -z "$log_folder" ] && { echo ": $0/$FUNCNAME: Undefined log_folder" ; exit -1 ; }

  local log_file=$log_folder/run.log
  local cmd="$@ 2>>$log_file"

  echo `date` [`whoami`@`hostname` `pwd`]"\$ $cmd" >>$log_file
  eval $cmd
  local result=$?
  echo `date` [`whoami`@`hostname` `pwd`]"\$ result=$result" >>$log_file

  return $result
}

### Rotate file
rotate_file() {
  local file=$1
  local maxsize=$2
  local count=$3

  [ -z $file ] && { echo ": $0/$FUNCNAME: Undefined file"; exit -1; }
  [ -z $maxsize ] && maxsize=1048576
  [ -z $count ] && count=5

  if [ -f $file ]; then
    local size=`stat -c '%s' $file`
    if [ $size -gt $maxsize ]; then
      local i
      for i in `seq 1 $count`; do
        local from; let from=$count-$i
        local to; let to=$count+1-$i
        [ -f ${file}.${from} ] && mv ${file}.${from} ${file}.${to}
      done
      cp $file ${file}.1
      : > $file
    fi
  fi

  return 0
}

### Creates global variable SCRIPT_LOG_FILE
### It keeps script's log file name
log_before() {
  local log_folder=$1 ; shift
  local args="$@"

  [ -z "$log_folder" ] && { echo ": $0/$FUNCNAME: Undefined log_folder" ; exit -1 ; }

  SCRIPT_LOG_FILE=$log_folder/`basename $0`".log"
  rotate_file $SCRIPT_LOG_FILE
  echo >>$SCRIPT_LOG_FILE
  echo `date` [`whoami`@`hostname` `pwd`]"\$ $0 $args" >>$SCRIPT_LOG_FILE
}

check_user() {
  local effective_user=$1

  [ -z "$effective_user" ] && { log ": $0/$FUNCNAME: Undefined effective_user" ; exit -1 ; }

  if [ "$USER" != "$effective_user" ]; then
    log "Only '$effective_user' user can run the script"
    exit -1
  fi
}

check_operation() {
  local operation=$1

  if [ "$operation" != "start" -a "$operation" != "stop" -a "$operation" != "status" ] ; then
    log "Usage: `basename $0` start | stop | status"
    exit -1
  fi
}

log() {
  local msg="$1"

  echo `date` [`whoami`@`hostname` `pwd`]"\$ $0: $msg" >> $SCRIPT_LOG_FILE
  echo $msg
}

log_silent() {
  local msg=$1
  log "$msg" &>/dev/null
}

log_after() {
  local result=$1

  [ -z "$result" ] && { echo ": $0/$FUNCNAME: Undefined result" ; exit -1 ; }

  echo `date` [`whoami`@`hostname` `pwd`]"\$ $0: Done, result=$result" >>$SCRIPT_LOG_FILE
}

wait_pid() {
  local PID=$1
  [ -n "$PID" ] || return 0
  while kill -0 "$PID" >/dev/null 2>&1; do
    sleep 0.5
  done
  return 0
}

wait_file() {
  local filename=$1
  while [ ! -f "$filename" ]; do
    sleep 0.5
  done
  return 0
}

get_centos_release() {
  CENTOS_RELEASE=`rpm -q --qf '%{release}\n' coreutils |\
                  sed -e 's|^.*\(el[0-9]\+\).*$|\1|'`
  [ -z $CENTOS_RELEASE ] && { echo ": $FUNCNAME: could not get centos release"; exit 1; }
}

get_httpd_bin() {
  get_centos_release
  local bin="/usr/sbin/httpd"
  if [ "X${CENTOS_RELEASE}" = "Xel7" ]; then
    HTTPD_BIN="${bin}"
  else
    HTTPD_BIN="${bin}.worker.x86_64"
  fi
  log_silent "$FUNCNAME: $HTTPD_BIN"
}

