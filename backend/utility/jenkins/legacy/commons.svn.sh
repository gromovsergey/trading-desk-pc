#!/bin/bash

is_branch() {
  local name=$1

  [ -z "$name" ] && { echo ": $FUNCNAME: Undefined name" ; exit 1 ; }
  [ "`echo $name | sed 's/\([0-9]\+\.[0-9]\+\.[0-9]\+\)/yes/'`" == "yes" ] && return 0
  return 1
}

get_next_branch() {
  local branch="$1" # 3.5.0

  [ -z "$branch" ] && { echo ": $FUNCNAME: Undefined branch" ; exit 1 ; }

  local major=`echo $branch | sed -n -e 's|[0-9]\+\.\([0-9]\+\)\.[0-9]\+|\1|p'`
  ((major+=1))
  echo "${branch%%.*}.$major.${branch##*.}" # 3.6.0
  return 0
}

is_tag() {
  local name=$1

  [ -z "$name" ] && { echo ": $FUNCNAME: Undefined name" ; exit 1 ; }
  [ "`echo $name | sed 's/\([0-9]\+\.[0-9]\+\.[0-9]\+\.[0-9]\)/yes/'`" == "yes" ] && return 0
  return 1
}

# Returns 0 if the given version corresponds to trunk
is_trunk() {
  local project="$1" # "ui", "pgdb" etc
  local version="$2" # "3.7.0"

  [ -z "$project" ] && { echo ": $FUNCNAME: Undefined project" ; exit 1 ; }
  [ -z "$version" ] && { echo ": $FUNCNAME: Undefined version" ; exit 1 ; }
  
  get_latests_branch 1 "svn+ssh://svn/home/svnroot/oix/$project/branches"
  trunk_branch=`get_next_branch $LATESTS_BRANCH` \
    || { echo ": Can't get the next branch for $LATESTS_BRANCH" ; exit 1 ; }

  [ "$version" = "$trunk_branch" ] && return 0
  return 1
} 

