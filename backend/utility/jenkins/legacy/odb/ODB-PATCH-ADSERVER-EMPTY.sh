#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

get_pgdb_version "epostdb00.ocslab.com" "5432" "stat"
doc patch_postgres "adserver_empty" "$PGDB_VERSION" "$BI_VERSION"

exit 0
