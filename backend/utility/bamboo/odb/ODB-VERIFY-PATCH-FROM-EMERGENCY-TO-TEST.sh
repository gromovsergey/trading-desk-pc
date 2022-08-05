#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

 # Scheduled to execute at 21:30

doc get_postgres_db_branch "test"
BRANCH=$POSTGRES_DB_BRANCH

doc get_latests_tag "$BRANCH" "svn+ssh://svn/home/svnroot/oix/db/tags"
ORACLE_VERSION="$LATESTS_TAG"

doc get_latests_tag "$BRANCH" "svn+ssh://svn/home/svnroot/oix/pgdb/tags"
POSTGRES_VERSION="$LATESTS_TAG"

doc get_latests_tag "$BRANCH" "svn+ssh://svn/home/svnroot/oix/bi/tags"
BI_VERSION="$LATESTS_TAG"

doc checkout_file "svn+ssh://svn/home/svnroot/oix/ui/trunk/utility/bamboo/oui-custom/OUICUST-ADVANCED-REFRESH-DATABASE.sh"
doc cp $CHECKOUT_FILE $WORKING_DIR/OUICUST-ADVANCED-REFRESH-DATABASE.sh

export bamboo__0_Source="emergency"
export bamboo__1_SchemaNumber="17"
export bamboo__2_RefreshOracle="yes"
export bamboo__3_RefreshPostgres="yes"
export bamboo__4_PatchOracleTill="$ORACLE_VERSION"
export bamboo__5_PatchPostgresTill="$POSTGRES_VERSION"
export bamboo__6_ReplicateTables="yes"
export bamboo__8_UsePostgresFullCopy="no"
export bamboo__9_PatchBiTill="$BI_VERSION"

doc $WORKING_DIR/OUICUST-ADVANCED-REFRESH-DATABASE.sh

exit 0

