#! /bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule on every commit to PGDB

echo ": Patching the database"
doc patch_postgres "unittest_db_1" "trunk" "trunk"
doc pgdb_do_epic "update.sh" "unittest_db_1"

echo ": Executing tests"
GLOBIGNORE=* ; doc apply_pg_sql_command "stat-dev0" 5432 "unittest_db_1" "foros" "'select * from test.run_all();'"
grep -qE '(ERROR|FAIL)' $PG_SQL_FILE.log && exit 1

exit 0
