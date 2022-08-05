#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Refresh Oracle and Postgres UI_DEV_18
doc refresh_oracle_from_test "18"
doc refresh_postgres_from_test "18"

exit 0

