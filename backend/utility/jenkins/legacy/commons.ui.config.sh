#!/bin/bash

ui_download_colocation_xml() {
  local colo_name=$1
  local version=$2

  [ -z "$version" ] && { echo ": $FUNCNAME: Undefined version" ; exit 1 ; }
  [ -z "$colo_name" ] && { echo ": $FUNCNAME: Undefined colo_name" ; exit 1 ; }

  download_plugin_colocation_xml "$colo_name" "FOROS-UI" "$version" "trunk"
  return $?
}

ui_create_colocation_xml() {
  local host=$1
  local version=$2
  local branch=$3

  [ -z "$host" ] && { echo ": $FUNCNAME: Undefined host" ; exit 1 ; }
  [ -z "$version" ] && { echo ": $FUNCNAME: Undefined version" ; exit 1 ; }
  [ -z "$branch" ] && { echo ": $FUNCNAME: Undefined branch" ; exit 1 ; }

  export CMS_PG_HOST=$PG_DBHOST
  export CMS_PG_DB=$PG_DB
  export CMS_IMPALA_DB=$PG_DB

  create_plugin_colocation_xml "$host" "FOROS-UI" "$version" "$branch"
  local result=$?

  return $result
}

ui_create_config_rpms() {
  local version=$1
  local branch=$2
  local colo_xml_file=$3

  [ -z "$branch" ] && { echo ": $FUNCNAME: Undefined branch" ; exit 1 ; }
  [ -z "$version" ] && { echo ": $FUNCNAME: Undefined version" ; exit 1 ; }
  [ -z "$colo_xml_file" ] && { echo ": $FUNCNAME: Undefined colo_xml_file" ; exit 1 ; }

  create_config_rpms "FOROS-UI" "$version" "$branch" "$colo_xml_file" || \
    { echo ": $FUNCNAME: Could not create config rpms for FOROS-UI" ; exit 1 ; }

  rm -rf $WORKING_DIR/ui_rpms && \
    mv $WORKING_DIR/rpms $WORKING_DIR/ui_rpms || \
    { echo ": $FUNCNAME: Could not move config rpms for FOROS-UI" ; exit 1 ; }

  return 0
}

ui_install_config_rpms() {
  local host=$1

  [ -z "$host" ] && { echo ": $FUNCNAME: Undefined host" ; exit 1 ; }

  [ -d $WORKING_DIR/ui_rpms ] || { echo ": $FUNCNAME: Directory ui_rpms not found"; exit 1; }

  local rpms=`find $WORKING_DIR/ui_rpms -type f -name "*-mgr*.rpm" | sort`
  rpms="$rpms `find -L "$WORKING_DIR/ui_rpms" -name '*.rpm' ! -name '*-mgr*' ! -name '*-zenoss*'`"

  [ -z "$rpms" ] && { echo ": $FUNCNAME: Packages not found in directory"; exit 1; }

  install_packages "$host" $rpms

  return 0
}

