#! /bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule on every commit to PGDB

echo ": Patching the database"
get_pgdb_version "epostdb00" 5432 "stat"
doc patch_postgres "unittest_db_3" "$PGDB_VERSION" "$BI_VERSION"
doc pgdb_do_epic "update.sh" "unittest_db_3"

echo ": Executing tests"
GLOBIGNORE=* ; doc apply_pg_sql_command "stat-dev0" 5432 "unittest_db_3" "foros" "'select * from test.run_all();'"
grep -qE '(ERROR|FAIL)' $PG_SQL_FILE.log && exit 1

exit 0
