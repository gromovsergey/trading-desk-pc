#!/bin/bash

WORKING_DIR=`pwd`
. $WORKING_DIR/commons.sh

# get NB version
VERSION=`ssh oui-nbmaster0 rpm -q --qf '%{version}' foros-ui`
install_packages_from_repo "gp-test0" "foros-ui-tests" "$VERSION"

# OUI-26510
ssh gp-test0 sudo -u uiuser /opt/foros/ui/bin/tests/jmeter/startup.sh
exit $?

