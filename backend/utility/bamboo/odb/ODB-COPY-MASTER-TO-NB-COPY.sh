#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Scheduled to execute after Patch NB Master

#refresh_schema "ora-nb" 1521 "addbnba.ocslab.com" "NB_COPY" "
#  whenever sqlerror exit failure \n
#  connect sys_nb_refresh/.ora_123@ora-nb/addbnba.ocslab.com \n
#  set serveroutput on size 1000000 lines 999 \n
#  exec sys.NBC_REFRESH \n" || exit 1

# patch replication for ADDB NB AUTO
# doc patch_ora_replication "ora-nb.ocslab.com" 1521 "addbnba.ocslab.com" "S"

exit 0
