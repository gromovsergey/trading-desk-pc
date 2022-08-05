#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

moscow_nb_master_schemas="${bamboo_moscow_nb_master}" # Custom Report Cube,Campaign Dashboard Cube,Channel Cube
moscow_test_central_schemas="${bamboo_moscow_test_central}"
moscow_stage_central_schemas="${bamboo_moscow_stage_central}"

doc wget -q http://maven.ocslab.com/nexus/content/repositories/foros/com/foros/cube/cubes-tests/0.0.6/cubes-tests-0.0.6.jar -O $WORKING_DIR/cubes-tests.jar

result=0
all_schemas=0
invalid_schemas=0

function test_colo() {
  local colo=$1
  shift
  local schemas=$@
  
  echo
  echo "----------------------------------------------------------------------------"
  echo "                           $colo"
  echo "$schemas"
  echo "----------------------------------------------------------------------------"

  IFS=","
  for schema in $schemas ; do
    IFS=" "
    echo
    echo "*** Schema '$schema' ***"

    connect="$schema $colo"
    login="Admin" ; password="password"
    url="https://bi.foros-rubytest.net/pentaho/plugin/saiku/api/"
    if [ "$colo" = "moscow-stage-central" ] ; then
      connect="$schema"
      login="admin" ; password="gth05cbr"
      url="https://bi.foros-stage.net/pentaho/plugin/saiku/api/"
    fi    

    echo ": java -jar $WORKING_DIR/cubes-tests.jar $url $login *** \"[$connect].[$connect].[$connect]\""
    java -jar $WORKING_DIR/cubes-tests.jar $url $login $password "[$connect].[$connect].[$connect]"
    [ "$?" != "0" ] && { result=1 ; ((invalid_schemas++)) ; }

    ((all_schemas++))
    IFS=","
  done
  IFS=" "
}

test_colo "moscow-nb-master" "$moscow_nb_master_schemas"
test_colo "moscow-test-central" "$moscow_test_central_schemas"
test_colo "moscow-stage-central" "$moscow_stage_central_schemas"

echo
echo "TOTAL: $invalid_schemas invalid schema(s) from $all_schemas"

exit $result
