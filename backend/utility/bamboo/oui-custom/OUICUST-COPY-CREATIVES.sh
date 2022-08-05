#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.ui.sh

[ -z "${bamboo__1_Source}" ] && { echo "Undefined parameter: _1_Source"; exit 1; }
[ -z "${bamboo__2_Host}" ] && { echo "Undefined parameter: _2_Host"; exit 1; }

SOURCE="`echo ${bamboo__1_Source} | tr '[:lower:]' '[:upper:]'`"
HOST=${bamboo__2_Host}

echo $SOURCE | grep -qi master && SOURCE="TRUNK"

echo ": Updating  on '$HOST' from '$SOURCE'"
ui_update_dir $HOST "/opt/foros/ui/var/www/fmroot/Creatives" "$SOURCE"
ui_update_dir $HOST "/opt/foros/ui/var/www/tags" "$SOURCE"
ui_update_dir $HOST "/opt/foros/ui/var/www/fmroot/Templates" "$SOURCE"

exit 0
