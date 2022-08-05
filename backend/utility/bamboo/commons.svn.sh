#!/bin/bash

is_branch() {
  local name=$1

  [ -z "$name" ] && { echo ": $FUNCNAME: Undefined name" ; exit 1 ; }
  [ "`echo $name | sed 's/\([0-9]\+\.[0-9]\+\.[0-9]\+\)/yes/'`" == "yes" ] && return 0
  return 1
}

is_tag() {
  local name=$1

  [ -z "$name" ] && { echo ": $FUNCNAME: Undefined name" ; exit 1 ; }
  [ "`echo $name | sed 's/\([0-9]\+\.[0-9]\+\.[0-9]\+\.[0-9]\)/yes/'`" == "yes" ] && return 0
  return 1
}

### Returns
###  SVN_PATH
get_svn_path() {
  local version=$1

  SVN_PATH=
  [ -z "$version" ] && return 0

  if [ "$version" = "trunk" ] ; then
    SVN_PATH="trunk"
  else
    if [ "`echo $version | sed 's/\([0-9]\+\.[0-9]\+\.[0-9]\+\)/yes/'`" == "yes" ]; then
      SVN_PATH="branches/$version"
    else
      if [ "`echo $version | sed 's/\([0-9]\+\.[0-9]\+\.[0-9]\+\.[0-9]\+\)/yes/'`" == "yes" ]; then
        SVN_PATH="tags/$version"
      else
        SVN_PATH="branches/dev/$version"
      fi
    fi
  fi
  return 0
}

is_svn_path_exists() {
  local svn_path="$1"

  [ -z "$svn_path" ] && { echo ": $FUNCNAME: Undefined svn_path" ; exit 1 ; }

  svn info $svn_path 2> /dev/null
  return $?
}

### Checkout a file from SVN
### Returns the checkouted file in $CHECKOUT_FILE
checkout_file() {
  local SVN_PATH=$1
  local OUT_FILE=$2

  [ -z "$SVN_PATH" ] && { echo ": $FUNCNAME: Undefined SVN_PATH" ; exit 1 ; }
  # when empty, then output file's name will be get from SVN_PATH
  [ -z "$OUT_FILE" ] && OUT_FILE=`basename $SVN_PATH`

  local TMP_DIR=$WORKING_DIR/svn
  CHECKOUT_FILE=$WORKING_DIR/svn/$OUT_FILE
  echo ": Checking out $SVN_PATH to $CHECKOUT_FILE"
  [ -d "$TMP_DIR" ] || mkdir -p "$TMP_DIR"
  rm -rf $CHECKOUT_FILE

  svn export -q $SVN_PATH $CHECKOUT_FILE 2>&1 || return 1
  return 0
}

### Checkout a folder from SVN
### Returns the checkouted folder in $CHECKOUT_FOLDER
svn_checkout_folder() {
  local SVN_PATH=$1
  local OUT_FOLDER=$2

  [ -z "$OUT_FOLDER" ] && OUT_FOLDER=`basename $SVN_PATH`

  svn_get_folder "checkout" $SVN_PATH $OUT_FOLDER
  return $?
}

svn_export_folder() {
  local SVN_PATH=$1
  local OUT_FOLDER=$2

  [ -z "$OUT_FOLDER" ] && OUT_FOLDER=`basename $SVN_PATH`

  svn_get_folder "export" $SVN_PATH $OUT_FOLDER
  return $?
}

### Checkout a folder from SVN
### Returns the checkouted folder in $CHECKOUT_FOLDER
svn_get_folder() {
  local COMMAND=$1
  local SVN_PATH=$2
  local OUT_FOLDER=$3

  [ -z "$COMMAND" ] && { echo ": $FUNCNAME: Undefined COMMAND" ; exit 1 ; }
  [ -z "$SVN_PATH" ] && { echo ": $FUNCNAME: Undefined SVN_PATH" ; exit 1 ; }
  [ -z "$OUT_FOLDER" ] && OUT_FOLDER=`basename $SVN_PATH`

  CHECKOUT_FOLDER=$WORKING_DIR/svn/$OUT_FOLDER
  echo ": Checking out $SVN_PATH to $CHECKOUT_FOLDER"
  rm -rf $CHECKOUT_FOLDER

  svn $COMMAND -q $SVN_PATH $CHECKOUT_FOLDER 2>&1 || return 1
  return 0
}

