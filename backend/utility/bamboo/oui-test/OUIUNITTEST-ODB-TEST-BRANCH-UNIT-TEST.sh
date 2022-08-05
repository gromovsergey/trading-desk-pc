#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Server Host
HOST=${bamboo_ouiOraHost}
[ -z "$HOST" ] && HOST="ora-nb.ocslab.com"

# Server Port
PORT=${bamboo_ouiOraPort}
[ -z "$PORT" ] && PORT=1521

# Database Instance
INSTANCE=${bamboo_ouiOraInstance}
[ -z "$INSTANCE" ] && INSTANCE="addbnba.ocslab.com"

# Database Schema
SCHEMA=${bamboo_ouiOraSchema}
[ -z "$SCHEMA" ] && SCHEMA="unittest_db_2"

get_oracle_db_branch "test"
DO_PATCH="YES"
PATCH_VERSION=$ORA_DB_BRANCH
SVN_PATH=$ORA_DB_BRANCH

get_svn_path $SVN_PATH
echo Host=$HOST, Port=$PORT, Instance=$INSTANCE, Schema=$SCHEMA, Do the patching=$DO_PATCH, Patch to version=$PATCH_VERSION, SVN=$SVN_PATH

echo "Upgrading unittest types"
svn_export_folder "svn+ssh://svn.ocslab.com/home/svnroot/oix/db/$SVN_PATH/tests/unit"
UNIT_FOLDER=$CHECKOUT_FOLDER

doc apply_sql $HOST $PORT $INSTANCE $SCHEMA $UNIT_FOLDER/uninstall.sql

if [ "$DO_PATCH" = "YES" ] ; then
  doc patch_schema_replication $HOST $PORT $INSTANCE $SCHEMA "trunk"
  doc patch_schema $HOST $PORT $INSTANCE $SCHEMA $SVN_PATH "yes" "no"
fi

cd $UNIT_FOLDER
doc apply_sql $HOST $PORT $INSTANCE $SCHEMA install.sql

echo "Executing tests"
doc $UNIT_FOLDER/run.sh -h $HOST -p $PORT -db $INSTANCE -U $SCHEMA -W adserver

exit 0
