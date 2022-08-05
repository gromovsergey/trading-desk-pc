#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

#doc refresh_oracle_from_nb_master "8"
doc refresh_postgres_from_nb_master "8"

exit 0

