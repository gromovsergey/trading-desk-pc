#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $CONF_DIR/glassfish_service

operation=$1
host=$2

[ -z "$domains_dir" ] && { echo ": $0: Undefined domains_dir" ; exit -1 ; }
[ -z "$glassfish_pswd_file" ] && { echo ": $0: Undefined glassfish_pswd_file" ; exit -1 ; }
[ -z "$admin_gui_enabled" ] && { echo ": $0: Undefined admin_gui_enabled" ; exit -1 ; }
[ -z "$effective_user" ] && { echo ": $0: Undefined effective_user" ; exit -1 ; }
[ -z "$log_folder" ] && { echo ": $0: Undefined log_folder" ; exit -1 ; }
[ -z "$tmp_dir" ] && { echo ": $0: Undefined tmp_dir" ; exit -1 ; }
[ -z "$pg_host" ] && { echo ": $0: Undefined pg_host" ; exit -1 ; }
[ -z "$pg_port" ] && { echo ": $0: Undefined pg_port" ; exit -1 ; }
[ -z "$pg_db" ] && { echo ": $0: Undefined pg_db" ; exit -1 ; }
[ -z "$pg_user" ] && { echo ": $0: Undefined pg_user" ; exit -1 ; }
[ -z "$pg_password" ] && { echo ": $0: Undefined pg_password" ; exit -1 ; }

export LD_LIBRARY_PATH=/opt/foros/ui/lib
domain_name="domain1"
domain_folder="$domains_dir/$domain_name"

create_domain() {
  # If Glassfish wasn't stopped correctly, remove domain folder
  if [ -e "$domain_folder" ] ; then
    local glassfish_pid=`cat "$domain_folder/config/pid"`
    ps --pid $glassfish_pid >/dev/null 2>&1
    if [ "$?" != "0" ] ; then
      log "Warning: Glassfish wasn't stopped correctly, removing domain folder '$domain_folder'"
      delete_domain
    fi
  fi

  mkdir -p "$domains_dir" && \
  /opt/glassfish/bin/asadmin \
    --user admin \
    --passwordfile "$glassfish_pswd_file" \
    create-domain \
    --domaindir "$domains_dir" \
    $domain_name >/dev/null
  if [ "$?" != 0 ]; then
    log "Error: can't create '$domains_dir'"
    exit -1
  fi

  return 0
}

get_pgdb_version() {
  local version
  local result
  local pgpassfile
  pgpassfile=`mktemp -p $tmp_dir pgpass.XXXXX`
  [ "$?" != "0" ] && { log "Could not create pgpassfile"; exit -1; }
  run chmod 600 $pgpassfile || \
    { log "Could not change permissions for pgpassfile"; exit -1; }
  echo "$pg_host:$pg_port:$pg_db:$pg_user:$pg_password" > $pgpassfile
  export PGPASSFILE=$pgpassfile
  version=`run_and_log_errors psql \
             -d $pg_db -h $pg_host -p $pg_port \
             -U $pg_user -tA -w -c "select\ get_version\(\'statdb\'\)\;"`
  result=$?
  run rm -f $pgpassfile
  [ "$result" != "0" ] && { log "Could not get version of PGDB"; exit -1; }
  PGDB_VERSION=$version
  return 0
}

