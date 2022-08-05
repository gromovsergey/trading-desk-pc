#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.dstr.sh

COLOCATION="${bamboo_Colocation}"

doc dstr_start $COLOCATION