### Returns latests (before trunk) BRANCH name
### First parameter can be 1, 2 ...
### Returns LATESTS_BRANCH
get_latests_branch() {
  local BRANCH_NO="$1"
  local SVN_PATH="$2"

  [ -z "$BRANCH_NO" ] && { echo ": $FUNCNAME: Undefined BRANCH_NO" ; exit 1 ; }
  [ -z "$SVN_PATH" ] && { echo ": $FUNCNAME: Undefined SVN_PATH" ; exit 1 ; }

  LATESTS_BRANCH=
  svn ls "$SVN_PATH" | sed -r -e '/^[0-9]+.[0-9]+.[^0]+/d' > $WORKING_DIR/branches.txt || return 1
  local BRANCH_NAME=`sort -t. -k 1,1nr -k 2,2nr -k 3,3nr branches.txt | head -n $BRANCH_NO | tail -n 1`
  LATESTS_BRANCH="${BRANCH_NAME%/}"
  return 0
}

### Returns latests TAG name for the given branch
### Returns LATESTS_TAG
get_latests_tag() {
  local BRANCH_NAME="$1"
  local SVN_PATH="$2"

  [ -z "$BRANCH_NAME" ] && { echo ": $FUNCNAME: Undefined BRANCH_NAME" ; exit 1 ; }
  [ -z "$SVN_PATH" ] && { echo ": $FUNCNAME: Undefined SVN_PATH" ; exit 1 ; }

  local POINT_COUNT=`echo $BRANCH_NAME | grep -o "\." | wc -l | sed s/\ //g`

  # A branch, like '2.5.0'
  LATESTS_TAG=
  if [ "$POINT_COUNT" = "2" ] ; then
    svn ls "$SVN_PATH" | grep "$BRANCH_NAME" > $WORKING_DIR/tags.txt || return 1
    local TAG_NAME=`sort -t. -k 1,1nr -k 2,2nr -k 3,3nr -k 4,4nr tags.txt | head -n 1`
    LATESTS_TAG="${TAG_NAME%/}"
  fi
  return 0
}

## Print all tags from $repository
get_tags() {
  local repository=$1
  [ -z $repository ] && { echo "Undefined repository"; exit -1; }

  svn ls $repository/tags | \
    grep -E '([0-9]+\.[0-9]+\.[0-9]\.+[0-9]+/)' | \
    sort -t. -k 1,1n -k 2,2n -k 3,3n -k 4,4n | \
    sed 's|/||' || { echo "$FUNCNAME: could not get tags from $repository"; exit -1; }
}


### Returns path to ancestor of svn_path
### Returns SVN_ANCESTOR
get_ancestor() {
  local svn_path="$1"
  local svn_root="svn+ssh://svn/home/svnroot"

  if echo $svn_path | grep -E "branches/[0-9]+\.[0-9]+\.0|trunk" >/dev/null; then
    SVN_ANCESTOR=$svn_path
    return 0
  fi

  SVN_ANCESTOR=""
  while ! echo $SVN_ANCESTOR | grep -E "branches/[0-9]+\.[0-9]+\.0|trunk" >/dev/null; do
    local branch_name=`echo $svn_path | sed -n -e "s|^.*/\(.*\$\)|\1|p"`
    SVN_ANCESTOR=`svn log -v --stop-on-copy $svn_path | sed -n -e "s|^[\t ]*A.*/$branch_name .*from \(.*\):.*\$|\1|p"`
    if [ -z $SVN_ANCESTOR ]; then
      return 1
    fi
    local svn_path=$svn_root$SVN_ANCESTOR
  done
  SVN_ANCESTOR=$svn_root$SVN_ANCESTOR
  return 0
}

### Returns last changed rev of svn_path
### Returns SVN_LAST_CHANGED_REV
get_last_changed_revision() {
  local path="$1"
  local root="svn+ssh://svn/home/svnroot"

  SVN_LAST_CHANGED_REV=$(svn info ${root}/${path} | sed -n -e 's|^Last Changed Rev: \([0-9]\+\)|\1|p')
}

