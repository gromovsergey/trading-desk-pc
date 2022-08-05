#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

VERSION_FILE=$WORKING_DIR/foros-ui-version
abt_build_trunk "foros/datastore" `cat_from_store foros-ui-version $VERSION_FILE`

exit 0

