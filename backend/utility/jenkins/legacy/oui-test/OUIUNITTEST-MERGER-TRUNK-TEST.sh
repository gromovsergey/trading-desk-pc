#! /bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Monday, Tuesday, Wednesday, Thursday and Friday at 9:00 am

PG_DATABASE="unittest_merger_1"
[ "${bamboo_ouiPGDatabase}" != "" ] && PG_DATABASE="${bamboo_ouiPGDatabase}"

# Empty by default, but an user can fill it to test a branch
if [ "${bamboo_ouiPGDBSvnPath}" != "" ] ; then
  get_svn_path ${bamboo_ouiPGDBSvnPath}
  PGDB_SVN_PATH=$SVN_PATH
else
  doc patch_postgres "$PG_DATABASE" "trunk" "Skip"  
  PGDB_SVN_PATH="trunk"
fi

doc apply_pg_sql_command "stat-dev0.ocslab.com" 5432 "$PG_DATABASE" "oix" \
  "'select test_functional.fill_one_of_every_entity_kind();'"

docl checkout_file svn+ssh://svn/home/svnroot/oix/ui/trunk/utility/dev/merger-performance-test/perf_test.py perf_test.py
docl cp $CHECKOUT_FILE $WORKING_DIR

doc merger_func_test $PGDB_SVN_PATH $PG_DATABASE

exit $?
