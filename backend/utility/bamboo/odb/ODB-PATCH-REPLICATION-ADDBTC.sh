#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

doc patch_ora_replication "oradev.ocslab.com" 1521 "addbtc.ocslab.com" "S"

exit 0
