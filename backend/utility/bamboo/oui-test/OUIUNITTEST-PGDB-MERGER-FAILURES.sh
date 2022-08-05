#! /bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh

# Schedule: Each Monday, Tuesday, Wednesday, Thursday and Friday every 3 hours from 10:00 am to 9:00

result=0

# Merger's old log files location
failure_dir="/opt/foros/pgdb/var/cache/failure"
for host in epostgres stat-discover ; do
  fail_count=`ssh $host "ls -l $failure_dir | tail -n +2 | wc -l"`
  if [ "$fail_count" != "0" ] ; then
    echo
    echo "========================================"
    echo "$host:$failure_dir --- $fail_count Merger failure(s)"
    ssh $host "ls $failure_dir | sed 's/^\([^_]\{1,\}\).*/\1/' | sort | uniq -c"
    echo "========================================"
    echo
    result=1
  else
    echo "Merger on $host - OK ($failure_dir)"
  fi
done

# Merger's new log files location
failure_dir="/opt/foros/pgdb/var/spool/merger/failure"
for host in spostdb0 stat-test stat-nbmaster stat-nbouiat stat-nbperf ; do
  fail_count=`ssh $host "ls -l $failure_dir | tail -n +2 | wc -l"`
  if [ "$fail_count" != "0" ] ; then
    echo
    echo "========================================"
    echo "$host:$failure_dir --- $fail_count Merger failure(s)"
    ssh $host "ls $failure_dir | sed 's/^\([^_]\{1,\}\).*/\1/' | sort | uniq -c"
    echo "========================================"
    echo
    result=1
  else
    echo "Merger on $host - OK ($failure_dir)"
  fi
done

exit $result

