#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

[ -z "${bamboo__1_Database}" ] && { echo "Undefined parameter: _1_Database"; exit 1; }
[ -z "${bamboo__2_PgdbPatchTill}" ] && { echo "Undefined parameter: _2_PgdbPatchTill"; exit 1; }
[ -z "${bamboo__3_BiPatchTill}" ] && { echo "Undefined parameter: _3_BiPatchTill"; exit 1; }

PGDB=${bamboo__1_Database}
PGDB_PATCH_TILL=${bamboo__2_PgdbPatchTill}
BI_PATCH_TILL=${bamboo__3_BiPatchTill}

doc patch_postgres "$PGDB" "$PGDB_PATCH_TILL" "$BI_PATCH_TILL"

exit 0
