#!/bin/bash

ZENOSS_NB_HOST=zenoss-nb

ZENOSS_PRODUCTION_STATE=1000
ZENOSS_MAINTENANCE_STATE=300

### Set zenoss device profuction state
### $1 - monitoring host (xnb or zenoss)
### $2 - device, ex. oui-nbmaster0
### $3 - state: 1000 - production, 300 - maintenance
set_zenoss_device_state() {
  local monitoring_host=$1
  local DEVICE=$2
  local STATE=$3

  [ -z $monitoring_host ] && { echo "Undefined monitoring_host"; exit 1; }

  execute_remote_as "zenoss" $monitoring_host $DEVICE $STATE <<-"EOF"
    [ "`whoami`" != "zenoss" ] && { echo "Only zenoss can run this script"; exit 1; }

    DEVICE=$1
    STATE=$2

    [ -z "$DEVICE" ] && { echo "Undefined DEVICE" ; exit 1 ; }
    [ -z "$STATE" ] && { echo "Undefined STATE" ; exit 1 ; }

    export LD_LIBRARY_PATH=/opt/zenoss/lib/
    export ZENHOME=/opt/zenoss
    . /opt/zenoss/bin/zenfunctions

    /opt/zenoss/bin/python -c "`cat  <<-"PY_EOF"
import Globals;
from Products.ZenUtils.ZenScriptBase import ZenScriptBase;
from transaction import commit, manager;
import sys;

dmd = ZenScriptBase(connect=True).dmd;
device=dmd.Devices.findDevice(sys.argv[1]);
for attempt in manager.attempts():
    with attempt:
        device.setProdState(sys.argv[2]);
PY_EOF`" $DEVICE $STATE >./zenstate.log 2>&1

if [ "$?" != "0" ]; then
  cat ./zenstate.log
  echo "Could not change state of device $DEVICE" | \\
    mailx -s "[ZenOSS ERROR]" -a ./zenstate.log \\
    kirill_goldshtein@ocslab.com vitaliy_knyazev@ocslab.com
fi
exit 0
EOF
  [ "$?" != "0" ] && exit 1
  return 0
}

zenforos_erase() {
  local monitoring_host=$1
  local colo_name=$2
  local template=$3

  [ -z $monitoring_host ] && { echo ": $FUNCNAME: Undefined monitoring_host" ; exit 1 ; }
  [ -z $colo_name ] && { echo ": $FUNCNAME: Undefined colo_name" ; exit 1 ; }
  [ -z $template ] && { echo ": $FUNCNAME: Undefined template" ; exit 1 ; }

  execute_remote_ex "maint" "$monitoring_host" "*.sh" $template $colo_name <<-"EOF"
  WORKING_DIR=`pwd`
  . $WORKING_DIR/commons.sh

  TEMPLATE=$1
  COLO_NAME=$2

  [ -z $TEMPLATE ] && { echo "Undefined TEMPLATE" ; exit 1 ; }
  [ -z $COLO_NAME ] && { echo "Undefined COLO_NAME" ; exit 1 ; }

  echo ": update_zenforos_plugin: cleaning template $TEMPLATE from $COLO_NAME"
  docl sudo -iH -u zenoss /opt/zenoss/bin/zenforos -- --erase $COLO_NAME $TEMPLATE

  exit 0
EOF
  [ "$?" != "0" ] && exit 1
  return 0
}

zenforos_install() {
  local monitoring_host=$1
  local colo_name=$2
  local template=$3

  [ -z $monitoring_host ] && { echo ": $FUNCNAME: Undefined monitoring_host" ; exit 1 ; }
  [ -z $colo_name ] && { echo ": $FUNCNAME: Undefined colo_name" ; exit 1 ; }
  [ -z $template ] && { echo ": $FUNCNAME: Undefined template" ; exit 1 ; }

  execute_remote_ex "maint" "$monitoring_host" "*.sh" $template $colo_name <<-"EOF"
  WORKING_DIR=`pwd`
  . $WORKING_DIR/commons.sh

  TEMPLATE=$1
  COLO_NAME=$2

  [ -z $TEMPLATE ] && { echo "Undefined TEMPLATE" ; exit 1 ; }
  [ -z $COLO_NAME ] && { echo "Undefined COLO_NAME" ; exit 1 ; }

  echo ": update_zenforos_plugin: installing template $TEMPLATE to $COLO_NAME"
  docl sudo -iH -u zenoss /opt/zenoss/bin/zenforos $COLO_NAME $TEMPLATE

  exit 0
EOF
  [ "$?" != "0" ] && exit 1
  return 0
}


update_zenforos_plugin() {
  local monitoring_host=$1; shift
  local project=$1; shift

  [ -z $monitoring_host ] && { echo ": $FUNCNAME: Undefined monitoring_host" ; exit 1 ; }
  [ -z $project ] && { echo ": $FUNCNAME: Undefined project" ; exit 1 ; }

  local func="${project}_get_package_list"
  local packages=`eval "$func -z -i $@"`
  [ -z "$packages" ] && { echo ": $FUNCNAME: undefined packages "; exit 1; }
  [ `echo "$packages" | wc -l` -gt 1 ] && { echo ": $FUNCNAME: too many packages"; exit 1; }
  local template="foros${project}"
  local colo_name=`product_get_package_list_colo $@`

  zenforos_erase $monitoring_host $colo_name $template
  install_packages "$monitoring_host" -r local $packages
  zenforos_install $monitoring_host $colo_name $template
  return 0
}

test_snmp_walk() {
  local monitoring_host=$1
  local colo_name=$2
  shift; shift;
  local hosts="$@"

  [ -z $monitoring_host ] && { echo ": $FUNCNAME: Undefined monitoring_host" ; exit 1 ; }
  [ -z $colo_name ] && { echo ": $FUNCNAME: Undefined colo_name" ; exit 1 ; }
  [ -z "$hosts" ] && { echo ": $FUNCNAME: Undefined hosts" ; exit 1 ; }

  execute_remote_ex "zenoss" "$monitoring_host" "*.sh" $colo_name $hosts <<-"EOF"
  [ "`whoami`" != "zenoss" ] && { echo "Only zenoss can run this script"; exit 1; }

  WORKING_DIR=`pwd`
  . $WORKING_DIR/commons.sh

  COLO_NAME=$1; shift
  HOSTS="$@"

  [ -z "$HOSTS" ] && { echo "Undefined HOSTS" ; exit 1 ; }
  [ -z $COLO_NAME ] && { echo "Undefined COLO_NAME" ; exit 1 ; }

  for host in $HOSTS; do
    echo ": test_snmp_walk: testing $host"
    doc snmpwalk -v2c -c public -M+/opt/zenoss/lib/foros/$COLO_NAME -mALL $host foros
  done

  exit 0
EOF
  [ "$?" != "0" ] && exit 1
  return 0
}

test_ui_snmp_walk() {
  local monitoring_host=$1
  local colo_name=$2
  shift; shift;
  local hosts="$@"

  [ -z $monitoring_host ] && { echo ": $FUNCNAME: Undefined monitoring_host" ; exit 1 ; }
  [ -z $colo_name ] && { echo ": $FUNCNAME: Undefined colo_name" ; exit 1 ; }
  [ -z "$hosts" ] && { echo ": $FUNCNAME: Undefined hosts" ; exit 1 ; }
  local result=0

  for host in $hosts; do
    local output=`test_snmp_walk $monitoring_host $colo_name $host`
    echo -e "$output"
    local values=`echo -e "$output" | sed -n -e \
      's|^FOROSUI-WEB-SERVER-APPLICATION-MIB::oui[a-Z]\+BalancerCode.[0-9]\+ = Gauge32: \([0-9]\+\)$|\1|gp'`

    [ "`echo $values | wc -w`" = "0" ] && \
      echo ": $FUNCNAME:  Could not get balancer response code" && \
      ((result+=1))

    for value in $values; do
      [ "$value" != "200" ] && [ "$value" != "302" ] && echo ": $FUNCNAME:  Code: $value" && ((result+=1))
    done
  done
  return $result
}

zenoss_model_colocation() {
  local monitoring_host=$1
  local colo_name=$2

  [ -z $monitoring_host ] && { echo ": $FUNCNAME: Undefined monitoring_host" ; exit 1 ; }
  [ -z $colo_name ] && { echo ": $FUNCNAME: Undefined colo_name" ; exit 1 ; }

  execute_remote_ex "maint" "$monitoring_host" "*.sh" $colo_name <<-"EOF"
  WORKING_DIR=`pwd`
  . $WORKING_DIR/commons.sh

  COLO_NAME=$1

  [ -z $COLO_NAME ] && { echo "Undefined COLO_NAME" ; exit 1 ; }

  echo ": zenforos_model_colocation: modelling $COLO_NAME"
  docl sudo -iH -u zenoss /opt/zenoss/bin/zenmodeler run -p /Systems/$COLO_NAME

  exit 0
EOF
  [ "$?" != "0" ] && exit 1
  return 0
}

