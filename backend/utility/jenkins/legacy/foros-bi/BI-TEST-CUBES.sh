#!/bin/bash

# Each Monday, Tuesday, Wednesday, Thursday and Friday at 3:00 pm

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

moscow_nb_master_schemas="${bamboo_moscow_nb_master}" # Custom Report Cube,Campaign Dashboard Cube,Channel Cube
moscow_test_central_schemas="${bamboo_moscow_test_central}"
moscow_stage_central_schemas="${bamboo_moscow_stage_central}"

doc execute_remote oix-dev8 "$svnpath" <<-"EOF"
  #!/bin/bash
  WORKING_DIR=`pwd`
  . $WORKING_DIR/commons.sh

  # get saiku-client's version
  doc checkout_file "svn+ssh://svn.ocslab.com/home/svnroot/oix/ui/trunk/pom.xml"
  version=`cat $CHECKOUT_FILE | grep -A 2 com.foros.olap | tail -n 1 | grep -oPm1 "(?<=<version>)[^<]+"`

  # build 
  doc svn_export_folder "svn+ssh://svn/home/svnroot/oix/bi/trunk/utility/dev/cubes-tests"
  cd $CHECKOUT_FOLDER

  sed -e "s/###HOSTED_IN_UI###/$version/" -i pom.xml
  doc mvn -q clean package

  # copy it to an exchange folder
  doc cp -f target/cubes-tests-0.0.7.jar /tmp/
  exit 0
EOF

doc scp oix-dev8:/tmp/cubes-tests-0.0.7.jar $WORKING_DIR/cubes-tests.jar

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
    [ "$colo" = "moscow-stage-central" ] && connect="$schema"

    login="bi.devoixui" ; password="6d71fdbba3b0d20a1c0973b2eeafbfbc"
    url="https://bi.oix-rubytest.net/pentaho/plugin/saiku/api/"
    if [ "$colo" = "moscow-stage-central" ] ; then
      url="https://bi.oix-stage.net/pentaho/plugin/saiku/api/"
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
