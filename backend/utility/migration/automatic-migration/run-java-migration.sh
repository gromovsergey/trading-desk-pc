#!/bin/sh

export COLOCATION_PROPERTIES_FILE=/opt/foros/ui/lib/domains/domain1/config/colocation.properties
export FOROS_LD_PATH=/opt/foros/ui/lib
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$FOROS_LD_PATH
export CLASSPATH=$CLASSPATH:`echo /opt/foros/ui/lib/domains/domain1/lib/*.jar | sed -e 's/jar /jar:/g'`
export CLASSPATH=$CLASSPATH:`echo /opt/foros/ui/var/tmp/lib/*.jar | sed -e 's/jar /jar:/g'`

COMMAND="java -Djava.library.path=$FOROS_LD_PATH -Dforos.httpfunctions.lib=HttpJavaFunctions $@"
echo "$COMMAND"
eval "$COMMAND"
