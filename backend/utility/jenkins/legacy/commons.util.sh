#!/bin/bash

### Executes the script on the given host
### as an user
EXECUTE_REMOTE_COUNT=0
execute_remote_ex() {
  local USER=$1 ; shift
  local HOST=$1 ; shift
  local COPIED_FILES=$1 ; shift
  local ARGS="$@"

  # Prepare the script as a local file
  (( EXECUTE_REMOTE_COUNT += 1 ))
  local SCRIPT=$WORKING_DIR/execute-remote$EXECUTE_REMOTE_COUNT.sh
  rm -f $SCRIPT
  echo "#!/bin/bash" > $SCRIPT
  echo "# Args: $ARGS" >> $SCRIPT
  local ifs="$IFS"
  IFS=''
  while read data ; do
    echo "$data" >> $SCRIPT
  done
  IFS="$ifs"
  echo "exit 0" >> $SCRIPT
  chmod a+x $SCRIPT

  # Command to execute on the remote host
  local CMD=$SCRIPT
  if [ "$USER" != "`whoami`" ] ; then
    CMD="sudo -Hiu $USER $CMD"
  fi

  # Copy files to remote host
  local result=0
  if [ "$HOST" == `hostname` ] ; then
    echo ": $FUNCNAME: Executing $HOST:$CMD"
    $CMD $ARGS
    result="$?"
  else
    ssh $HOST "rm -f -r $WORKING_DIR && mkdir -p $WORKING_DIR && chmod a+rw $WORKING_DIR/"
    [ "$?" != "0" ] && { echo "Can't create $HOST:$WORKING_DIR" ; return 1 ; }

    local file
    for file in $COPIED_FILES; do
      scp -r -q $WORKING_DIR/$file $HOST:$WORKING_DIR || exit 1
    done
    scp -r -q $SCRIPT $HOST:$SCRIPT || exit 1

    echo ": $FUNCNAME: Executing $HOST:$CMD"
    ssh -A $HOST "cd $WORKING_DIR ; $CMD $ARGS"
    result="$?"
  fi

  echo ": $FUNCNAME: result=$result"
  return $result
}

execute_remote() {
  local HOST=$1 ; shift
  local ARGS="$@"

  execute_remote_ex `whoami` $HOST "*.sh" "$ARGS"
  return $?
}

execute_remote_as() {
  local USER=$1 ; shift
  local HOST=$1 ; shift
  local ARGS="$@"

  execute_remote_ex $USER $HOST "*.sh" "$ARGS"
  return $?
}

### Execute a command
docr() {
  local CMD="$@"

  echo ": doc `date`: $CMD"
  eval $CMD

  return $?
}

### Execute a command and do exit if it is failed
doc() {
  local CMD="$@"

  echo
  echo ": doc `date`: $CMD"
  eval $CMD
  local result="$?"

  [ "$result" != "0" ] && exit 1
  return 0
}

### Execute a command and do exit if it is failed
### All the output will be redirected to a file
### In case an error that file will be printed
DOCL_COUNT=0
docl() {
  (( DOCL_COUNT += 1 ))
  local LOG_FILE="$WORKING_DIR/docl$DOCL_COUNT.log"
  local CMD="$@ 2>&1 >>$LOG_FILE"

  echo ": docl `date`: $CMD" > $LOG_FILE
  eval $CMD
  local result="$?"
  echo ": docl `date`: result=$result" >> $LOG_FILE

  [ "$result" != "0" ] && { cat $LOG_FILE ; exit 1 ; }
  return 0
}

create_store() {
  local STORE_NAME=$1

  [ -z "$STORE_NAME" ] && { echo ": $FUNCNAME: Undefined STORE_NAME" ; exit 1 ; }

  rm -r -f /tmp/OUI-COMMON/$STORE_NAME || exit 1
  mkdir -p /tmp/OUI-COMMON/$STORE_NAME || exit 1
  return 0
}

