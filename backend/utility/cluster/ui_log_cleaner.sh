#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $CONF_DIR/ui_log_cleaner

[ -z "$log_keep_interval" ] && { echo ": $0: Undefined log_keep_interval" ; exit -1 ; }
[ -z "$log_root_folder" ] && { echo ": $0: Undefined log_root_folder" ; exit -1 ; }
[ -z "$log_folder" ] && { echo ": $0: Undefined log_folder" ; exit -1 ; }

log_before $log_folder $@

result=0
if [ -z "$log_keep_interval" ] || [ $log_keep_interval -lt 1 ]; then
  log ": $0: log_keep_interval=$log_keep_interval is too short"
else
  run "/usr/bin/find -L $log_root_folder -name '*log*' -not -path */migration/* -type f -mtime +$log_keep_interval -delete"
  result=$?
fi

log_after $result

exit $result
