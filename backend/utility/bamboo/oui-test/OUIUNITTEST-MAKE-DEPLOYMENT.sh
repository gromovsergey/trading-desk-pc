#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Monday, Tuesday, Wednesday, Thursday and Friday every 15 minutes from 10:00 am to 9:00 pm

FROM_VERSION="`svn cat svn+ssh://svn/home/svnroot/oix/pgdb/trunk/RPM/SPECS/abt | sed -n 's|^\s\+local pgMininalVersion="\(.*\)"|\1|p'`"
TO_VERSION="trunk"

svn_export_folder "svn+ssh://svn.ocslab.com/home/svnroot/oix/db/trunk/util/patch_db"
PATCH_DB_DIR=$CHECKOUT_FOLDER

RESULT=0

echo ": check pgdb patches"
doc $PATCH_DB_DIR/make_deployment.php -statdb \
  -r svn+ssh://svn.ocslab.com/home/svnroot/oix/pgdb \
  -from $FROM_VERSION -to $TO_VERSION -o $WORKING_DIR/pgdb \
  -l $WORKING_DIR/make_deployment.pgdb.log || RESULT=1
echo; echo

echo ": check streams-replication patches"
doc $PATCH_DB_DIR/make_deployment.php -statdb \
  -r svn+ssh://svn.ocslab.com/home/svnroot/oix/streams-replication \
  -from $FROM_VERSION -to $TO_VERSION -o $WORKING_DIR/replication \
  -prefix statdb -product replication \
  -l $WORKING_DIR/make_deployment.repl.log || ((RESULT+=1))

exit $RESULT

