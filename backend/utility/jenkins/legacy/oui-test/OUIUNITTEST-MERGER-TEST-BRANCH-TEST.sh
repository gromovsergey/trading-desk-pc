#! /bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Monday, Tuesday, Wednesday, Thursday and Friday at 9:00 am

PG_DATABASE="unittest_merger_2"
[ "${bamboo_ouiPGDatabase}" != "" ] && PG_DATABASE="${bamboo_ouiPGDatabase}"

# Empty by default, but an user can fill it to test a branch
if [ "${bamboo_ouiPGDBSvnPath}" != "" ] ; then
  get_svn_path ${bamboo_ouiPGDBSvnPath}
  PGDB_SVN_PATH=$SVN_PATH
else
  get_pgdb_version "stat-test.ocslab.com" 5432 "stat_test"
  doc patch_postgres "$PG_DATABASE" "$PGDB_VERSION" "Skip"  
  PGDB_SVN_PATH="branches/$PGDB_VERSION"
fi

doc apply_pg_sql_command "stat-dev0.ocslab.com" 5432 "$PG_DATABASE" "oix" \
  "'select test_functional.fill_one_of_every_entity_kind();'"

get_latests_branch 1 "svn+ssh://svn/home/svnroot/oix/ui/branches"
docl checkout_file svn+ssh://svn/home/svnroot/oix/ui/branches/$LATESTS_BRANCH/utility/dev/merger-performance-test/perf_test.py perf_test.py
docl cp $CHECKOUT_FILE $WORKING_DIR

doc merger_func_test $PGDB_SVN_PATH $PG_DATABASE

exit $?
