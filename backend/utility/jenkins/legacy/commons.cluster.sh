#!/bin/bash

. $WORKING_DIR/commons.util.sh

### Copy files from Master (NB Master, Test) to Auto (NB Auto, NB AT Test)
sync_oui_files() {
  local FROM_HOST=$1
  local TO_HOST=$2
  local USER_NAME=$3

  if ! ssh ${USER_NAME}@${FROM_HOST} 'test -f $HOME/.ssh/sync_oui_files.key' ; then
    echo "Creating SSH key"
    ssh ${USER_NAME}@${FROM_HOST} 'ssh-keygen -f $HOME/.ssh/sync_oui_files.key -N "" -q'
    [ "$?" != "0" ] && { echo "Can't create a key file" ; return 1 ; }
    
    ssh ${USER_NAME}@${FROM_HOST} 'cat $HOME/.ssh/sync_oui_files.key.pub' | ssh ${USER_NAME}@${TO_HOST} 'cat >> $HOME/.ssh/authorized_keys'
    [ "$?" != "0" ] && { echo "Can't autorize $FROM_HOST on $TO_HOST" ; return 1 ; }
  fi
  
  local SYNC_DIR
  for SYNC_DIR in www sync ; do
    ssh ${USER_NAME}@${FROM_HOST} "/usr/bin/rsync -az --delete-after \
      -e \"ssh -i /home/${USER_NAME}/.ssh/sync_oui_files.key\" \
      /opt/foros/ui/var/${SYNC_DIR} ${USER_NAME}@${TO_HOST}:/opt/foros/ui/var/"

    if [ "$?" = "0" ] ; then
      echo "Files ${TO_HOST}:/opt/foros/ui/var/${SYNC_DIR}/ have been synchronized from ${FROM_HOST}"
    else
      echo "ERROR: Can't execute rsync command"
      return 1
    fi
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
      echo ": start_cluster: Starting a cluster on `hostname`"
      doc sudo -Hiu uiuser /opt/foros/manager/bin/cmgr start -f $PATTERN $SUBGROUP
      echo ": start_cluster: Cluster started successfully"
    else
      echo ": start_cluster: packages foros-config-$PATTERN not found, skipped"
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

  execute_remote $HOST $HOST $PATTERN <<-"EOF"
    WORKING_DIR=`pwd`
    . $WORKING_DIR/commons.sh

    HOST=$1
    PATTERN=$2

    echo ": stop_cluster: Installed packages:"
    rpm -qa | grep foros | sort

    echo "Stopping foros $PATTERN"
    cnt=`rpm -qa | grep foros-config-$PATTERN | wc -l`
    if [ "$cnt" -gt "2" ] ; then
      doc sudo -Hiu uiuser /opt/foros/manager/bin/cmgr stop -f $PATTERN
      echo ": stop_cluster: foros $PATTERN stopped successfully"
    else
      echo ": stop_cluster: packages foros-config-$PATTERN not found, skipped"
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
    doc ssh -o BatchMode=yes aduser@$manager_host "/opt/foros/manager/bin/cmgr -f $product-$colo start dbaccess"
  elif [ "$product" = "bi" ] ; then
    doc ssh -o BatchMode=yes uiuser@$manager_host "/opt/foros/manager/bin/cmgr -f pgdb-$colo bi start"
  elif [ "$product" = "mondrian" ] ; then
    doc ssh -o BatchMode=yes bi@$manager_host "/opt/foros/manager/bin/cmgr -f foros-bi InstallAnalisysDatasources connection=$COLO"
  elif [ "$product" = "biadm" ] ; then
    doc ssh -o BatchMode=yes bi@$manager_host "/opt/foros/manager/bin/cmgr -f foros-bi start"
  elif [ "$product" = "dstr" ] ; then
    doc ssh -o BatchMode=yes datastore@$manager_host "/opt/foros/manager/bin/cmgr -f dstr-$colo start"
  else
    doc ssh -o BatchMode=yes uiuser@$manager_host "/opt/foros/manager/bin/cmgr -f $product-$colo start"
  fi

  return 0
}

stop_product() {
  local product="$1"      # "ui" or "pgdb" or "server" (FOROS Manager file)
  local colo="$2"
  local manager_host="$3" # like "voix0 voix1 voix2", first host is the FOROS Manager host
  
  # check is product installed
  local package_name=$product-$colo
  [ "$product" = "dstr" ] && package_name=datastore-$colo
  local cnt=`ssh $manager_host "rpm -qa | grep $package_name | wc -l"`
  [ "$cnt" = "0" ] && { echo "Packages $package_name on $manager_host not found, skipped" ; return 0 ; }

  if [ "$product" = "server" ] ; then
    doc ssh aduser@$manager_host "/opt/foros/manager/bin/cmgr -f $product-$colo stop dbaccess"
  elif [ "$product" = "biadm" ] ; then
    doc ssh bi@$manager_host "/opt/foros/manager/bin/cmgr -f foros-bi stop"
  elif [ "$product" = "dstr" ] ; then
    doc ssh -o 'BatchMode=yes' datastore@$manager_host "/opt/foros/manager/bin/cmgr -f dstr-$colo stop"
  else
    doc ssh uiuser@$manager_host "/opt/foros/manager/bin/cmgr -f $product-$colo stop"
    [ "$product" = "ui" ] && { doc ssh uiuser@$manager_host "/opt/foros/manager/bin/cmgr -f $product-$colo start :MaintenancePage*" ; }
  fi

  return 0
}

