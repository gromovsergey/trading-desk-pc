#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Monday, Tuesday, Wednesday, Thursday and Friday,
#           10:00 MSK-20:00 MSK every 3 hours

PG_DBHOST="epostgres"
PG_DBPORT="5432"
PG_DB="pgsql_91_93"

ORA_DB="UNITTEST_UI_17"

get_pgdb_version "epostgres.ocslab.com" 5432 "stat" "ro"
doc patch_oracle "$ORA_DB" "$PGDB_VERSION" "trunk" "no" "no"
doc patch_pg_repl "$PG_DBHOST" "$PG_DBPORT" "$PG_DB" "oix" "trunk"

doc replicate_tables "$PG_DBHOST" "$PG_DBPORT" "$PG_DB"

doc patch_pg_statdb "$PG_DBHOST" "$PG_DBPORT" "$PG_DB" "oix" "branches/$PGDB_VERSION"

exit 0

