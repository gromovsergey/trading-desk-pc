#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.adclient.sh

VERSION=`abt_get_nb_version`

for COLOCATION in moscow-nb-master moscow-nb-oui-at moscow-nb-oui-perf ; do
  adclient_download_colocation_xml "$COLOCATION" "$VERSION"
  adclient_create_config_rpms $VERSION "trunk" $COLO_XML_FILE
  create_store "$COLOCATION-creatives"
  put_to_store "$COLOCATION-creatives" $WORKING_DIR/adclient_rpms
  rm -rf $WORKING_DIR/adclient_rpms
done

exit 0
