#!/bin/bash

WORKING_DIR=`pwd`
. $WORKING_DIR/commons.sh

touch $WORKING_DIR/pgpass
chmod 600 $WORKING_DIR/pgpass
echo "epostdb00:5432:*:ro:adserver" > $WORKING_DIR/pgpass
export PGPASSFILE=$WORKING_DIR/pgpass
trap "rm -f $WORKING_DIR/pgpass" EXIT

doc  psql -h epostdb00 -p 5432 -U ro -d stat -c "'select test_consistence.test_all();'"

exit 0

