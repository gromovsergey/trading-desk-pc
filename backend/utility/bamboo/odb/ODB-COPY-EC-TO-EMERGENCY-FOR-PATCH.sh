#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Refresh Oracle and Postgres UI_DEV_17
doc refresh_oracle_from_emergency "17"
doc refresh_postgres_from_emergency "17"

exit 0
