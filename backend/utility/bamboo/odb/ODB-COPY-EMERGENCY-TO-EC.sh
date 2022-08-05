#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Monday, Tuesday, Wednesday, Thursday and Friday at 7:00 am

refresh_schema "oradev" 1521 "addbtc.ocslab.com" "ADSERVER_EC" "
  whenever sqlerror exit failure \n
  connect SYS_ADSERVER_EC_REFRESH/.ora_123@oradev/addbtc.ocslab.com \n
  set serveroutput on size 1000000 lines 999 \n
  exec sys.ADSERVER_EC_REFRESH_PROC \n" || exit 1

exit 0
