#!/bin/bash

project_path=/opt/foros/spa-ui
PID=`cat "$project_path/var/java.pid" ||:`
echo "PID is $PID"

nginx -c "$project_path/etc/nginx/nginx.conf" -s quit

if [ -n "$PID" ]; then
    kill $PID 2>/dev/null && sleep 3 ||:
    kill -0 $PID 2>/dev/null && echo "Process $PID still exists" >&2 ||:
fi
