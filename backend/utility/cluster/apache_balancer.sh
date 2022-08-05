#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
operation="$1"
mode="$2"
host="$3"
port="$4"
. $CONF_DIR/apache_balancer_${host}_${port}

[ -z "$server_root" ] && { echo ": $0: Undefined server_root" ; exit -1 ; }
[ -z "$balancer_conf" ] && { echo ": $0: Undefined balancer_conf" ; exit -1 ; }
[ -z "$maintenance_html_name" ] && { echo ": $0: Undefined maintenance_html_name" ; exit -1 ; }
[ -z "$log_folder" ] && { echo ": $0: Undefined log_folder" ; exit -1 ; }
[ -z "$pid_file" ] && { echo ": $0: Undefined pid_file" ; exit -1 ; }

BALANCER_MODE="###BALANCER MODE###"
MAINTENANCE_MODE="###MAINTENANCE MODE###"

BEGIN_MARK="###MODE CODE BEGIN###"
END_MARK="###MODE CODE END###"

BALANCER_CODE="$BALANCER_MODE
  JkMount /* loadbalancer"

MAINTENANCE_HTML_DIR=`dirname "$maintenance_html_name"`
MAINTENANCE_CODE="$MAINTENANCE_MODE
  DocumentRoot $MAINTENANCE_HTML_DIR/
  AddHandler type-map .var
  DirectoryIndex $maintenance_html_name.var
  Header unset Last-Modified
  ExpiresActive On
  ExpiresDefault A1
  Header unset Cache-Control
  Header append Cache-Control \"no-cache, must-revalidate\"
  ErrorDocument 503 $maintenance_html_name.var

  RewriteCond %{ENV:REDIRECT_STATUS} !=503
  RewriteRule !^/maintenance(.*)\$ /maintenance\$1 [L,R=503]

  <Directory \"$MAINTENANCE_HTML_DIR/\">
    Options FollowSymLinks
    AllowOverride None
    Order allow,deny
    Allow from all
  </Directory>

  AliasMatch .*maintenance\.html         \"$maintenance_html_name.html\"
  AliasMatch .*maintenance\.ru\.html     \"$maintenance_html_name.ru.html\"
  AliasMatch .*maintenance\.ko\.html     \"$maintenance_html_name.ko.html\"
  AliasMatch .*maintenance\.ja\.html     \"$maintenance_html_name.ja.html\"
  AliasMatch .*maintenance\.pt\.html     \"$maintenance_html_name.pt.html\"
  AliasMatch .*maintenance\.tr\.html     \"$maintenance_html_name.tr.html\"
  AliasMatch .*maintenance\.ro\.html     \"$maintenance_html_name.ro.html\"
  AliasMatch .*maintenance\.zh\.html     \"$maintenance_html_name.zh.html\"
  AliasMatch .*/maintenance\-images/(.*) \"$MAINTENANCE_HTML_DIR/maintenance-images/\$1\"
  AliasMatch .*/maintenance\-styles/(.*) \"$MAINTENANCE_HTML_DIR/maintenance-styles/\$1\"
  AliasMatch .*                          \"$maintenance_html_name.var\""

switch_mode() {
  # If mode name is not set in cmd line or if status is needed, do nothing
  if [ "$operation" != 'status' ] ; then
    # Trying to find a line after which new mode code should be written
    local begin_line=`cat "$balancer_conf" | grep -n "$BEGIN_MARK"`
    begin_line=${begin_line%:*}

    # Trying to find the last line of previous mode code section
    local end_line=`cat "$balancer_conf" | grep -n "$END_MARK"`
    end_line=${end_line%:*}

    # Fetching total lines number of the config
    local eof_line=`wc -l "$balancer_conf"`
    eof_line=${eof_line% *}

    # Validating results
    if [ -z "$begin_line" ] || [ -z "$end_line" ] || [ -z "$eof_line" ] || [ $end_line -lt $begin_line ] || [ $eof_line -lt $end_line ]; then
      log "$0: Conf file '$balancer_conf' has an unexpected format: it should contain begin mark '$BEGIN_MARK' and end mark '$END_MARK' after it"
      exit -1
    fi

    # Creating new conf file as apache.tmp$$.conf
    local config_file_dir=`dirname "$balancer_conf"`
    local config_file_tmp="$config_file_dir/apache.tmp$$.conf"
    let "tail_line = eof_line - end_line + 1"
    cat "$balancer_conf" | head -n $begin_line >"$config_file_tmp"
    if [ "$mode" == 'balancer' ]; then
      echo "$BALANCER_CODE" >>"$config_file_tmp"
    else
      echo "$MAINTENANCE_CODE" >>"$config_file_tmp"
    fi
    cat "$balancer_conf" | tail -n $tail_line >>"$config_file_tmp"

    # Replacing the old conf with the new conf
    mv "$config_file_tmp" "$balancer_conf"
  fi
}

do_operation() {
  # All commands besides 'status' are redirecting directly to httpd
  if [ "$operation" == 'status' ] ; then
    # Apache 'maintenance' mode should be always in status 'started' when apache is running.
    # But apache 'balancer' mode should be in status 'started' only when it is true
    if [ "$mode" == 'balancer' ] ; then
      local in_right_mode=`cat "$balancer_conf" | grep "$BALANCER_MODE"`
      if [ -z "$in_right_mode" ] ; then
        exit 1
      fi
    fi

    # Checking up that apache is running
    local PID=`cat "$pid_file" 2>/dev/null`
    /usr/bin/test -n "$PID" && /bin/kill -s 0 "$PID"
  else
    $HTTPD_BIN -C "Include ${CENTOS_RELEASE}.conf" -e error -d $server_root -k "$operation" &
  fi
}

log_before $log_folder $@
get_centos_release
get_httpd_bin

switch_mode
do_operation
result=$?

log_after $result

exit $result
