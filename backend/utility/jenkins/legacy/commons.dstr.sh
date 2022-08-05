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
  "

  create_plugin_colocation_xml "$HOST" "FOROS-DSTR" "$VERSION" "$BRANCH" "$PARAMS"
  local result=$?

  return $result
}

dstradm_create_colocation_xml() {
  local COLO=$1
  local VERSION=$2
  local BRANCH=$3

  [ -z "$COLO" ] && { echo ": $FUNCNAME: Undefined COLO" ; exit 1 ; }
  [ -z "$VERSION" ] && { echo ": $FUNCNAME: Undefined VERSION" ; exit 1 ; }
  [ -z "$BRANCH" ] && { echo ": $FUNCNAME: Undefined BRANCH" ; exit 1 ; }

  create_plugin_colocation_xml "$COLO" "FOROS-DSTRADM" "$VERSION" "$BRANCH"
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

dstradm_create_config_rpms() {
  local VERSION=$1
  local BRANCH=$2
  local COLO_XML_FILE=$3

  [ -z "$BRANCH" ] && { echo ": $FUNCNAME: Undefined BRANCH" ; exit 1 ; }
  [ -z "$VERSION" ] && { echo ": $FUNCNAME: Undefined VERSION" ; exit 1 ; }
  [ -z "$COLO_XML_FILE" ] && { echo ": $FUNCNAME: Undefined COLO_XML_FILE" ; exit 1 ; }

  create_config_rpms "FOROS-DSTRADM" "$VERSION" "$BRANCH" "$COLO_XML_FILE" || \
    { echo ": $FUNCNAME: Could not create config rpms for FOROS-DSTRADM" ; exit 1 ; }

  rm -rf $WORKING_DIR/dstradm_rpms && \
    mv $WORKING_DIR/rpms $WORKING_DIR/dstradm_rpms || \
    { echo ": $FUNCNAME: Could not move config rpms for FOROS-DSTRADM" ; exit 1 ; }
}

dstradm_install_config_rpms() {
  local CLUSTER=$1

  [ -z "$CLUSTER" ] && { echo ": $FUNCNAME: Undefined CLUSTER" ; exit 1 ; }

  [ -d $WORKING_DIR/dstradm_rpms ] || { echo ": $FUNCNAME: Directory dstradm_rpms not found"; exit 1; }

  local node
  for node in `dstradm_get_nodes $CLUSTER`; do
    echo ": $FUNCNAME: installing foros-config-datastore-adm on node $node"
    local rpms=`find $WORKING_DIR/dstradm_rpms -type f -name "*-mgr*.rpm" -or -name "*$node*.rpm"`
    [ -z "$rpms" ] && { echo ": $FUNCNAME: Packages not found in directory"; exit 1; }
    install_packages "$node" $rpms
    echo; echo
  done

  return 0
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

dstradm_start() {
  local host="$1"
  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host "; exit 1; }

  ssh -o BatchMode=yes datastore@$host -- /opt/foros/manager/bin/cmgr -f datastore-adm start
  return $?
}

dstradm_stop() {
  local host="$1"
  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host "; exit 1; }

  ssh -o BatchMode=yes datastore@$host -- /opt/foros/manager/bin/cmgr -f datastore-adm stop
  return 0
}

dstradm_get_manager_host() {
  local cluster="$1"
  [ -z "$cluster" ] && { echo ": $FUNCNAME: undefined cluster >&2"; exit 1; }

  if [ "$cluster" = "moscow-hadoop-central" ]; then
    echo "hadoop0"
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
    echo "hadoop0 hadoop1 hadoop2"
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
    abt_setup_custom_build_mock_config "oix/datastore" $target
    centos_release="$target"
  else
    abt_setup_custom_build_mock_config_with_host "oix/datastore" $target
    centos_release="$CENTOS_RELEASE"
  fi
  abt_custom_build_with_cache "oix/datastore" "foros-datastore-hdfs-load" \
    -A "oix/datastore oix/datastore/$svnpath" \
    -m "$ABT_MOCK_CONFIG"
  abt_save_custom_build_version "oix/datastore" "$ABT_VERSION"
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
    abt_setup_custom_build_mock_config "oix/datastore-adm" $target
    centos_release="$target"
  else
    abt_setup_custom_build_mock_config_with_host "oix/datastore-adm" $target
    centos_release="$CENTOS_RELEASE"
  fi
  abt_custom_build "oix/datastore-adm" "$version" \
    -A "oix/datastore-adm oix/datastore-adm/$svnpath" \
    -m "$ABT_MOCK_CONFIG"
  abt_save_custom_build_version "oix/datastore-adm" "$version"
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
                                   "foros-datastore-adm-config-$cluster-$node" \
                                   "foros-datastore-adm-oozie-server-web" \
                                   "foros-datastore-adm"
    doc install_packages "$node" "foros-datastore-adm-${version}*.rpm"
    if [ "$node" = "$manager" ]; then
      doc install_packages "$node" "foros-datastore-adm-oozie-server-web-${version}*.rpm"
    fi
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
  echo $flag | grep -q z && binary=""

  local config="foros-config-datastore-${colo}-mgr foros-config-datastore-${colo}-local-mgr"
  echo "$flag" | grep -q S && config="$config foros-config-datastore-${colo}-hdfs-load"
  echo "$flag" | grep -vq S && config="$config foros-config-datastore-${colo}"
  echo "$flag" | grep -q z && config="foros-config-datastore-${colo}-zenoss"
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

dstr_perf_test() {
  local cluster="$1"
  local branch="$2"
  local stage="$3"

  local hostname=`dstradm_get_manager_host $cluster`

  execute_remote $hostname $cluster $branch $stage <<-"EOF"
    #!/bin/bash
    WORKING_DIR=`pwd`
    . $WORKING_DIR/commons.sh

    CLUSTER=$1
    BRANCH=$2
    STAGE=$3

    SVNPATH="svn+ssh://svn.ocslab.com/home/svnroot/oix/datastore-adm/$BRANCH/utility"
    svn_export_folder $SVNPATH
    sudo chown -R datastore:datastore $CHECKOUT_FOLDER
    echo $CHECKOUT_FOLDER
    [ -f $CHECKOUT_FOLDER/perf-test/stages/$STAGE.sh ] || exit 1
    sudo -Hiu datastore -i -- env PATH=$PATH:$CHECKOUT_FOLDER/impala/tools/bin PYTHONPATH=$CHECKOUT_FOLDER/impala/tools \\
      $CHECKOUT_FOLDER/perf-test/perf-test.sh \\
      $CHECKOUT_FOLDER/perf-test/conf/$CLUSTER.yaml \\
      $CHECKOUT_FOLDER/perf-test/stages/$STAGE.sh
    RESULT=$?
    sudo chown -R maint:maint $CHECKOUT_FOLDER
    exit $RESULT
EOF
  local result=$?
  return $result
}
