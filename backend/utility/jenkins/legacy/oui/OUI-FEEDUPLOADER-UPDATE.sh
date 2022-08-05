#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

doc svn export svn+ssh://svn/home/svnroot/oix/ui-tools/rs-utilities/rs-utilities.sh $WORKING_DIR/rs-utilities.sh

execute_remote_ex "feeduploader" "stat-discover" "*.sh" <<-"EOF"
  WORKING_DIR=`pwd`
  . $WORKING_DIR/commons.sh

  pushd /u01/foros/rs-utilities
  if [ -e ./rs-utilities.sh ]; then
    doc ./rs-utilities.sh uninstall
  fi
  doc cp $WORKING_DIR/rs-utilities.sh ./rs-utilities.sh
  doc ./rs-utilities.sh install
  popd

EOF
[ "$?" != "0" ] && exit 1

exit 0

