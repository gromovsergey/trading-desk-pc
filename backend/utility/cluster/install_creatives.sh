#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $CONF_DIR/install_creatives

[ -z "$creatives_src" ] && { echo ": $0: Undefined creatives_src" ; exit -1 ; }
[ -z "$creatives_dst" ] && { echo ": $0: Undefined creatives_dst" ; exit -1 ; }
[ -z "$log_folder" ] && { echo ": $0: Undefined log_folder" ; exit -1 ; }
[ -z "$preview_folder" ] && { echo ": $0: Undefined preview_folder" ; exit -1 ; }

remove_previews() {
  run "rm -rf $preview_folder/*"
  if [ "$?" != "0" ]; then
    log ": $0: Can't remove the creatives from preview folder $preview_folder. See $SCRIPT_LOG_FILE for details"
    exit -1
  fi
  return 0
}

copy_creatives() {
  run cp -ru $creatives_src/* $creatives_dst/
  if [ "$?" != "0" ]; then
    log ": $0: Can't copy the creatives. See $SCRIPT_LOG_FILE for details"
    exit -1
  fi
  return 0
}

log_before $log_folder $@

remove_previews
copy_creatives

log_after 0

exit 0
