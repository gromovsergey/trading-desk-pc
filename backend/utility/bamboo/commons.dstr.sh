#!/bin/bash

dstr_download_colocation_xml() {
  local COLO_NAME=$1
  local VERSION=$2

  [ -z "$VERSION" ] && { echo ": $FUNCNAME: Undefined VERSION" ; exit 1 ; }
  [ -z "$COLO_NAME" ] && { echo ": $FUNCNAME: Undefined COLO_NAME" ; exit 1 ; }

  download_plugin_colocation_xml "$COLO_NAME" "FOROS-DSTR" "$VERSION" "trunk"
  return $?
}

dstr_create_colocation_xml() {
  local HOST=$1
  local VERSION=$2
  local BRANCH=$3
  local HDFS_USERNAME=$4
  shift; shift; shift; shift;
  local PARAMS="$@"

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$VERSION" ] && { echo ": $FUNCNAME: Undefined VERSION" ; exit 1 ; }
  [ -z "$BRANCH" ] && { echo ": $FUNCNAME: Undefined BRANCH" ; exit 1 ; }

  PARAMS="$PARAMS
    CMS_HOST=$HOST
    CMS_USER=$HDFS_USERNAME
    CMS_CURRENT_VERSION=$VERSION
  "

  create_plugin_colocation_xml "$HOST" "FOROS-DSTR" "$VERSION" "$BRANCH" "$PARAMS"
  local result=$?

  return $result
}

dstr_create_config_rpms() {
  local VERSION=$1
  local BRANCH=$2
  local COLO_XML_FILE=$3

  [ -z "$BRANCH" ] && { echo ": $FUNCNAME: Undefined BRANCH" ; exit 1 ; }
  [ -z "$VERSION" ] && { echo ": $FUNCNAME: Undefined VERSION" ; exit 1 ; }
  [ -z "$COLO_XML_FILE" ] && { echo ": $FUNCNAME: Undefined COLO_XML_FILE" ; exit 1 ; }

  create_config_rpms "FOROS-DSTR" "$VERSION" "$BRANCH" "$COLO_XML_FILE" || \
    { echo ": $FUNCNAME: Could not create config rpms for FOROS-DSTR" ; exit 1 ; }


  rm -rf $WORKING_DIR/dstr_rpms && \
    mv $WORKING_DIR/rpms $WORKING_DIR/dstr_rpms || \
    { echo ": $FUNCNAME: Could not move config rpms for FOROS-DSTR" ; exit 1 ; }
}

dstr_install_hadoop_config_rpms() {
  local HOST=$1

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }

  [ -d $WORKING_DIR/dstr_rpms ] || { echo ": $FUNCNAME: Directory dstr_rpms not found"; exit 1; }

  local rpms=`find $WORKING_DIR/dstr_rpms -type f -name "*-mgr*.rpm" | sort`
  rpms="$rpms `find -L "$WORKING_DIR/dstr_rpms" -name '*.rpm' ! -name '*-mgr*' ! -name '*-zenoss*' ! -name '*hdfs-load*'`"

  [ -z "$rpms" ] && { echo ": $FUNCNAME: Packages not found in directory"; exit 1; }

  install_packages "$HOST" $rpms

  return 0
}

dstr_install_stat_config_rpms() {
  local HOST=$1

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }

  [ -d $WORKING_DIR/dstr_rpms ] || { echo ": $FUNCNAME: Directory dstr_rpms not found"; exit 1; }

  local rpms=`find $WORKING_DIR/dstr_rpms -type f -name "*-mgr*.rpm" -or -name "*-hdfs-load*" | sort`

  [ -z "$rpms" ] && { echo ": $FUNCNAME: Packages not found in directory"; exit 1; }

  install_packages "$HOST" $rpms

  return 0
}

dstr_start() {
  local colocation="$1"
  local host="$2"

  [ -z "$colocation" ] && { echo ": $FUNCNAME: undefined colocation "; exit 1; }
  [ -z "$host" ] && host="hadoop1"

  ssh -o BatchMode=yes datastore@$host -- /opt/foros/manager/bin/cmgr -f dstr-$colocation start
  return $?
}

dstr_stop() {
  local colocation="$1"
  local host="$2"

  [ -z "$colocation" ] && { echo ": $FUNCNAME: undefined colocation "; exit 1; }
  [ -z "$host" ] && host="hadoop1"

  if ssh -o BatchMode=yes $host -- rpm -q foros-config-datastore-$colocation-local-mgr; then
    ssh -o BatchMode=yes datastore@$host -- /opt/foros/manager/bin/cmgr -f dstr-$colocation stop
    return $?
  fi
  return 0
}

dstradm_start() {
  local host="$1"
  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host "; exit 1; }

  ssh -o BatchMode=yes datastore@$host -- /opt/foros/manager/bin/cmgr -f foros-datastore start
  return $?
}

dstradm_stop() {
  local host="$1"
  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host "; exit 1; }

  ssh -o BatchMode=yes datastore@$host -- /opt/foros/manager/bin/cmgr -f foros-datastore stop
  return 0
}

dstradm_get_manager_host() {
  local cluster="$1"
  [ -z "$cluster" ] && { echo ": $FUNCNAME: undefined cluster >&2"; exit 1; }

  if [ "$cluster" = "moscow-hadoop-central" ]; then
    echo "hadoop1"
  elif [ "$cluster" = "dev-hadoop-central" ]; then
    echo "gp-test0"
  else
    echo ": $FUNCNAME: unknown cluster $cluster >&2"
    exit 1
  fi
}

