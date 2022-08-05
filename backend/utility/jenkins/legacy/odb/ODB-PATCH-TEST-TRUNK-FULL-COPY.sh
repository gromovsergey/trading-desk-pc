#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Monday, Tuesday, Wednesday, Thursday and Friday at 9:00 pm

doc patch_postgres "test_trunk_full_copy" "trunk" "trunk"

exit 0
