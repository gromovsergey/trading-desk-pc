#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.bi.sh

VERSION_FILE=$WORKING_DIR/foros-ui-version
VERSION=`cat_from_store foros-ui-version $VERSION_FILE`

doc install_packages_from_repo "pentaho-test" "foros-bi-mondrian-moscow-nb-master" "$VERSION"
doc bi_install_analisys_datasources "pentaho-test" "moscow-nb-master"

exit 0


