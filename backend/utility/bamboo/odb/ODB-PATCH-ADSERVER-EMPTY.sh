#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

doc get_ora_version "oradev.ocslab.com" 1521 "addbdev.ocslab.com" "ADSERVER_EMPTY" "replication"
REPLICATION_VERSION=$ORA_VERSION

doc get_ora_version "oradev.ocslab.com" 1521 "addbdev.ocslab.com" "ADSERVER_EMPTY" "oixdb"

doc get_pgdb_version "stat-dev0.ocslab.com" "5432" "adserver_empty" "oix"
PGDB_VERSION=`cat $PG_SQL_FILE.log`

doc get_pgdb_bi_version "stat-dev0.ocslab.com" "5432" "adserver_empty" "oix"
BI_VERSION=`cat $PG_SQL_FILE.log`

for v_name in REPLICATION_VERSION ORA_VERSION PGDB_VERSION BI_VERSION; do
  echo ": $v_name = ${!v_name}"
  [ -z "${!v_name}" ] && { echo "Undefined $v_name"; exit 1; }
done


doc patch_schema_replication_using_branch "tags/$ORA_VERSION" "oradev.ocslab.com" 1521 "addbdev.ocslab.com" "ADSERVER_EMPTY" "$REPLICATION_VERSION"

doc patch_ora_replication_using_branch "tags/$ORA_VERSION" "oradev.ocslab.com" 1521 "addbdev.ocslab.com" "ADSERVER_EMPTY" "$REPLICATION_VERSION"

doc patch_schema_using_branch "tags/$ORA_VERSION" "oradev.ocslab.com" 1521 "addbdev.ocslab.com" "ADSERVER_EMPTY" "$ORA_VERSION" "yes"

doc patch_pg_repl "stat-dev0.ocslab.com" "5432" "adserver_empty" "oix" "$REPLICATION_VERSION"

doc patch_pg_statdb "stat-dev0.ocslab.com" "5432" "adserver_empty" "oix" "$PGDB_VERSION"

doc patch_pg_bi "stat-dev0.ocslab.com" "5432" "adserver_empty" "bi" "$BI_VERSION"

exit 0