# Returns 0 if the given branch is greater then or equal to the version
# Examples
#   "ui" "trunk" "trunk" - return 0
#   "ui" "trunk" "3.5.0" - return 0 (for both cases, when 3.5.0 is the trunk and when trunk > 3.5.0)
#   "ui" "branches/3.5.0" "3.5.0" - return 0
#   "ui" "branches/dev/OUI_MADE_FROM_3.5.5" "3.5.0" - return 0
#   "ui" "branches/3.4.0" "3.5.0" - return 1
is_svn_branch_ge() {
  local project="$1"
  local branch="$2"
  local version="$3"

  [ -z "$project" ] &&{ echo ": $FUNCNAME: Undefined project" ; exit 1 ; }
  [ -z "$branch" ] &&{ echo ": $FUNCNAME: Undefined branch" ; exit 1 ; }
  [ -z "$version" ] &&{ echo ": $FUNCNAME: Undefined version" ; exit 1 ; }

  get_latests_branch 1 "svn+ssh://svn/home/svnroot/oix/$project/branches"
  local trunk_branch=`get_next_branch $LATESTS_BRANCH` \
    || { echo ": $FUNCNAME: can't get the next branch for $LATESTS_BRANCH" ; exit 1 ; }

  get_ancestor "svn+ssh://svn/home/svnroot/oix/$project/$branch"
  local branch_version=$trunk_branch
  [ "$SVN_ANCESTOR_NAME" != "trunk" ] && branch_version="$SVN_ANCESTOR_NAME" # 3.5.0

  echo $version | grep -q trunk
  [ "$?" = "0" ] && version=$trunk_branch

  # switch to numbers
  branch_version=${branch_version//./}
  version=${version//./}
  (( branch_version >= version )) && return 0

  return 1
}

# Returns
# SVN_PATH
#   ""        : SVN_PATH=
#   "trunk"   : SVN_PATH=trunk
#   "3.5.0"   : SVN_PATH=branches/3.5.0
#   "3.5.0.1" : SVN_PATH=tags/3.5.0.1
#   "other"   : SVN_PATH=branches/dev/other
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

# Checkout a file from SVN
# Returns the exported file in $CHECKOUT_FILE
checkout_file() {
  local svn_path=$1
  local out_file=$2

  [ -z "$svn_path" ] && { echo ": $FUNCNAME: Undefined svn_path" ; exit 1 ; }
  # when empty, then output file's name will be get from svn_path
  [ -z "$out_file" ] && out_file=`basename $svn_path`

  local out_dir=`dirname $out_file`
  if [ "$out_dir" = "." ] ; then
    out_dir=$WORKING_DIR/svn
    out_file=$out_dir/$out_file
  fi
  
  CHECKOUT_FILE=$out_file
  echo ": Checking out $svn_path to $CHECKOUT_FILE"
  [ -d "$out_dir" ] || mkdir -p "$out_dir"
  rm -rf $CHECKOUT_FILE

  svn export -q $svn_path $CHECKOUT_FILE 2>&1 || return 1
  return 0
}

# Checkout a folder from SVN
# Returns the checkouted folder in $CHECKOUT_FOLDER
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

# Checkout a folder from SVN
# Returns the checkouted folder in $CHECKOUT_FOLDER
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

# Returns latests (before trunk) BRANCH name
# First parameter can be 1, 2 ...
# Returns LATESTS_BRANCH
get_latests_branch() {
  local BRANCH_NO="$1"
  local SVN_PATH="$2" # svn+ssh://svn/home/svnroot/oix/ui/branches

  [ -z "$BRANCH_NO" ] && { echo ": $FUNCNAME: Undefined BRANCH_NO" ; exit 1 ; }
  [ -z "$SVN_PATH" ] && { echo ": $FUNCNAME: Undefined SVN_PATH" ; exit 1 ; }

  LATESTS_BRANCH=
  svn ls "$SVN_PATH" | sed -r -e '/^[0-9]+.[0-9]+.[^0]+/d' > $WORKING_DIR/branches.txt || return 1
  local BRANCH_NAME=`sort -t. -k 1,1nr -k 2,2nr -k 3,3nr $WORKING_DIR/branches.txt | head -n $BRANCH_NO | tail -n 1`

  LATESTS_BRANCH="${BRANCH_NAME%/}"
  return 0
}

# Returns latests TAG name for the given branch
# Returns LATESTS_TAG
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

# Print all tags from $repository
get_tags() {
  local repository=$1
  [ -z $repository ] && { echo "Undefined repository"; exit -1; }

  svn ls $repository/tags | \
    grep -E '([0-9]+\.[0-9]+\.[0-9]\.+[0-9]+/)' | \
    sort -t. -k 1,1n -k 2,2n -k 3,3n -k 4,4n | \
    sed 's|/||' || { echo "$FUNCNAME: could not get tags from $repository"; exit -1; }
}


# Returns path to ancestor of svn_path
# Returns
#   SVN_ANCESTOR, full path like svn+ssh://svn/home/svnroot/oix/ui/3.5.0
#   SVN_ANCESTOR_NAME, like '3.5.0' or 'trunk'
#   SVN_ANCESTOR_BRANCH_NAME, like 'branches/3.5.0' or 'trunk'
get_ancestor() {
  local svn_path="$1" # svn+ssh://svn/home/svnroot/oix/ui/branches/dev/OUI-NNN
  [ -z $svn_path ] && { echo "Undefined svn_path"; exit -1; }

  if echo $svn_path | grep -E "branches/[0-9]+\.[0-9]+\.0|trunk" >/dev/null; then
    SVN_ANCESTOR=$svn_path
  else
    SVN_ANCESTOR=""
    local svn_root="svn+ssh://svn/home/svnroot"
    while ! echo $SVN_ANCESTOR | grep -E "branches/[0-9]+\.[0-9]+\.0|trunk" >/dev/null; do
      local branch_name=`echo $svn_path | sed -n -e "s|^.*/\(.*\$\)|\1|p"`
      SVN_ANCESTOR=`svn log -v --stop-on-copy $svn_path | sed -n -e "s|^[\t ]*A.*/$branch_name .*from \(.*\):.*\$|\1|p"`
      if [ -z "$SVN_ANCESTOR" ]; then
        return 1
      fi
      local svn_path=$svn_root$SVN_ANCESTOR
    done
    SVN_ANCESTOR=$svn_root$SVN_ANCESTOR
  fi

  SVN_ANCESTOR_NAME=`echo $SVN_ANCESTOR | sed -n -e "s|^.*/\(.*\$\)|\1|p"`
  SVN_ANCESTOR_BRANCH_NAME=$SVN_ANCESTOR_NAME
  [ "$SVN_ANCESTOR_NAME" != "trunk" ] && SVN_ANCESTOR_BRANCH_NAME=branches/$SVN_ANCESTOR_NAME
  return 0
}

# Returns last changed rev of svn_path
# Returns SVN_LAST_CHANGED_REV
get_last_changed_revision() {
  local path="$1"
  local root="svn+ssh://svn/home/svnroot"

  SVN_LAST_CHANGED_REV=$(svn info ${root}/${path} | sed -n -e 's|^Last Changed Rev: \([0-9]\+\)|\1|p')
}

