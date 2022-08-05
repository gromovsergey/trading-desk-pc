#!/bin/sh

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $CONF_DIR/oui_jmx_status

[ -z "$glassfish_jmx_monitoring_properties" ] && { echo ": $0: Undefined glassfish_jmx_monitoring_properties" ; exit -1 ; }
[ -z "$monitored_pid_file" ] && { echo ": $0: Undefined monitored_pid_file" ; exit -1 ; }
[ -z "$effective_user" ] && { echo ": $0: Undefined effective_user" ; exit -1 ; }
[ -z "$log_folder" ] && { echo ": $0: Undefined log_folder" ; exit -1 ; }

log_before $log_folder $@
check_user $effective_user

result=0
glassfish_pid=`cat "/opt/foros/ui/var/domains/domain1/config/pid" 2>/dev/null`
if [ -n "$glassfish_pid" ] ; then
  # check is it the same monitored JVM process or not
  is_new_process="0"
  monitored_pid=`cat $monitored_pid_file`
  if [ -z "$monitored_pid" ] || [ "$glassfish_pid" != "$monitored_pid" ] ; then
    echo $glassfish_pid > $monitored_pid_file
    is_new_process="1"
  fi

  JAVAC_FILENAME=$(readlink -f `which javac`)
  JAVAC_FOLDER=`dirname "$JAVAC_FILENAME"`
  JDK_HOME=`expr "$JAVAC_FOLDER" : '\(.*\)/bin'`
  run_and_log_errors \
    java -classpath "$JDK_HOME/lib/tools.jar:/opt/foros/ui/lib/jmx-client/jmx-client.jar" \
      com.foros.ssj.Main $glassfish_pid $glassfish_jmx_monitoring_properties $is_new_process
  result=$?
else
  log "Glassfish is not running"
fi

log_after $result

exit $result

