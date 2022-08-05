#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

#doc patch_schema_replication "ora-nb.ocslab.com" 1521 "addbnbm.ocslab.com" "NIGHTLY_BUILDS"
#doc patch_ora_replication "ora-nb.ocslab.com" 1521 "addbnbm.ocslab.com" "NIGHTLY_BUILDS"
#doc patch_schema "ora-nb.ocslab.com" 1521 "addbnbm.ocslab.com" "NIGHTLY_BUILDS" "trunk" "yes" "yes"

exit 0