put_to_store() {
  local STORE_NAME=$1
  local FILE_NAME=$2

  [ -z "$STORE_NAME" ] && { echo ": $FUNCNAME: Undefined STORE_NAME" ; exit 1 ; }
  [ -z "$FILE_NAME" ] && { echo ": $FUNCNAME: Undefined FILE_NAME" ; exit 1 ; }

  [ ! -d "/tmp/OUI-COMMON/$STORE_NAME" ] && mkdir -p "/tmp/OUI-COMMON/$STORE_NAME"
  cp -r $FILE_NAME /tmp/OUI-COMMON/$STORE_NAME/${FILE_NAME##*/} || exit 1
  return 0
}

# First, checks is the file in a store
# If no, then downloads it and puts to a store
# Returns STORE_PATH - a path to the store
wget_to_store() {
  local store_name=$1
  local http_url=$2
  local output_file_name=$3
  local do_unzip=$4

  [ -z "$store_name" ] && { echo ": $FUNCNAME: Undefined store_name" ; exit 1 ; }
  [ -z "$http_url" ] && { echo ": $FUNCNAME: Undefined http_url" ; exit 1 ; }
  [ -z "$output_file_name" ] && { echo ": $FUNCNAME: Undefined output_file_name" ; exit 1 ; }
  [ -z "$do_unzip" ] && { echo ": $FUNCNAME: Undefined do_unzip" ; exit 1 ; }

  STORE_PATH="/tmp/OUI-COMMON/$store_name"

  # already downloaded, do nothing
  [ -d "$STORE_PATH" ] && [ -f "$STORE_PATH/$output_file_name" ] && return 0

  docl mkdir -p "$STORE_PATH"
  docl wget "$http_url" -O "$STORE_PATH/$output_file_name"
  if [ "$do_unzip" = "true" ] ; then
    pushd "$STORE_PATH"
    docl unzip -uo "$output_file_name"
    popd
  fi

  return 0
}

get_from_store() {
  local STORE_NAME=$1
  local FILE_NAME=$2

  [ -z "$STORE_NAME" ] && { echo ": $FUNCNAME: Undefined STORE_NAME" ; exit 1 ; }
  [ -z "$FILE_NAME" ] && { echo ": $FUNCNAME: Undefined FILE_NAME" ; exit 1 ; }

  cp -r /tmp/OUI-COMMON/$STORE_NAME/${FILE_NAME##*/} $FILE_NAME || exit 1
  return 0
}

cat_from_store() {
  local STORE_NAME=$1
  local FILE_NAME=$2

  [ -z "$STORE_NAME" ] && { echo ": $FUNCNAME: Undefined STORE_NAME" ; exit 1 ; }
  [ -z "$FILE_NAME" ] && { echo ": $FUNCNAME: Undefined FILE_NAME" ; exit 1 ; }

  cat /tmp/OUI-COMMON/$STORE_NAME/${FILE_NAME##*/} || exit 1
  return 0
}

clean_store() {
  local STORE_NAME=$1
  [ -z "$STORE_NAME" ] && { echo ": $FUNCNAME: Undefined STORE_NAME" ; exit 1 ; }

  rm -r -f /tmp/OUI-COMMON/$STORE_NAME/* || exit 1

  return 0
}

get_centos_release() {
  local host="$1"
  [ -z $host ] && { echo ": $FUNCNAME: undefined host"; exit 1; }

  CENTOS_RELEASE=`ssh -o BatchMode=yes $host -- rpm -q --qf '%{release}\n' coreutils |\
                  sed -e 's|^.*\(el[0-9]\+\).*$|\1|'`
  if [ -z $CENTOS_RELEASE ] ; then
    # because of power outage in Moscow
    CENTOS_RELEASE="el7"
    echo ": $FUNCNAME: $host : could not get centos release, using default $CENTOS_RELEASE"
  fi
}

save_artifacts() {
  local subdir="$1"; shift
  local filenames="$@"

  [ -z "$subdir" ] && { echo ": $FUNCNAME: undefined subdir"; exit 1; }

  local wd=${bamboo_working_directory}
  echo ": $FUNCNAME: saving artifacts to $subdir"
  rm -rf $wd/$subdir && \
    mkdir -p $wd/$subdir && \
    cp -rv $filenames $wd/$subdir || \
    echo ": $FUNCNAME: could not save artifacts to $subdir"
}

print_globals() {
  for varname in $@; do
    [ ! -z "${!varname}" ] && echo ": $varname = ${!varname}"
  done
  echo
}

check_globals() {
  for varname in $@; do
    if [ ! -z "${!varname}" ]; then
      echo ": $varname = ${!varname}"
    else
      echo ": undefined global variable $varname"
      exit 1
    fi
  done
  echo
}

get_required_java_version() {
  local old_version="1.7.0.45"
  local package="$1"

  echo ": $FUNCNAME: $package" >&2

  [ -z "$package" ] && { echo "$old_version"; return 0; }
  [ ! -e "$package" ] && { echo "$old_version"; return 0; }

  local new_version
  new_version=`rpm -qpR $package | sed -n -e 's|^java-1.7.0-oracle = \(.:\)\?\(.*\)$|\2|gp'`
  echo "$new_version"

  return 0
}
