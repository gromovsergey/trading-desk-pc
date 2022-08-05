#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
TMP_WORKING_DIR="/tmp/jenkins-bamboo$WORKING_DIR"
rm -rf "$TMP_WORKING_DIR"
mkdir -p "$TMP_WORKING_DIR"
cp -rf "$WORKING_DIR"/* "$TMP_WORKING_DIR"/
WORKING_DIR="$TMP_WORKING_DIR"

. $WORKING_DIR/commons.sh

DBN=${bamboo__1_SchemaNumber}
PATCH_TILL=${bamboo__2_PatchPostgresTill}
FULL_COPY=${bamboo__3_UsePostgresFullCopy}
PATCH_BI_TILL=${bamboo__4_PatchBiTill}

doc refresh_postgres "epostdb00.ocslab.com" "emergency_copy" "$DBN" "$FULL_COPY"
doc patch_postgres "ui_dev_$DBN" "$PATCH_TILL" "$PATCH_BI_TILL"

exit 0
