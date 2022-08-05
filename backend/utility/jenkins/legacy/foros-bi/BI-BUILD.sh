#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

VERSION_FILE=$WORKING_DIR/foros-ui-version
abt_build_trunk "foros/bi" `cat_from_store foros-ui-version $VERSION_FILE` -D "'%skipAnalysisDatasources true'"

exit 0


