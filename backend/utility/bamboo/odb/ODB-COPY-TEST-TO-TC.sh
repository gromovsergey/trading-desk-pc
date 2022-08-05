#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Monday, Tuesday, Wednesday, Thursday and Friday at 1:00 am

refresh_schema "oradev" 1521 "addbtc.ocslab.com" "ADSERVER_TC" "
  whenever sqlerror exit failure \n
  connect sys_tc_refresh/.ora_123@oradev/addbtc.ocslab.com \n
  set serveroutput on size 1000000 lines 999 \n
  exec sys.ADSERVER_TC_REFRESH \n" || exit 1

exit 0
