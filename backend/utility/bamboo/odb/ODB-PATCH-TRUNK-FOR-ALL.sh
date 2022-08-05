#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Monday, Tuesday, Wednesday, Thursday and Friday at 9:00 pm

# Patch UI_DEV_12 till trunk
#doc patch_oracle "UI_DEV_12" "trunk" "trunk" "no" "no"
#doc replicate_tables "stat-dev0.ocslab.com" 5432 "ui_dev_12"
doc patch_postgres "ui_dev_12" "trunk" "trunk"
doc patch_postgres "test_trunk_copy" "trunk" "S"
doc patch_postgres "test_trunk_full_copy" "trunk" "S"
doc patch_pg_bi "stat-dev0" 5432 "ui_dev_12" "bi" "trunk"

exit 0
