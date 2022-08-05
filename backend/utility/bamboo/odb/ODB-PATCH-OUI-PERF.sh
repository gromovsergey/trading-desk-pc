#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# To re-create Oracle replication's logs
#doc ora_recreate_streams "oraperf.ocslab.com" 1721 "addbpt.ocslab.com"
#doc patch_schema_replication "oraperf.ocslab.com" 1721 "addbpt.ocslab.com" "NIGHTLY_PERF"
#doc patch_ora_replication "oraperf.ocslab.com" 1721 "addbpt.ocslab.com" "NIGHTLY_PERF"
#doc patch_schema "oraperf.ocslab.com" 1721 "addbpt.ocslab.com" "NIGHTLY_PERF" "trunk" "yes" "yes"

#echo ": Executing SVN:/foros/db/trunk/util/compare_schemas/compare_schemas.sql"
#docl checkout_file "svn+ssh://svn.ocslab.com/home/svnroot/oix/db/trunk/util/compare_schemas/compare_schemas.sql"
#doc apply_sql "oraperf.ocslab.com" 1721 "addbpt.ocslab.com" "NIGHTLY_PERF" $CHECKOUT_FILE \
#  "NIGHTLY_BUILDS" "adserver" "ora-nb.ocslab.com" 1521 "addbnbm.ocslab.com"

exit 0

