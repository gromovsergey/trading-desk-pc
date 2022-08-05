#!/bin/bash

CURR_PATH=`pwd`
PROPERTIES_FILE=/opt/foros/ui/lib/domains/domain1/config/colocation.properties
FOROS_LD_PATH=/opt/foros/ui/lib

export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$FOROS_LD_PATH
export CLASSPATH=$CLASSPATH:add-channel-triggers-jar-with-dependencies.jar
echo $CLASSPATH

java -Djava.library.path=$FOROS_LD_PATH -Dforos.httpfunctions.lib=HttpJavaFunctions -jar add-channel-triggers-jar-with-dependencies.jar $PROPERTIES_FILE export.csv
