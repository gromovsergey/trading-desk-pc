#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

doc fast_copy_statdb "stat-dev0" "test_trunk_full_copy" "test_full_copy"
doc patch_postgres "test_trunk_full_copy" "trunk" "trunk"

exit 0

