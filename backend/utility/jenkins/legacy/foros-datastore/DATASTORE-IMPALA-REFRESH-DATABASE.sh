#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.impala.sh

SRC=${bamboo__01_Source}
DST=${bamboo__02_Destination}
PARTITIONS=${bamboo__03_Partitions}
EXCLUDED=${bamboo__04_ExcludedTables}
COLO="moscow-${SRC}-central"

[ -z "$SRC" ] && { echo ": $FUNCNAME: undefined SRC "; exit 1; }
[ -z "$DST" ] && { echo ": $FUNCNAME: undefined DST "; exit 1; }
[ -z "$PARTITIONS" ] && { echo ": $FUNCNAME: undefined PARTITIONS "; exit 1; }

if [ "${DST,,}" = "stage" -o "${DST,,}" = "test" ]; then
  echo ": could not refresh $DST database"
  exit 1
fi

if echo "$EXCLUDED" | grep 'entity'; then
  EXCLUDED="$EXCLUDED `impala_get_replicated_tables $COLO`"
fi
doc impala_refresh_database $SRC $DST $PARTITIONS $EXCLUDED

doc update_hadoop_applied_patches $SRC $DST

exit 0
