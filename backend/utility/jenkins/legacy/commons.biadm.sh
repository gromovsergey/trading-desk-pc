#!/bin/bash

biadm_custom_build() {
  local svnpath="$1"
  local target="$2"

  [ -z "$svnpath" ] && { echo ": $FUNCNAME: undefined svnpath"; exit 1; }
  [ -z "$target" ] && target="el6"
  local centos_release=""

  if [ "${target%%[0-9]}" = "el" ]; then
    abt_setup_custom_build_mock_config "oix/biadm" $target
    centos_release="$target"
  else
    abt_setup_custom_build_mock_config_with_host "oix/biadm" $target
    centos_release="$CENTOS_RELEASE"
  fi
  abt_custom_build_with_cache "oix/biadm" "oix-biadm" \
    -A "oix/biadm oix/biadm/$svnpath" \
    -F "fake/centos:$centos_release" \
    -m "$ABT_MOCK_CONFIG"
  abt_save_custom_build_version "oix/biadm" "$ABT_VERSION"
}

biadm_stop() {
  local host="$1"
  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host"; exit 1; }

  if ssh -o BatchMode=yes $host -- rpm -q foros-biadm; then
    doc ssh -o BatchMode=yes bi@$host -- "/opt/foros/manager/bin/cmgr -f foros-bi stop"
  fi
}

biadm_start() {
  local host="$1"
  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host"; exit 1; }

  doc ssh -o BatchMode=yes bi@$host -- "/opt/foros/manager/bin/cmgr -f foros-bi start"
}

biadm_get_colo() {
  local host="$1"
  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host"; exit 1; }
  case $host in
    pentaho-test)
      echo "moscow-test-central"
      ;;
    pentaho-stage)
      echo "moscow-stage-central"
      ;;
    ru77probi00)
      echo "production-ru-central"
      ;;
    *)
      echo "moscow-dev-ui"
      ;;
  esac
}

biadm_update() {
  local host="$1"
  local version="$2"

  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host"; exit 1; }
  [ -z "$version" ] && { echo ": $FUNCNAME: undefined version "; exit 1; }

  local colo=`biadm_get_colo $host`

  doc uninstall_packages "$host" "foros-config-biadm"
  doc install_packages "$host" "foros-biadm-${version}*.rpm" "foros-config-biadm-${colo}-${version}*.rpm"

  if [ "$colo" = "moscow-dev-ui" ]; then
    doc ssh -o BatchMode=yes bi@$host -- \
      "rm -f /opt/foros/bi/etc/subagent/conf.d/$host-conf.xml"
    doc ssh -o BatchMode=yes bi@$host -- \
      "ln -s /opt/foros/bi/etc/subagent/conf.d/localhost-conf.xml /opt/foros/bi/etc/subagent/conf.d/$host-conf.xml"
  fi
}

biadm_get_package_list() {
  product_get_package_list "biadm" $@ | grep -v -- '-mgr'
}
