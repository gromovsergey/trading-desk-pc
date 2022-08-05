#!/bin/bash

WORKING_DIR=$(cd `dirname $0`; pwd)
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.dstr.sh

CLUSTER=moscow-hadoop-central
BRANCH=branches/dev/DSTRADM-13

check_globals CLUSTER BRANCH


case $BAMBOO_SUBTASK in
  CLEANUP)
    doc dstr_perf_test $CLUSTER $BRANCH "01-cleanup";;
  RECREATESCHEMA)
    doc dstr_perf_test $CLUSTER $BRANCH "02-recreate-schema";;
  RESTOREDATA)
    doc dstr_perf_test $CLUSTER $BRANCH "03-restore-data";;
  COPYBUNDLES)
    doc dstr_perf_test $CLUSTER $BRANCH "04-copy-bundles";;
  SUSPENDBUNDLES)
    doc dstr_perf_test $CLUSTER $BRANCH "05-suspend-bundles";;
  SYNCTABLE)
    doc dstr_perf_test $CLUSTER $BRANCH "06-run-synctable";;
  LOGPROC)
    doc dstr_perf_test $CLUSTER $BRANCH "07-run-logproc";;
  RESUMEBUNDLES)
    doc dstr_perf_test $CLUSTER $BRANCH "08-resume-bundles";;
  *)
    echo "Unknown subtask '$BAMBOO_SUBTASK'"; exit 1;;
esac

exit 0
