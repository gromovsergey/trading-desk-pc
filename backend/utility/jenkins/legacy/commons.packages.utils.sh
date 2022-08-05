#!/bin/bash

product_get_package_list_version() {
  local OPTIND opt
  while getopts ":c:v:r:izZCS" opt; do
    case "${opt}" in
        v)  echo "${OPTARG}"
            return
            ;;
        *)
            ;;
    esac
  done
  shift $((OPTIND-1))
}

product_get_package_list_release() {
  local OPTIND opt
  while getopts ":c:v:r:izZCS" opt; do
    case "${opt}" in
        r)  echo "${OPTARG}"
            return
            ;;
        *)
            ;;
    esac
  done
  shift $((OPTIND-1))
}

product_get_package_list_colo() {
  local OPTIND opt
  while getopts ":c:v:r:izZCS" opt; do
    case "${opt}" in
        c)  echo "${OPTARG}"
            return
            ;;
        *)
            ;;
    esac
  done
  shift $((OPTIND-1))
}

# -i -- install pattern, add *.rpm to end of packages
# -Z -- just zenpacks
# -z -- just zenforos
# -C -- config only
# -S -- stat host, for bi and dstr
product_get_package_list_flag() {
  local flag=""
  local OPTIND opt
  while getopts ":c:v:r:izZCS" opt; do
    case "${opt}" in
        [izZCS])  flag="${flag}${opt}"
                ;;
        *)
                ;;
    esac
  done
  shift $((OPTIND-1))
  echo $flag
}

product_get_package_list() {
  local product="$1"; shift
  [ -z "$product" ] && { echo ": $FUNCNAME: undefined product "; exit 1; }

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

  echo ": $FUNCNAME: product=$product colo=$colo, version=$version, release=$release, flag=$flag" >&2
  local binary="foros-${product}"
  echo $flag | grep -q Z && binary="foros-${product}-zenpacks"
  [ ! -z "$version" ] && binary="${binary}-${version}"
  echo $flag | grep -q C && binary=""
  echo $flag | grep -q z && binary=""

  local config="foros-config-${product}-${colo} foros-config-${product}-${colo}-mgr foros-config-${product}-${colo}-local-mgr"
  echo "$flag" | grep -q z && config="foros-config-${product}-${colo}-zenoss"
  echo "$flag" | grep -q Z && config=""
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
