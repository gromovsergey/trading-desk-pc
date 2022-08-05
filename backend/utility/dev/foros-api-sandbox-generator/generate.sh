#!/bin/sh

BASEDIR=`dirname $0`
CP=
for i in `ls $BASEDIR/target/lib/*.jar`
do
  CP=${CP}:${i}
done

#DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=59009"
java $DEBUG $@ -cp "$BASEDIR/target/foros-api-sandbox-generator-trunk-tests.jar:$CP" \
    com.foros.rs.sandbox.ForosApiSandboxGenerator \
    ../../../rs-client/java/src/test/resources/sandbox.properties \
    generated-rs-client-test.properties
