#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.adclient.sh

get_from_store "moscow-nb-master-creatives" $WORKING_DIR/adclient_rpms

echo "---- FOROS Pagesense -----------------"
adclient_install_config_rpms "ads-nbmaster" "foros-pagesense"

exit 0
