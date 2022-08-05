#!/bin/bash

postgresdb_download_colocation_xml() {
  local COLO_NAME=$1
  local VERSION=$2

  [ -z "$VERSION" ] && { echo ": $FUNCNAME: Undefined VERSION" ; exit 1 ; }
  [ -z "$COLO_NAME" ] && { echo ": $FUNCNAME: Undefined COLO_NAME" ; exit 1 ; }

  download_plugin_colocation_xml "$COLO_NAME" "PGADM" "$VERSION" "trunk"
  return $?
}

postgresdb_create_config_rpms() {
  local VERSION=$1
  local BRANCH=$2
  local COLO_XML_FILE=$3

  [ -z "$BRANCH" ] && { echo ": $FUNCNAME: Undefined BRANCH" ; exit 1 ; }
  [ -z "$VERSION" ] && { echo ": $FUNCNAME: Undefined VERSION" ; exit 1 ; }
  [ -z "$COLO_XML_FILE" ] && { echo ": $FUNCNAME: Undefined COLO_XML_FILE" ; exit 1 ; }

  create_config_rpms "PGADM" "$VERSION" "$BRANCH" "$COLO_XML_FILE" || \
    { echo ": $FUNCNAME: Could not create config rpms for PGADM" ; exit 1 ; }


  rm -rf $WORKING_DIR/postgresdb_rpms && \
    mv $WORKING_DIR/rpms $WORKING_DIR/postgresdb_rpms || \
    { echo ": $FUNCNAME: Could not move config rpms for PGADM" ; exit 1 ; }
}

postgresdb_install_config_rpms() {
  local HOST=$1

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }

  [ -d $WORKING_DIR/postgresdb_rpms ] || { echo ": $FUNCNAME: Directory postgresdb_rpms not found"; exit 1; }

  local rpms=`find $WORKING_DIR/postgresdb_rpms -type f -name "*-mgr*.rpm" ! -name "*zenoss*" | sort`
  rpms="$rpms `find -L "$WORKING_DIR/postgresdb_rpms" -name '*.rpm' ! -name '*-mgr*' ! -name '*zenoss*'`"

  [ -z "$rpms" ] && { echo ": $FUNCNAME: Packages not found in directory"; exit 1; }

  install_packages "$HOST" $rpms

  return 0
}

pgadm_get_package_list() {
  product_get_package_list "pgadm" $@
}
