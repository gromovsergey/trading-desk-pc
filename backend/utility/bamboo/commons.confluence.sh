#!/bin/bash

. commons.util.sh
. commons.svn.sh

confluence_client() {
  local username="$1"
  local password="$2"
  shift; shift;

  [ -z "$username" ] && { echo ": $FUNCNAME: Undefined username"; exit 1; }
  [ -z "$password" ] && { echo ": $FUNCNAME: Undefined password"; exit 1; }

  local server="https://confluence.ocslab.com"
  wget_to_store confluence-cli-3.8.0 "http://maven.ocslab.com/nexus/service/local/repositories/thirdparty/content/confluence/cli-jars/3.8.0/cli-jars-3.8.0-jars.jar" archive.zip true

  java -jar "$STORE_PATH/confluence-cli-3.8.0.jar" --server "$server" \
    --user "$username" --password "$password" "$@"
  [ "$?" != "0" ] && exit 1

  return 0
}

confluence_get_children() {
  local username="$1"
  local password="$2"
  local space="$3"
  local title="$4"
  shift; shift; shift; shift;

  [ -z "$username" ] && { echo ": $FUNCNAME: Undefined username"; exit 1; }
  [ -z "$password" ] && { echo ": $FUNCNAME: Undefined password"; exit 1; }
  [ -z "$space" ] && { echo ": $FUNCNAME: Undefined space"; exit 1; }
  [ -z "$title" ] && { echo ": $FUNCNAME: Undefined title"; exit 1; }

  confluence_client  "$username" "$password" -a "getPageList" --space "$space" \
    --title "$title" --children $@
}

confluence_get_page() {
  local username="$1"
  local password="$2"
  local space="$3"
  local title="$4"
  shift; shift; shift; shift;

  [ -z "$username" ] && { echo ": $FUNCNAME: Undefined username"; exit 1; }
  [ -z "$password" ] && { echo ": $FUNCNAME: Undefined password"; exit 1; }
  [ -z "$space" ] && { echo ": $FUNCNAME: Undefined space"; exit 1; }
  [ -z "$title" ] && { echo ": $FUNCNAME: Undefined title"; exit 1; }

  confluence_client  "$username" "$password" -a "getPageSource" --space "$space" \
    --title "$title" $@
}

confluence_get_history_list() {
  local username="$1"
  local password="$2"
  local space="$3"
  local title="$4"
  shift; shift; shift; shift;

  [ -z "$username" ] && { echo ": $FUNCNAME: Undefined username"; exit 1; }
  [ -z "$password" ] && { echo ": $FUNCNAME: Undefined password"; exit 1; }
  [ -z "$space" ] && { echo ": $FUNCNAME: Undefined space"; exit 1; }
  [ -z "$title" ] && { echo ": $FUNCNAME: Undefined title"; exit 1; }

  confluence_client  "$username" "$password" -a "getPageHistoryList" --space "$space" \
    --title "$title" $@
}

confluence_make_url() {
  local space="$1"
  local title="$2"
  local server="$3"

  [ -z "$space" ] && { echo ": $FUNCNAME: Undefined space"; exit 1; }
  [ -z "$title" ] && { echo ": $FUNCNAME: Undefined title"; exit 1; }
  [ -z "$server" ] && server="https://confluence.ocslab.com"

  CONFLUENCE_URL="`echo "${server}/display/${space}/${title}" | tr ' ' '\+'`"
}

confluence_store_page() {
  local username="$1"
  local password="$2"
  local space="$3"
  local title="$4"
  local parent="$5"
  shift; shift; shift; shift; shift;

  [ -z "$username" ] && { echo ": $FUNCNAME: Undefined username"; exit 1; }
  [ -z "$password" ] && { echo ": $FUNCNAME: Undefined password"; exit 1; }
  [ -z "$space" ] && { echo ": $FUNCNAME: Undefined space"; exit 1; }
  [ -z "$title" ] && { echo ": $FUNCNAME: Undefined title"; exit 1; }
  [ -z "$parent" ] && { echo ": $FUNCNAME: Undefined parent"; exit 1; }

  confluence_client  "$username" "$password" -a "storePage" --space "$space" \
    --title "$title" --parent "$parent" "$@"
}

confluence_rename_page() {
  local username="$1"
  local password="$2"
  local space="$3"
  local title="$4"
  local new_title="$5"

  [ -z "$username" ] && { echo ": $FUNCNAME: Undefined username"; exit 1; }
  [ -z "$password" ] && { echo ": $FUNCNAME: Undefined password"; exit 1; }
  [ -z "$space" ] && { echo ": $FUNCNAME: Undefined space"; exit 1; }
  [ -z "$title" ] && { echo ": $FUNCNAME: Undefined title"; exit 1; }
  [ -z "$new_title" ] && { echo ": $FUNCNAME: Undefined new_title"; exit 1; }

  confluence_client  "$username" "$password" -a "renamePage" --space "$space" \
    --title "$title" --newTitle "$new_title"
}
