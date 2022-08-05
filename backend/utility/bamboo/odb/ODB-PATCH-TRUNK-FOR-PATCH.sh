#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Monday, Tuesday, Wednesday, Thursday and Friday every hour from 10:00 am to 9:00 pm

# Patch NB_COPY8 till trunk
#doc patch_oracle "NB_COPY8" "trunk" "trunk" "no" "no"
doc patch_postgres "nb_copy8" "trunk" "trunk"
doc patch_pg_bi "stat-dev0" 5432 "nb_copy8" "bi" "trunk"

exit 0
