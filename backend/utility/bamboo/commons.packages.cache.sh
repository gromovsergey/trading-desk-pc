#!/bin/bash

cache_save_package_version() {
  local package="$1"; shift
  local version="$1"; shift
  local branches="$@"
  local cache_file="/tmp/OUI-COMMON/packages.cache"

  [ -z "$package" ] && { echo ": $FUNCNAME: undefined package " >&2; exit 1; }
  [ -z "$version" ] && { echo ": $FUNCNAME: undefined version " >&2; exit 1; }
  [ -z "$branches" ] && { echo ": $FUNCNAME: undefined branches " >&2; exit 1; }

  local entry="$package $version"
  local branch
  for branch in $branches; do
    if [ "${branch%/*}" = "fake" ]; then
      entry="$entry $branch"
    else
      get_last_changed_revision $branch
      entry="$entry $branch:$SVN_LAST_CHANGED_REV"
    fi
  done

  echo ": $FUNCNAME: saving record for ${package}-${version}: "
  echo ": $FUNCNAME:   '$entry'"
  echo "$entry" >> $cache_file
}

cache_get_package_version() {
  local package="$1"; shift
  local branches="$@"
  local cache_file="/tmp/OUI-COMMON/packages.cache"

  [ -z "$package" ] && { echo ": $FUNCNAME: undefined package " >&2; exit 1; }
  [ -z "$branches" ] && { echo ": $FUNCNAME: undefined branches " >&2; exit 1; }

  echo ": $FUNCNAME: $package ($branches)" >&2

  local entry=""
  local branch
  for branch in $branches; do
    if [ "${branch%/*}" = "fake" ]; then
      entry="$entry $branch"
    else
      get_last_changed_revision $branch
      entry="$entry $branch:$SVN_LAST_CHANGED_REV"
    fi
  done

  local version=""
  version=`grep -E "^$package [0-9\.]+$entry\$" $cache_file | tail -n 1 | cut -d " " -f 2`
  [ -z "$version" ] || echo ": $FUNCNAME:   found: $version" >&2
  echo $version
}
