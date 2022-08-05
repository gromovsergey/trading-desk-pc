#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

HOST="stat-discover"
BASEDIR="/u01/foros/rs-utilities"

echo -ne "Checking host $HOST: ... \t"
TABLE=`ssh -o 'BatchMode yes' feeduploader@$HOST -- crontab -l`
[ "$?" != 0 ] && { echo -e "FAIL\nERROR: Could not retrieve crontab records"; exit 1; }
echo "OK"

echo -ne "Checking crontab: ... \t"
UTILITIES=`echo "$TABLE" | sed -n -e 's|^[^#].*/rs-utilities.sh \(.*\)$|\1|gp'`
[ -z "$UTILITIES" ] && { echo -e "FAIL\nERROR: There are no scheduled utilities"; exit 1; }
echo "OK"

exit 0