configure_domain() {
  # Copy domain's config files, lib jars etc
  cp -Rf /opt/foros/ui/lib/domains/$domain_name/* "$domain_folder"
  if [ "$?" != 0 ]; then
    log "Error: can't copy FOROS UI config to '$domain_folder'"
    exit -1
  fi

  # Make a symlink to logs
  local ui_log_dir="/opt/foros/ui/var/log/$domain_name"
  local glassfish_log_dir="$domain_folder/logs"
  mkdir -p "$ui_log_dir"
  [ -d "$glassfish_log_dir" ] && rm -rf "$glassfish_log_dir"
  [ -L "$glassfish_log_dir" ] || ln -s "$ui_log_dir" "$glassfish_log_dir"
  if [ "$?" != 0 ]; then
    log "Error: can't create symlink: $glassfish_log_dir -> $ui_log_dir"
    exit -1
  fi

  # Replace domain.xml with the pre-configured one
  local glassfish_conf_dir="$domain_folder/config"
  cp "$glassfish_conf_dir/domain.$host.xml" "$glassfish_conf_dir/domain.xml"
  if [ "$?" != 0 ]; then
    log "Can't copy $glassfish_conf_dir/domain.$host.xml to $glassfish_conf_dir/domain.xml"
    exit -1
  fi

  # Add pgdb.version system property
  get_pgdb_version
  sed -e \
    "0,/\(<system-property .*\/>\)/s//<system-property name=\"pgdb.version\" value=\"$PGDB_VERSION\"\/>\n\n      \1/" \
    -i $glassfish_conf_dir/domain.xml
  if [ "$?" != 0 ]; then
    log "Can't add pgdb.version system-property to $glassfish_conf_dir/domain.xml"
    exit -1
  fi

  # Regenerate certificates to enable JMX (at 8686 port)
  # Re-create keystore.jks
  rm $glassfish_conf_dir/keystore.jks
  keytool -genkeypair -alias s1as -dname "CN=$host, OU=GlassFish, O=Oracle Corporation, L=Santa Clara, ST=California, C=US" -keyalg RSA -keysize 2048 -validity 3650 -keystore $glassfish_conf_dir/keystore.jks -keypass changeit -storepass changeit
  keytool -genkeypair -alias glassfish-instance -dname "CN=$host, OU=GlassFish, O=Oracle Corporation, L=Santa Clara, ST=California, C=US" -keyalg RSA -keysize 2048 -validity 3650 -keystore $glassfish_conf_dir/keystore.jks -keypass changeit -storepass changeit
  keytool -export -alias s1as -file $glassfish_conf_dir/s1as.cert -keystore $glassfish_conf_dir/keystore.jks -storepass changeit > /dev/null
  keytool -export -alias glassfish-instance -file $glassfish_conf_dir/glassfish-instance.cert -keystore $glassfish_conf_dir/keystore.jks -storepass changeit > /dev/null
  log "$glassfish_conf_dir/keystore.jks re-created"

  if ! cp -p $glassfish_pswd_file $domain_folder/config/pswd; then
      log ": $0: Can't copy glassfish password file"
      exit -1
  fi

  return 0
}

start_domain() {
  /opt/glassfish/bin/asadmin start-domain \
    --domaindir "$domains_dir" \
    "$domain_name"
  if [ "$?" != "0" ] ; then
    log ": $0: Can't start DAS"
    exit -1
  fi

  # To enable Admin GUI (at https://<host>:4848) we must enable secure admin option
  if [ "$admin_gui_enabled" = "true" ] ; then
    /opt/glassfish/bin/asadmin --user admin --passwordfile $glassfish_pswd_file enable-secure-admin
    if [ "$?" != "0" ] ; then
      log ": $0: Can't enable-secure-admin"
      exit -1
    fi

    /opt/glassfish/bin/asadmin restart-domain \
      --domaindir "$domains_dir" \
      "$domain_name"
    if [ "$?" != "0" ] ; then
      log ": $0: Can't restart-domain"
      exit -1
    fi
  fi

  return 0
}

stop_domain_forcibly() {
  local glassfish_pid=`cat "$domain_folder/config/pid"`
  local timestamp=`date +%d%m%y.%H%M%S`

  log "Warning: killing glassfish"
  run cp $domain_folder/logs/server.log $log_folder/${timestamp}.server.log
  run_and_log_errors jstack $glassfish_pid > $log_folder/${timestamp}.jstack.log
  run_and_log_errors ps x > $log_folder/${timestamp}.ps.log
  run kill -9 $glassfish_pid
  return $?
}

stop_domain() {
  /opt/glassfish/bin/asadmin \
    --user admin \
    --passwordfile "$domain_folder/config/pswd" \
    stop-domain \
    --domaindir "$domains_dir" \
    "$domain_name"
  if [ "$?" != 0 ]; then
    log "Can't stop DAS"
    stop_domain_forcibly
    return $?
  fi

  return 0
}

delete_domain() {
  rm -rf $domain_folder
  if [ "$?" != 0 ]; then
    log "Warning: can't delete $domain_folder"
  fi
  return 0
}

start() {
  create_domain
  configure_domain
  start_domain
  return 0
}

stop() {
  stop_domain
  delete_domain
  return 0
}

status() {
  local domain_status=`/opt/glassfish/bin/asadmin list-domains --domaindir "$domains_dir" | grep -v 'domains' | egrep -iv 'not|No Domains to list'`
  [ -n "$domain_status" ]
}


log_before $log_folder $@
check_user $effective_user
check_operation $operation

[ "$operation" = "start" ] && { start ; result=$? ; }
[ "$operation" = "status" ] && { status ; result=$? ; }
[ "$operation" = "stop" ] && { stop ; result=$? ; }

log_after $result

exit $result
