#!/bin/bash

HOME=/home/maint
[ ! -d /tmp/OUI-COMMON ] && mkdir -p /tmp/OUI-COMMON

. $WORKING_DIR/commons.util.sh
. $WORKING_DIR/commons.svn.sh
. $WORKING_DIR/commons.pgdb.sh
. $WORKING_DIR/commons.postgres.sh
. $WORKING_DIR/commons.cluster.sh
. $WORKING_DIR/commons.packages.sh
. $WORKING_DIR/commons.cms.sh
. $WORKING_DIR/commons.merger.sh
. $WORKING_DIR/commons.zenoss.sh
. $WORKING_DIR/commons.abt.sh

