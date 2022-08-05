#!/bin/bash

WORKING_DIR=`pwd`
. $WORKING_DIR/commons.sh

ssh stat-nbmaster sudo -Hiu jmeter /opt/foros/ui-jmeter/bin/jmeter.sh
exit $?
