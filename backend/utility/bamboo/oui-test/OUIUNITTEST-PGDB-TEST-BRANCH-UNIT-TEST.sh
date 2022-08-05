#! /bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule
# Daily every 3 hours from 11:00 am to 7:00

echo ": Patching the database"
get_pgdb_version "stat-test" 5432 "stat_test" "oix"
doc patch_pg_repl "stat-dev0" 5432 "unittest_db_2" "oix" "trunk"
doc replicate_tables "stat-dev0" 5432 "unittest_db_2"
doc patch_pg_statdb "stat-dev0" 5432 "unittest_db_2" "oix" "branches/$PGDB_VERSION"
# get_pgdb_bi_version "stat-test" 5432 "stat" "bi"
# doc patch_pg_bi "stat-dev0" 5432 "unittest_db_2" "bi" "branches/$BI_VERSION"

echo ": Checkout tests"
svn_export_folder "svn+ssh://svn/home/svnroot/oix/pgdb/branches/$PGDB_VERSION/tests" $WORKING_DIR/"tests"
export PGPASSFILE=$WORKING_DIR/.pgpass
echo "stat-dev0:5432:*:oix:adserver" > $PGPASSFILE
chmod 0600 $PGPASSFILE
doc $CHECKOUT_FOLDER/update.sh -h "stat-dev0" -p 5432 -d "unittest_db_2" -U "oix"

echo ": Executing tests"
GLOBIGNORE=* ; doc apply_pg_sql_command "stat-dev0" 5432 "unittest_db_2" "oix" "'select * from test.run_all();'"
grep -qE '(ERROR|FAIL)' $PG_SQL_FILE.log && exit 1

exit 0
