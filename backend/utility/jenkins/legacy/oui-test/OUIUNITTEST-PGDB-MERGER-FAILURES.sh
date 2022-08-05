#! /bin/bash

set -o pipefail

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Monday, Tuesday, Wednesday, Thursday and Friday every 3 hours from 10:00 am to 9:00

result=0
failure_dir="/opt/foros/pgdb/var/spool/merger/failure"
for host in epostdb00 spostdb0 stat-test stat-nbouiat stat-nbperf; do
  fail_count=`ssh $host "ls -1U $failure_dir" | wc -l`
	if [ $? != 0 ]; then
		echo "Failed to retrieve data from $host"
		result=1
  elif [ "$fail_count" != "0" ]; then
    echo
    echo "========================================"
    echo "$host:$failure_dir --- $fail_count Merger failure(s)"
    ssh $host "ls -1U $failure_dir | sed 's/^\([^_]\{1,\}\).*/\1/' | tr A-Z a-z | sort | uniq -c"
    echo "========================================"
    echo
    result=1
  else
    echo "Merger on $host - OK ($failure_dir)"
  fi
done

exit $result
