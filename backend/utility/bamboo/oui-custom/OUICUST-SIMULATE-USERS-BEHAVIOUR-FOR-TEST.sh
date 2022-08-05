#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: every day at 9:00 am

SVNROOT="svn+ssh://svn/home/svnroot/oix/ui"
get_latests_branch 1 "$SVNROOT/branches"
docl svn_export_folder "$SVNROOT/branches/$LATESTS_BRANCH/utility/db/uugen"

rm -rf $CHECKOUT_FOLDER/var
get_from_store uugen $CHECKOUT_FOLDER/var

$CHECKOUT_FOLDER/uugen.sh daily
RESULT=$?

clean_store uugen
put_to_store uugen $CHECKOUT_FOLDER/var

exit $RESULT
