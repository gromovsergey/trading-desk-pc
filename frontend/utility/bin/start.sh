#!/bin/bash

project_path=/opt/foros/spa-ui
config_path=$project_path/etc/spa-ui/
log_path=$project_path/var/log
tmp_path=$project_path/var/tmp

profile=$1
if [ -z "$profile" ]; then
    available_profiles=`find $config_path | egrep application-.+[.]properties | sed -re 's/.*application-(.+)[.]properties/\1/;'`
    profiles_num=`echo $available_profiles | wc -w`
    if [ "$profiles_num" == "1" ]; then
        profile=$available_profiles
    fi
fi

if [ -z "$profile" ]; then
    echo "Profile should be defined"
    exit 1
fi

JAVA_VERSION=17
JAVA_BIN=java
if [ -z "`java -version 2>&1 | grep version | grep $JAVA_VERSION`" ]; then
    JAVA_PACKAGE=`rpm -qa | grep java-$JAVA_VERSION-oracle-devel`
    if [ -z "$JAVA_PACKAGE" ]; then
        echo "No oracle java $JAVA_VERSION installed on the system" >&2
        exit 1
    fi
    JAVA_BIN=`rpm -ql $JAVA_PACKAGE | grep /bin/java$`
fi
echo "JAVA_PATH=$JAVA_BIN"

set -e

# Default heap size for java server = total RAM / 4
let "HEAP_SIZE=`free -m | grep Mem: | sed -re 's/[Mem:[:blank:]]+([[:digit:]]+).*/\1/;'` / 4"

nohup \
  $JAVA_BIN \
    -Djava.net.preferIPv4Stack=true \
    -Dsun.jnu.encoding=UTF-8 \
    -Dfile.encoding=UTF-8 \
    -agentlib:jdwp=transport=dt_socket,address=55009,server=y,suspend=n \
    -d64 \
    -XX:+UnlockDiagnosticVMOptions \
    -XX:HeapDumpPath=${log_path} \
    -XX:+LogVMOutput \
    -XX:LogFile=${log_path}/jvm.log \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:+ExitOnOutOfMemoryError \
    -XX:+PrintGCDetails \
    -XX:+PrintGCTimeStamps \
    -Xmx${HEAP_SIZE}m \
    -Xms${HEAP_SIZE}m \
    -Djava.io.tmpdir=${tmp_path} \
    --add-opens java.base/java.lang=ALL-UNNAMED \
    -jar $project_path/lib/frontend-1.0-SNAPSHOT.jar \
    --spring.config.location=$config_path \
    --spring.profiles.active=$profile \
>>$project_path/var/log/spa-ui.log 2>>$project_path/var/log/spa-ui.log </dev/null &

echo $! > $project_path/var/java.pid

# NGINX
nginx -c "$project_path/etc/nginx/nginx.conf"

# Waiting java server to start
tail -F $project_path/var/log/spa-ui.log | egrep -m1 "Undertow started on port|Started ApplicationManager in"
