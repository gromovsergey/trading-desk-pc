#!/bin/bash

. commons.util.sh

# Comments
# - Customize for your installation, for instance you might want to add default parameters like the following:
# java -jar `dirname $0`/lib/jira-cli-3.7.0.jar --server http://my-server --user automation --password automation "$@"

JIRA_BASE_URL="https://jira.ocslab.com"

create_jira_issue() {
  local project="$1" # "ENVDEV", "OUI" etc
  local type="$2"    # 1 - major
  local summary="$3"
  local component="$4"
  shift; shift; shift; shift;
  local description="$@"

  [ -z "$project" ] && { echo "Undefined project" ; exit 1 ; }
  [ -z "$type" ] && { echo "Undefined type" ; exit 1 ; }
  [ -z "$summary" ] && { echo "Undefined summary" ; exit 1 ; }
  [ -z "$component" ] && { echo "Undefined component" ; exit 1 ; }
  [ ! -z "$description" ] && local description_arg="--description"

  if [ "$component" != "-" ]; then
    component="--components $component"
  else
    component=""
  fi

  local output=$(jira --action createIssue --project "$project" --type "$type" --summary "$summary" $component --assignee "$LDAP_USER" $description_arg "$description" 2>&1)
  local result=$?
  if [ "$result" = "0" ]; then
    CREATED_JIRA_ISSUE=`echo "$output" | sed -n 's|^Issue \([A-Z0-9\-]\+\) .*$|\1|p'`
  else
    echo $output
    return $result
  fi
  if [ -z $CREATED_JIRA_ISSUE ]; then
    echo "$output"
    return 1
  fi
  echo $output

  jira --action progressIssue --issue $CREATED_JIRA_ISSUE --step "4"
  result=$?
  [ "$result" != "0" ] && return $result

  jira --action updateIssue --issue $CREATED_JIRA_ISSUE --assignee "$BAMBOO_USER"
  result=$?
  [ "$result" != "0" ] && return $result

  return $result
}

release_jira_version() {
  local project="$1"
  local old_version="$2"
  local new_version="$3"
  local new_major_version="$4"

  [ -z "$project" ] && { echo "Undefined project" ; exit 1 ; }
  [ -z "$old_version" ] && { echo "Undefined old_version" ; exit 1 ; }
  [ -z "$new_version" ] && { echo "Undefined new_version" ; exit 1 ; }

  echo "Add new version $new_version in Jira (first, check is it already exists)"
  jira --action getVersion --project "$project" --version "$new_version" 2> /dev/null
  if [ "$?" = "0" ] ; then
    echo "Yes, it is already exists"
    return 0
  else
    doc jira --action addVersion --project "$project" --version "$new_version" --after "$old_version"
  fi

  if [ ! -z "$new_major_version" ]; then
    echo "Add new major version $new_major_version in Jira (first, check is it already exists)"
    jira --action getVersion --project "$project" --version "$new_major_version" 2> /dev/null
    if [ "$?" = "0" ] ; then
      echo "Yes, it is already exists"
      return 0
    else
      doc jira --action addVersion --project "$project" --version "$new_major_version"
    fi
  fi

  echo "Change fix versions for open issue(s)"
  local open_issues=`jira --action getIssueList --outputFormat "101" --search "project = $project AND resolution = Unresolved AND fixVersion = '$old_version'"`
  [[ "$?" != "0" ]] && return 1
  #echo "output: $open_issues"

  issues_number=`echo "$open_issues" | head -n 1`
  open_issues=`echo "$open_issues" | tail -n +2`

  echo "issues number: $issues_number"
  echo "open issues: $open_issues"

  local number=0
  local issue
  for issue in $open_issues ; do
    (( number+= 1 ))
    echo "$number .. $issues_number: "

    local data="`doc jira --action getIssue --issue "$issue"`"
    [[ "$?" != "0" ]] && return 1
    local fvs=`echo "$data" | grep 'Fix versions  . . . . . . . . :'`
    fvs=${fvs:31}
    local priority=`echo "$data" | grep 'Priority  . . . . . . . . . . :' | sed -n -e 's|^.*\([0-9]\+\).*$|\1|p'`

    local n=0
    local ver
    local fix_versions=
    for ver in $fvs ; do
      if (( n % 2 == 0 )) ; then
        [[ -n "$fix_versions" ]] && fix_versions="$fix_versions,"
        if [ "$ver" = "'$old_version'" ] ; then
          if [ ! -z "$new_major_version" ]; then
            # check priority if it is major release
            # 1 - Blocker
            # 2 - Critical
            # 3 - Major
            # 4 - Minor
            # ...
            if [ "$priority" -lt 4 ]; then
              # it is 'red' issue
              fix_versions="$fix_versions$new_version"
            fi
          else
            # it is minor release
            fix_versions="$fix_versions$new_version"
          fi
        else
          ver=${ver#\'}
          ver=${ver%\'}
          fix_versions="$fix_versions$ver"
        fi
      fi
      (( n+= 1 ))
    done
    if [ ! -z "$new_major_version" ]; then
      # 'red' and 'green' issues should be fixed in new major version
      [[ -n "$fix_versions" ]] && fix_versions="$fix_versions,"
      fix_versions="$fix_versions$new_major_version"
    fi

    doc jira --action updateIssue --issue "$issue" --fixVersions "$fix_versions"
  done
  echo "Changed: $number issue(s)"

  echo "Release the old version $old_version in Jira (first, check is it already released)"
  local check_ver=`jira --action getVersion --project "$project" --version "$old_version" | grep 'Released  . . . . . . . . . . :'`
  if [ "${check_ver:32}" != "No" ] ; then
    echo "Yes, it is already released"
  else
    doc jira --action releaseVersion --project "$project" --version "$old_version"
  fi

  return 0
}

resolve_jira_issue() {
  local issue="$1"

  [ -z "$issue" ] && { echo "Undefined issue" ; exit 1 ; }

  jira --action progressIssue --issue $issue --step "5" --resolution "Fixed"
  return $?
}

get_jira_filter_url() {
  local project="$1"
  local version="$2"
  [ -z "$project" ] && { echo ": $FUNCNAME: undefined project "; exit 1; }
  [ -z "$version" ] && { echo ": $FUNCNAME: undefined version "; exit 1; }

  local query="project+%3D+${project}+AND+fixVersion+%3D+%22${version}%22+ORDER+BY+updated+DESC%2C+priority+DESC%2C+created+ASC"
  echo "$JIRA_BASE_URL/secure/IssueNavigator.jspa?reset=true&jqlQuery=$query&mode=hide"
}

jira() {
  # do not use 'doc' or 'docl' here as it will log the password
  # CAUTION: do not execute this on public hosts, as the password will be visible in 'ps' command
  # more at https://bobswift.atlassian.net/browse/CSOAP-153
  wget_to_store jira-cli-3.7.0 "http://maven.ocslab.com/nexus/service/local/repositories/thirdparty/content/jira/cli-jars/3.7.0/cli-jars-3.7.0-jars.jar" archive.zip true

  java -jar $STORE_PATH/jira-cli-3.7.0.jar --server $JIRA_BASE_URL --user "$LDAP_USER" --password "$LDAP_PASSWORD" "$@" 2>&1
  return $?
}