dstradm_get_nodes() {
  local cluster="$1"
  [ -z "$cluster" ] && { echo ": $FUNCNAME: undefined cluster >&2"; exit 1; }

  if [ "$cluster" = "moscow-hadoop-central" ]; then
    echo "hadoop1 hadoop2"
  elif [ "$cluster" = "dev-hadoop-central" ]; then
    echo "gp-test0 gp-test1"
  else
    echo ": $FUNCNAME: unknown cluster $cluster >&2"
    exit 1
  fi
}

dstr_custom_build() {
  local svnpath="$1"
  local target="$2"

  [ -z "$svnpath" ] && { echo ": $FUNCNAME: undefined svnpath"; exit 1; }
  [ -z "$target" ] && target="el6"
  local centos_release=""

  if [ "${target%%[0-9]}" = "el" ]; then
    abt_setup_custom_build_mock_config "foros/datastore" $target
    centos_release="$target"
  else
    abt_setup_custom_build_mock_config_with_host "foros/datastore" $target
    centos_release="$CENTOS_RELEASE"
  fi
  abt_custom_build_with_cache "foros/datastore" "foros-datastore-hdfs-load" \
    -A "foros/datastore foros/datastore/$svnpath" \
    -m "$ABT_MOCK_CONFIG"
  abt_save_custom_build_version "foros/datastore" "$ABT_VERSION"
}

dstradm_custom_build() {
  local svnpath="$1"
  local version="$2"
  local target="$3"

  [ -z "$svnpath" ] && { echo ": $FUNCNAME: undefined svnpath"; exit 1; }
  [ -z "$version" ] && { echo ": $FUNCNAME: undefined version "; exit 1; }
  [ -z "$target" ] && target="el6"
  local centos_release=""

  if [ "${target%%[0-9]}" = "el" ]; then
    abt_setup_custom_build_mock_config "foros/datastore-adm" $target
    centos_release="$target"
  else
    abt_setup_custom_build_mock_config_with_host "foros/datastore-adm" $target
    centos_release="$CENTOS_RELEASE"
  fi
  abt_custom_build "foros/datastore-adm" "$version" \
    -A "foros/datastore-adm foros/datastore-adm/$svnpath" \
    -m "$ABT_MOCK_CONFIG"
  abt_save_custom_build_version "foros/datastore-adm" "$version"
}

dstradm_update() {
  local cluster="$1"
  local version="$2"

  [ -z "$cluster" ] && { echo ": $FUNCNAME: undefined cluster "; exit 1; }
  [ -z "$version" ] && { echo ": $FUNCNAME: undefined version "; exit 1; }

  local manager=`dstradm_get_manager_host $CLUSTER`
  local nodes=`dstradm_get_nodes $CLUSTER`
  [ -z "$manager" -o -z "$nodes" ] && exit 1

  local node
  for node in $nodes; do
    echo ": $FUNCNAME: installing foros-datastore-adm on node $node"
    doc uninstall_packages "$node" "foros-datastore-adm-config-$cluster-local-mgr" \
                                   "foros-datastore-adm-config-$cluster-$node"
    doc install_packages_from_repo "$node" "foros-datastore-adm" "$version"
    doc install_packages_from_repo "$node" "foros-datastore-adm-config-$cluster-local-mgr" "$version"
    if [ "$node" = "$manager" ]; then
      doc install_packages_from_repo "$node" "foros-datastore-adm-config-$cluster-mgr" "$version"
      doc install_packages_from_repo "$node" "foros-datastore-adm-oozie-server-web" "$version"
    fi
    doc install_packages_from_repo "$node" "foros-datastore-adm-config-$cluster-$node" "$version"
    echo; echo;
  done

}

dstr_get_package_list() {
  local version=""
  local release=""
  local flag=""
  local colo=""
  version=`product_get_package_list_version $@`
  [ "$?" != "0" ] && { echo ": $FUNCNAME: could not parse version" >&2; exit 1; }
  release=`product_get_package_list_release $@`
  [ "$?" != "0" ] && { echo ": $FUNCNAME: could not parse release" >&2; exit 1; }
  colo=`product_get_package_list_colo $@`
  [ "$?" != "0" ] && { echo ": $FUNCNAME: could not parse colo" >&2; exit 1; }
  flag=`product_get_package_list_flag $@`
  [ "$?" != "0" ] && { echo ": $FUNCNAME: could not parse flags" >&2; exit 1; }

  echo ": $FUNCNAME: colo=$colo, version=$version, release=$release, flag=$flag" >&2
  local binary="foros-datastore-hdfs-load"
  [ ! -z "$version" ] && binary="${binary}-${version}"
  echo $flag | grep -vq S && binary=""
  echo $flag | grep -q C && binary=""

  local config="foros-config-datastore-${colo}-mgr foros-config-datastore-${colo}-local-mgr"
  echo "$flag" | grep -q S && config="$config foros-config-datastore-${colo}-hdfs-load"
  echo "$flag" | grep -vq S && config="$config foros-config-datastore-${colo}"
  local config_packages=""
  local package=""
  if [ ! -z "$version" ]; then
    for package in $config; do
      package="${package}-${version}"
      [ ! -z "$release" ] && package="${package}-${release}"
      config_packages="$config_packages $package"
    done
  else
    config_packages="$config"
  fi
  [ -z "$colo" ] && config_packages=""
  for package in $binary $config_packages; do
    if echo $flag | grep -q i; then
      echo "${package}*.rpm"
    else
      echo "${package}"
    fi
  done
}
