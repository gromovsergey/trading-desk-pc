#!/bin/bash

WORKING_DIR=`pwd`
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.confluence.pgtables.sh

collect_artifacts() {
  echo ": $FUNCNAME: $@"
  local artdir="/u01/Bamboo/bamboo-home/xml-data/build-dir/ODB-CONFLUENCEGENERATEIMPALAPAGES-JOB1/arts"
  rm -rf "$artdir"
  mkdir -p "$artdir"
  cp $@ $artdir
}

generate_impalatables "${bamboo_password}"
RESULT=$?
collect_artifacts $WORKING_DIR/*.json $WORKING_DIR/*log

exit $RESULT
