#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Scheduled after ODB-COPY-TEST-TO-TC

refresh_schema "oradev" 1521 "addbtc.ocslab.com" "ADSERVER_LC" "
  whenever sqlerror exit failure \n
  connect sys_lc_refresh/.ora_123@oradev/addbtc.ocslab.com \n
  set serveroutput on size 1000000 lines 999 \n
  exec sys.ADSERVER_LC_REFRESH \n" || exit 1

doc patch_oracle "ADSERVER_LC" "trunk" "trunk" "no" "no"

exit 0

