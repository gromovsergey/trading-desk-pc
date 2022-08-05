#!/bin/bash

repo_parse_args_get_repo_name() {
  local repo=""
  local OPTIND opt
  while getopts ":r:" opt; do
    case "${opt}" in
        r)  repo="${OPTARG}"
            ;;
        *)
            ;;
    esac
  done
  shift $((OPTIND-1))
  echo $repo
}

repo_parse_args_get_packages() {
  local packages=""
  local OPTIND opt
  while getopts ":r:" opt; do
    case "${opt}" in
        r)  ;;
        *)  break
            ;;
    esac
  done
  shift $((OPTIND-1))
  echo $@
}

repo_get_packages() {
  local repo=`repo_parse_args_get_repo_name $@`
  local packages=`repo_parse_args_get_packages $@`
  local host="repo"

  [ -z "$repo" ] && repo="any"
  [ -z "$packages" ] && { echo ": $FUNCNAME: undefined packages " >&2; exit 1; }

  local package=""
  local unknown_packages=""
  local found_packages=""
  local filename=""
  local basename=""
  local found=""

  local cmd="find"
  local suffix=""
  if [ "$repo" != "local" ]; then
    cmd="ssh -o BatchMode=yes $host -- find"
    suffix="$host:/"
  fi
  for package in $packages; do
    if [ -r "$package" ]; then #  local package
      found_packages="$found_packages $package"
      continue
    fi
    if [ "${package%%://*}" = "$host" ]; then #  remote package
      if ssh -o BatchMode=yes $host -- test -r ${package##*:/}; then
        found_packages="$found_packages $package"
        continue
      fi
    fi
    unknown_packages="$unknown_packages $package"
  done
  for filename in $found_packages; do
    # get name without version, release etc
    basename=`basename $filename | sed -e 's|^\(.*\)-[0-9]\+\.[0-9]\+\.[0-9]\+\.[0-9]\+.*\.rpm|\1|'`
    if echo $found | grep -qv ":${basename}:"; then
      echo ": $FUNCNAME:    $filename" >&2
      echo "${filename}"
      found="$found :${basename}:"
    fi
  done
  if [ -z "$unknown_packages" ]; then
    return 0
  fi
  echo ": $FUNCNAME: finding packages $unknown_packages in $repo repo" >&2
  local pattern=""
  case $repo in
    local)
      pattern=`repo_get_search_pattern "/tmp/OUI-COMMON/repo" $unknown_packages`
      ;;
    test)
      pattern=`repo_get_search_pattern "/u01/ks/repos/moscow-test" $unknown_packages`
      ;;
    stage)
      pattern=`repo_get_search_pattern "/u01/ks/repos/moscow-stage" $unknown_packages`
      ;;
    eme*)
      pattern=`repo_get_search_pattern "/u01/ks/repos/moscow-emergency" $unknown_packages`
      ;;
    devel):
      pattern=`repo_get_search_pattern "/u01/ks/repos/moscow-devel" $unknown_packages`
      ;;
    any)
      for repo in "local" "test" "stage" "eme" "devel"; do
        repo_get_packages -r $repo $unknown_packages && return 0
      done
      return 1
      ;;
    *)
      echo ": $FUNCNAME: unknown repo $repo"
      exit 1
      ;;
  esac
  local output=""
  echo ": $FUNCNAME: running: $cmd $pattern" >&2
  output=`eval "$cmd $pattern" | sort`
  [ "$?" != "0" ] && exit 1
  [ -z "$output" ] && { echo ": $FUNCNAME: no package found" >&2; return 0; }
  echo ": $FUNCNAME: found" >&2
  for filename in $output; do
    # get name without version, release etc
    basename=`basename $filename | sed -e 's|^\(.*\)-[0-9]\+\.[0-9]\+\.[0-9]\+\.[0-9]\+.*\.rpm|\1|'`
    if echo $found | grep -qv ":${basename}:"; then
      echo ": $FUNCNAME:    $filename" >&2
      echo "${suffix}${filename}"
      found="$found :${basename}:"
    fi
  done
}

repo_collect_packages() {
  local destination="$1"; shift
  local repo=`repo_parse_args_get_repo_name $@`
  local packages=`repo_parse_args_get_packages $@`

  [ -z "$destination" ] && { echo ": $FUNCNAME: undefined destination "; exit 1; }
  [ -z "$repo" ] && repo="any"
  [ -z "$packages" ] && { echo ": $FUNCNAME: undefined packages "; exit 1; }

  echo ": $FUNCNAME: collecting packages $packages from $repo repo to $destination"

  local output=""
  output=`repo_get_packages $@`
  [ "$?" != "0" ] && exit 1
  [ -z "$output" ] && { echo ": $FUNCNAME: no package found"; return 1; }

  rm -rf $destination
  mkdir -p $destination

  for package in $output; do
    basename=`basename $package`
    if [ ! -e "$destination/$basename" ]; then
      echo ": $FUNCNAME:   $package"
      scp  $package $destination || exit 1
    fi
  done
  echo ": $FUNCNAME: done"
  return 0
}

repo_get_search_pattern() {
  local folder="$1"; shift
  local packages="$@"

  [ -z "$folder" ] && { echo ": $FUNCNAME: undefined folder "; exit 1; }
  [ -z "$packages" ] && { echo ": $FUNCNAME: undefined packages "; exit 1; }

  local pattern="-L $folder"
  local package=""
  for package in $packages; do
    pattern="$pattern -name '$package' -o "
  done
  pattern="${pattern%*-o*}"
  echo "$pattern"
}

repo_clean_local_repo() {
  local days="$1"
  [ -z "$days" ] && days=2
  local folder="/tmp/OUI-COMMON/repo"

  echo ": $FUNCNAME: cleaning local repo"
  local file
  for file in `find -L $folder -name '*.rpm' -mtime +$days`; do
    doc rm -f $file
  done
  echo ": $FUNCNAME: current size"
  du -h $folder
}

repo_is_package_in_repo() {
  local repo=`repo_parse_args_get_repo_name $@`
  local packages=`repo_parse_args_get_packages $@`

  [ -z "$repo" ] && repo="any"
  [ -z "$packages" ] && { echo ": $FUNCNAME: undefined packages " >&2; exit 1; }

  repo_get_packages -r $repo $packages
  return $?
}

repo_get_repo_by_colo() {
  local colo="$1"
  [ -z "$colo" ] && { echo ": $FUNCNAME: undefined colo "; exit 1; }

  case $colo in
    moscow-test-central) echo test;;
    moscow-stage-central) echo stage;;
    moscow-emergency-central) echo "eme";;
    *) echo ": $FUNCNAME: unknown colo $colo" >&2; exit 1;;
  esac
}
