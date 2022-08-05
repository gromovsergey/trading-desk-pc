#!/bin/bash
run_from=$1
useCustomDb=$2


if [ -e /project/ui-ear/target/foros-ui.ear ];
then
  rm -rf /opt/foros/ui/lib/domains/domain1/lib/ && cp -r /project/target/lib/ /opt/foros/ui/lib/domains/domain1/lib/ && echo 'used libraries from project'
  cp /project/ui-ear/target/foros-ui.ear /opt/foros/ui/lib/autodeploy/ && echo 'used ear from project'
fi

if [ "$useCustomDb" != "false" ];
then
    sudo sed -i -r "s/dev_[0-9]{2}/dev_$useCustomDb/gI" /opt/foros/ui/lib/domains/domain1/config/domain.localhost.xml && echo "used custom db $useCustomDb"
fi

if [ "$run_from" == "mavenup" ];
then
  echo "UI will not be started for up from maven!"
else
  /opt/foros/manager/bin/cmgr start
fi