#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $CONF_DIR/webapp_service
operation=$1
host=$2

[ -z "$effective_user" ] && { echo ": $0: Undefined effective_user" ; exit -1 ; }
[ -z "$log_folder" ] && { echo ": $0: Undefined log_folder" ; exit -1 ; }
[ -z "$domains_dir" ] && { echo ": $0: Undefined domains_dir" ; exit -1 ; }

export LD_LIBRARY_PATH=/opt/foros/ui/lib
domain_name="domain1"
domain_folder="$domains_dir/$domain_name"
glassfish_pswd_file="$domain_folder/config/pswd"

try_kill_glassfish() {
  local glassfish_pid=`cat "$domain_folder/config/pid"`

  if [ -n "$glassfish_pid" ]; then
    log "Force stop requested. Killing glassfish... "
    kill -9 $glassfish_pid
    if [ "$?" == 0 ] ; then
      log "done."
      exit 0
    else
      log "failed."
    fi
  fi
}

is_deployed() {
  local app=$1
  /opt/glassfish/bin/asadmin \
    --passwordfile "$glassfish_pswd_file" \
    list-applications | grep $app 2>&1 > /dev/null
}

start() {
  local result=0

  local module
  for module in `ls /opt/foros/ui/lib/autodeploy/`; do
    local app=${module%.*}

    if is_deployed $app ; then
      log "Application $app is already deployed. Skipping."
    else
      log "Start deploying application $app"
      /opt/glassfish/bin/asadmin \
        --passwordfile "$glassfish_pswd_file" \
        deploy \
        --name $app \
        /opt/foros/ui/lib/autodeploy/$module
      result=$?

      [ "$result" = "0" ] && { log "Application $app is deployed successfully" ; continue; }
      [ "$result" = "1" ] && { log "Application $app is failed to deploy" ; exit -1; }
    fi
  done

  return 0
}

stop() {
  local result=0

  local module
  for module in `ls /opt/foros/ui/lib/autodeploy/`; do
    local app=${module%.*}

    if is_deployed $app ; then
      log "Start undeploying application $app"
      /opt/glassfish/bin/asadmin \
        --passwordfile "$glassfish_pswd_file" \
        undeploy \
        $app
      result=$?

      [ "$result" = "0" ] && { log "Application $app is undeployed successfully."; continue; }
      [ "$result" = "1" ] && { log "Failed to undeploy $app" ; try_kill_glassfish; exit 1; }
    else
      log "Application $app is not deployed"
    fi
  done

  return 0
}

status() {
  local base_url="https://localhost:8181/login/login"
  local status=`curl -s -L -k $base_url -o /dev/null -w '%{http_code}' | grep '200'`
  [ -n "$status" ]
}

log_before $log_folder $@
check_user $effective_user
check_operation $operation

[ "$operation" = "start" ] && { start ; result=$? ; }
[ "$operation" = "status" ] && { status ; result=$? ; }
[ "$operation" = "stop" ] && { stop ; result=$? ; }

log_after $result

exit $result
