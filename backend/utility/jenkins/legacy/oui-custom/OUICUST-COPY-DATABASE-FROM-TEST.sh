#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

DBN=${bamboo__1_SchemaNumber}
PATCH_TILL=${bamboo__2_PatchPostgresTill}
FULL_COPY=${bamboo__3_UsePostgresFullCopy}
PATCH_BI_TILL=${bamboo__4_PatchBiTill}

if [ "$PATCH_TILL" = "trunk" ]; then
  doc refresh_postgres "stat-test.ocslab.com" "test_trunk_copy" "$DBN" "$FULL_COPY"
else
  doc refresh_postgres "stat-test.ocslab.com" "test_copy" "$DBN" "$FULL_COPY"
fi

doc patch_postgres "ui_dev_$DBN" "$PATCH_TILL" "$PATCH_BI_TILL"

exit 0
