#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $CONF_DIR/create_symlinks

[ -z "$symlink_base_dir" ] && { echo ": $0: Undefined symlink_base_dir" ; exit -1 ; }
[ -z "$symlink_folder_list" ] && { echo ": $0: Undefined symlink_folder_list" ; exit -1 ; }
[ -z "$log_folder" ] && { echo ": $0: Undefined log_folder" ; exit -1 ; }

create_symlinks() {
  local folder
  local sync_folder
  for folder in $symlink_folder_list ; do
    sync_folder=${folder##*/}
    if [ -h "$symlink_base_dir/www/$folder" ]; then
      run rm $symlink_base_dir/www/$folder
      if [ "$?" != "0" ]; then
        log ": $0: Can't remove old symlink. See $SCRIPT_LOG_FILE for details"
        exit -1
      fi
    fi

    run ln -s $symlink_base_dir/sync/$sync_folder $symlink_base_dir/www/$folder
    if [ "$?" != "0" ]; then
      log ": $0: Can't create a symlink. See $SCRIPT_LOG_FILE for details"
      exit -1
    fi
  done
  return 0
}

log_before $log_folder $@

create_symlinks

log_after 0

exit 0
