#!/bin/bash

. $WORKING_DIR/commons.util.sh

### Copy files from Master (NB Master, Test) to Auto (NB Auto, NB AT Test)
sync_oui_files() {
  local FROM_HOST=$1
  local TO_HOST=$2
  local KEY_NAME=$3
  local USER_NAME=$4

  local SYNC_DIR
  for SYNC_DIR in www sync ; do
    ssh ${USER_NAME}@${FROM_HOST} "/usr/bin/rsync -az --delete-after -e \"ssh -i /home/${USER_NAME}/.ssh/${KEY_NAME}\" /opt/foros/ui/var/${SYNC_DIR} \
        ${USER_NAME}@${TO_HOST}:/opt/foros/ui/var/" && echo "Files ${TO_HOST}:/opt/foros/ui/var/${SYNC_DIR}/ have been synchronized from ${FROM_HOST}" \
            || { echo "ERROR: Can't synchronize ${TO_HOST}:/opt/foros/ui/var/${SYNC_DIR}/ from ${FROM_HOST}!" && return 1; }
  done
  return 0
}

start_cluster() {
  local HOST=$1
  local PATTERN=$2
  local SUBGROUP=$3
  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PATTERN" ] && PATTERN="ui"
  [ -z "$SUBGROUP" ] && SUBGROUP="all"

  execute_remote $HOST $PATTERN $SUBGROUP <<-"EOF"
    WORKING_DIR=`pwd`
    . $WORKING_DIR/commons.sh

    PATTERN=$1
    SUBGROUP=$2

    cnt=`rpm -qa | grep foros-config-$PATTERN | wc -l`
    if [ "$cnt" -gt "2" ] ; then
      echo ": start_cluster: Starting a cluster on $HOST"
      doc sudo -u uiuser /opt/foros/manager/bin/cmgr start -f $PATTERN $SUBGROUP
      echo ": start_cluster: Cluster started successfully"
    fi
EOF
  [ "$?" != "0" ] && exit 1
  return 0
}

stop_cluster() {
  local HOST=$1
  local PATTERN=$2

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PATTERN" ] && PATTERN="ui"

  # FOROS UI and Invitation can't be started on the same host
  # as they shared the same Glassfish ports (domain1)
  execute_remote $HOST $HOST $PATTERN <<-"EOF"
    WORKING_DIR=`pwd`
    . $WORKING_DIR/commons.sh

    HOST=$1
    PATTERN=$2

    echo ": stop_cluster/$0: Stopping a cluster"
    echo ": stop_cluster/$0: Currently installed packages:"
    rpm -qa | grep foros | sort

    cnt=`rpm -qa | grep foros-config-invitation | wc -l`
    if [ "$cnt" -gt "2" ] ; then
      echo "Stop Invitation"
      doc sudo -u invitations /opt/foros/manager/bin/cmgr stop -f invitation-moscow :Glassfish
      doc sudo -u invitations /opt/foros/manager/bin/cmgr stop -f invitation-moscow
      echo ": stop_cluster/$0: Invitation stopped successfully"
    fi

    echo "Stop foros $PATTERN"
    cnt=`rpm -qa | grep foros-config-$PATTERN | wc -l`
    if [ "$cnt" -gt "2" ] ; then
      doc sudo -u uiuser /opt/foros/manager/bin/cmgr stop -f $PATTERN
      echo ": stop_cluster/$0: foros $PATTERN stopped successfully"
    fi
EOF
  [ "$?" != "0" ] && exit 1
  return 0
}

start_product() {
  local product="$1" # "ui" or "pgdb" (FOROS Manager file)
  local colo="$2"
  local manager_host="$3"   # like "voix0 voix1 voix2", first host is the FOROS Manager host

  if [ "$product" = "server" ] ; then
    ssh -o BatchMode=yes aduser@$manager_host "/opt/foros/manager/bin/cmgr -f $product-$colo start dbaccess"
    return $?
  elif [ "$product" = "bi" ] ; then
    ssh -o BatchMode=yes uiuser@$manager_host "/opt/foros/manager/bin/cmgr -f pgdb-$colo bi start"
    return $?
  elif [ "$product" = "mondrian" ] ; then
    ssh -o BatchMode=yes bi@$manager_host "/opt/foros/manager/bin/cmgr -f foros-bi InstallAnalisysDatasources connection=$COLO"
    return $?
  elif [ "$product" = "biadm" ] ; then
    ssh -o BatchMode=yes bi@$manager_host "/opt/foros/manager/bin/cmgr -f foros-bi start"
    return $?
  elif [ "$product" = "dstr" ] ; then
    if [ "$DSTR_BRANCH" = "3.3.0" ]; then
      ssh -o BatchMode=yes datastore@$manager_host "/opt/foros/manager/bin/cmgr -f foros-datastore update colocation=$colo"
      return $?
    else
      ssh -o BatchMode=yes datastore@$manager_host "/opt/foros/manager/bin/cmgr -f dstr-$colo start"
      return $?
    fi
  else
    ssh -o BatchMode=yes uiuser@$manager_host "/opt/foros/manager/bin/cmgr -f $product-$colo start"
    return $?
  fi

  return 0
}

stop_product() {
  local product="$1"      # "ui" or "pgdb" or "server" (FOROS Manager file)
  local colo="$2"
  local manager_host="$3" # like "voix0 voix1 voix2", first host is the FOROS Manager host

  if [ "$product" = "server" ] ; then
    doc ssh aduser@$manager_host "/opt/foros/manager/bin/cmgr -f $product-$colo stop dbaccess"
  elif [ "$product" = "biadm" ] ; then
    doc ssh bi@$manager_host "/opt/foros/manager/bin/cmgr -f foros-bi stop"
  elif [ "$product" = "dstr" ] ; then
    if [ "$DSTR_BRANCH" = "3.3.0" ]; then
      doc ssh -o 'BatchMode=yes' datastore@$manager_host "/opt/foros/manager/bin/cmgr -f foros-datastore oozie-bundles stop colocation=$colo"
    else
      doc ssh -o 'BatchMode=yes' datastore@$manager_host "/opt/foros/manager/bin/cmgr -f dstr-$colo stop"
    fi
  else
    doc ssh uiuser@$manager_host "/opt/foros/manager/bin/cmgr -f $product-$colo stop"
    [ "$product" = "ui" ] && { doc ssh uiuser@$manager_host "/opt/foros/manager/bin/cmgr -f $product-$colo start :MaintenancePage*" ; }
  fi

  return 0
}

