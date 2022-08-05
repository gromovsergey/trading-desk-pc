#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

VERSION=`abt_get_nb_version`
VERSION_FILE=$WORKING_DIR/foros-postgresdb-version
echo "$VERSION" > $VERSION_FILE

create_store "foros-postgresdb-version"
export BAMBOO_MOCK_NO="nb"
abt_setup_custom_build_mock_config "foros/pgadm" "el7"
abt_build_trunk "foros/pgadm" "$VERSION" -m $ABT_MOCK_CONFIG
put_to_store "foros-postgresdb-version" $VERSION_FILE

exit 0
