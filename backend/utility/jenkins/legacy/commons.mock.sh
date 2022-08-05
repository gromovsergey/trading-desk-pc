#!/bin/bash

MOCK_LOG_NO=0
mock_clean() {
  echo ": $FUNCNAME"
  local mock_config_dir="$1"
  local mock_config="$2"
  trap - PIPE INT TERM EXIT
  ((MOCK_LOG_NO+=1))
  mock -v --configdir=$mock_config_dir -r $mock_config --scrub=chroot &>$WORKING_DIR/mock_log_${MOCK_LOG_NO}.log &
  if ! wait $!; then
    cat $WORKING_DIR/mock_log_${MOCK_LOG_NO}.log
    return 1
  fi
  return 0
}

mock_create() {
  echo ": $FUNCNAME"
  local mock_config_dir="$1"
  local mock_config="$2"

  trap "mock_clean $mock_config_dir $mock_config" INT TERM PIPE EXIT
  mock_clean $mock_config_dir $mock_config || return 1

  ((MOCK_LOG_NO+=1))
  mock -v --configdir=$mock_config_dir -r $mock_config --init &>$WORKING_DIR/mock_log_${MOCK_LOG_NO}.log &
  if ! wait $!; then
    cat $WORKING_DIR/mock_log_${MOCK_LOG_NO}.log
    mock_clean $mock_config_dir $mock_config
    return 1
  fi
  return 0
}


mock_copyin() {
  echo ": $FUNCNAME"
  local mock_config_dir="$1"
  local mock_config="$2"
  local src="$3"
  local dst="$4"

  ((MOCK_LOG_NO+=1))
  mock --configdir=$mock_config_dir -r $mock_config --copyin $src $dst &>$WORKING_DIR/mock_log_${MOCK_LOG_NO}.log &
  if ! wait $!; then
    cat $WORKING_DIR/mock_log_${MOCK_LOG_NO}.log
    mock_clean $mock_config_dir $mock_config
    return 1
  fi
  return 0
}

mock_copyout() {
  echo ": $FUNCNAME"
  local mock_config_dir="$1"
  local mock_config="$2"
  local src="$3"
  local dst="$4"

  ((MOCK_LOG_NO+=1))
  mock --configdir=$mock_config_dir -r $mock_config --copyout $src $dst &>$WORKING_DIR/mock_log_${MOCK_LOG_NO}.log &
  if ! wait $!; then
    cat $WORKING_DIR/mock_log_${MOCK_LOG_NO}.log
    mock_clean $mock_config_dir $mock_config
    return 1
  fi
  return 0
}

mock_run() {
  echo ": $FUNCNAME"
  local mock_config_dir="$1"
  local mock_config="$2"
  shift; shift
  local cmd="$@"

  ((MOCK_LOG_NO+=1))
  mock --configdir=$mock_config_dir -r $mock_config --chroot "$cmd" &>$WORKING_DIR/mock_log_${MOCK_LOG_NO}.log &
  if ! wait $!; then
    cat $WORKING_DIR/mock_log_${MOCK_LOG_NO}.log
    mock_clean $mock_config_dir $mock_config
    return 1
  fi
  return 0
}
