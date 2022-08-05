#!/bin/bash

adclient_download_colocation_xml() {
  local COLO_NAME=$1
  local VERSION=$2

  [ -z "$VERSION" ] && { echo ": $FUNCNAME: Undefined VERSION" ; exit 1 ; }
  [ -z "$COLO_NAME" ] && { echo ": $FUNCNAME: Undefined COLO_NAME" ; exit 1 ; }

  download_plugin_colocation_xml "$COLO_NAME" "AdClient" "$VERSION" "trunk"
  return $?
}

adclient_create_colocation_xml() {
  local HOST=$1
  local VERSION=$2
  local BRANCH=$3

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$VERSION" ] && { echo ": $FUNCNAME: Undefined VERSION" ; exit 1 ; }
  [ -z "$BRANCH" ] && { echo ": $FUNCNAME: Undefined BRANCH" ; exit 1 ; }

  create_plugin_colocation_xml "$HOST" "AdClient" "$VERSION" "$BRANCH"
  return $?
}

adclient_create_config_rpms() {
  local VERSION=$1
  local BRANCH=$2
  local COLO_XML_FILE=$3

  [ -z "$BRANCH" ] && { echo ": $FUNCNAME: Undefined BRANCH" ; exit 1 ; }
  [ -z "$VERSION" ] && { echo ": $FUNCNAME: Undefined VERSION" ; exit 1 ; }
  [ -z "$COLO_XML_FILE" ] && { echo ": $FUNCNAME: Undefined COLO_XML_FILE" ; exit 1 ; }

  create_config_rpms "AdClient" "$VERSION" "$BRANCH" "$COLO_XML_FILE" || \
    { echo ": $FUNCNAME: Could not create config rpms for AdClient" ; exit 1 ; }


  rm -rf $WORKING_DIR/adclient_rpms && \
    mv $WORKING_DIR/rpms $WORKING_DIR/adclient_rpms || \
    { echo ": $FUNCNAME: Could not move config rpms for AdClient" ; exit 1 ; }
}

adclient_create_config_rpms_with_cache() {
  local VERSION=$1
  local BRANCH=$2
  local COLO_XML_FILE=$3

  [ -z "$BRANCH" ] && { echo ": $FUNCNAME: Undefined BRANCH" ; exit 1 ; }
  [ -z "$VERSION" ] && { echo ": $FUNCNAME: Undefined VERSION" ; exit 1 ; }
  [ -z "$COLO_XML_FILE" ] && { echo ": $FUNCNAME: Undefined COLO_XML_FILE" ; exit 1 ; }

  # TODO: revert it after ADCLI-2302
  #local cached_version=`cache_get_package_version 'foros-creatives-moscow-dev-ui-oix-dev[0-9]' "foros/creatives/$BRANCH/CMS"`
  #[ "$?" != "0" ] && return 1
  local cached_version='5000.5.5.20151110150236'

  if [ -n "$cached_version" ] && \
    repo_collect_packages $WORKING_DIR/adclient_rpms -r local "foros-creatives-moscow-dev-ui-oix-dev*-$cached_version*.rpm"; then
    echo ": $FUNCNAME: '$cached_version' found, skip build"
    return 0
  fi

  adclient_create_config_rpms $VERSION $BRANCH $COLO_XML_FILE
  return $?
}

adclient_install_config_rpms() {
  local HOST=$1
  local PREFIX=$2

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PREFIX" ] && { echo ": $FUNCNAME: Undefined PREFIX" ; exit 1 ; }

  [ -d $WORKING_DIR/adclient_rpms ] || { echo ": $FUNCNAME: Directory $WORKING_DIR/adclient_rpms not found"; exit 1; }

  local rpms="`find -L "$WORKING_DIR/adclient_rpms" -name "$PREFIX*.rpm"`"

  [ -z "$rpms" ] && { echo ": $FUNCNAME: Packages with prefix $PREFIX not found"; exit 1; }

  echo ": $FUNCNAME: installing '$rpms' to '$HOST'"
  install_packages "$HOST" $rpms
  return $?
}

adclient_remove_old_packages() {
  local branch="$1"; shift
  local hosts="$@"

  [ -z "$branch" ] && { echo ": $FUNCNAME: undefined branch "; exit 1; }
  [ -z "$hosts" ] && { echo ": $FUNCNAME: undefined hosts"; exit 1; }

  if [ "$branch" != "none" ]; then
    for host in $hosts; do
      uninstall_packages $host `adclient_get_package_list -c 'moscow-dev-ui*'`
    done
  fi

}

adclient_get_package_list() {
  local version=""
  local flag=""
  local colo=""
  version=`product_get_package_list_version $@`
  [ "$?" != "0" ] && { echo ": $FUNCNAME: could not parse version" >&2; exit 1; }
  colo=`product_get_package_list_colo $@`
  [ "$?" != "0" ] && { echo ": $FUNCNAME: could not parse colo" >&2; exit 1; }
  flag=`product_get_package_list_flag $@`
  [ "$?" != "0" ] && { echo ": $FUNCNAME: could not parse flags" >&2; exit 1; }

  echo ": $FUNCNAME: colo=$colo, version=$version, flag=$flag" >&2
  local binary="foros-creatives"
  if [ ! -z "$colo" ]; then
    binary="${binary}-${colo}"
    [ ! -z "$version" ] && binary="${binary}-${version}"
  fi
  if echo $flag | grep -q i; then
    echo "${binary}*.rpm"
  else
    echo "${binary}"
  fi
}
