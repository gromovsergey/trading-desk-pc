#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

DESTINATION=${bamboo__1_Destination}
PATCH_TILL=${bamboo__2_PatchPostgresTill}
PATCH_BI_TILL=${bamboo__3_PatchBiTill}

doc fast_copy_statdb "stat-dev0" "$DESTINATION" "adserver_empty"
doc patch_postgres "$DESTINATION" "$PATCH_TILL" "$PATCH_BI_TILL"

exit 0

