#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $CONF_DIR/check_dirs_sync

[ -z "$private_ssh_key_file" ] && { echo ": $0: Undefined private_ssh_key_file" ; exit -1 ; }
[ -z "$glassfish_hosts" ] && { echo ": $0: Undefined glassfish_hosts" ; exit -1 ; }
[ -z "$mounted_dirs" ] && { echo ": $0: Undefined mounted_dirs" ; exit -1 ; }
[ -z "$effective_user" ] && { echo ": $0: Undefined effective_user" ; exit -1 ; }
[ -z "$log_folder" ] && { echo ": $0: Undefined log_folder" ; exit -1 ; }

### Creates global variables
###   MASTER_HOST
###   TRANSPORT
check_master_host() {
  MASTER_HOST=""

  local mounted
  for host in $glassfish_hosts; do
    mounted=`ssh -i "$private_ssh_key_file" -o 'BatchMode yes' "$effective_user"@"$host" mount | grep "$mounted_dirs" 2>/dev/null`
    ssh -i "$private_ssh_key_file" -o 'BatchMode yes' "$effective_user"@"$host" echo -n
    if [ "$?" == "0" ] && [ -z "$MOUNTED" ] ; then
      log ": $0: Synchronization: $host (main)"
      MASTER_HOST="$host"
      break;
    fi
  done

  if [ -z "$MASTER_HOST" ]; then
    log "Synchronization: main host not found"
    TRANSPORT=""
    MASTER_HOST="localhost"
  else
    TRANSPORT="ssh -i \"$private_ssh_key_file\" -o 'BatchMode yes' \"$effective_user\"@\"$host\""
  fi
  return 0
}

check_sync() {
  local result=0
  local sync_dirs="/opt/foros/ui/var/sync
    /opt/foros/ui/var/www"
  local test_file
  for sync_dir in $sync_dirs ; do
    test_file="$sync_dir"/check-dirs-sync-test-$$
    if [ -f $test_file ]; then
      log ": $0: Synchronization: utility already running ('$test_file' was found)"
      exit -1
    fi

    eval "$TRANSPORT touch \"$test_file\""
    if [ "$?" != "0" ]; then
      log ": $0: Synchronization: can't create '$test_file' on '$MASTER_HOST' host)"
      exit -1
    fi

    for host in $glassfish_hosts; do
      ssh -i "$private_ssh_key_file" -o 'BatchMode yes' "$effective_user"@"$host" ls "$test_file" >/dev/null
      if [ "$?" != "0" ]; then
        log ": $0: Synchronization: $host:$sync_dir (failed)"
        result=-1
      else
        log ": $0: Synchronization: $host:$sync_dir (passed)"
      fi
    done

    eval "$TRANSPORT rm -f \"$test_file\""
  done
  return $result
}

log_before $log_folder $@

glassfish_hosts=`echo $glassfish_hosts | sed -e "s/,/\n/g"`
check_master_host
check_sync
result=$?

log_after $result

exit $result
