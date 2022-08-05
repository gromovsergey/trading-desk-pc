#!/bin/bash

CMS_BASE_URL="https://cms.ocslab.com:8181"

### Checkouts a Project from SVN
### Returns: PROJECT_FOLDER
checkout_project() {
  local svn_branch="$1"

  [ -z "$svn_branch" ] && { echo ": $FUNCNAME: Undefined svn_branch" ; exit 1 ; }

  local base=`basename $svn_branch`
  PROJECT_FOLDER=$WORKING_DIR/svn/$base
  echo ": Checking out $svn_branch to $PROJECT_FOLDER"
  rm -rf $PROJECT_FOLDER

  auth=
  protocol="svn+ssh://svn/home"
  [ "$LDAP_USER" = "oix.project.coordinator" ] && { auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive" ; protocol="http://svn.ocslab.com" ; }
  svn -q co $auth $protocol$svn_branch --depth immediates $PROJECT_FOLDER || return 1

  pushd $PROJECT_FOLDER
  svn -q up $auth --set-depth infinity cms-plugin || return 1
  svn -q up $auth --set-depth infinity utility || return 1
  if echo $svn_branch | grep -q '/oix/datastore/'; then
    svn -q up $auth --set-depth infinity oozie || return 1
    svn -q up $auth --set-depth infinity patches || return 1
  fi
  popd

  return 0
}

update_foros_ui_svn() {
  local project_folder="$1"
  local new_version="$2"

  [ -z "$project_folder" ] && { echo ": $FUNCNAME: Undefined project_folder" ; exit 1 ; }
  [ -z "$new_version" ] && { echo ": $FUNCNAME: Undefined new_version" ; exit 1 ; }

  local xml_file=$project_folder/cms-plugin/UIConfigDescriptor.xml
  [ ! -f $xml_file ] && { echo "File $xml_file does not exist"; return 1; }

  local svn_ver=`sed -r -e 's/[[:print:]]*?version[[:blank:]]*?=[[:blank:]]*?\"([[:digit:]]+\.[[:digit:]]+\.[[:digit:]]+\.[[:digit:]]+)\"[[:print:]]*|[[:print:]]*/\1/g' < $xml_file | sed '/^$/d'`
  sed "s/version=\"$svn_ver\"/version=\"$new_version\"/" -i $xml_file
  sed "s/version=\"trunk\"/version=\"$new_version\"/" -i $xml_file

  echo "$new_version" | cat > $project_folder/jspwiki.version
  if [ "${new_version##*.}" = "0" ]; then
    echo "$new_version" | cat > $project_folder/unixcommons.version
  fi

  pushd "$project_folder"
  svn diff

  local auth=
  [ "$LDAP_USER" = "oix.project.coordinator" ] && auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive"
  svn commit $auth -m "Project version updated" || return 1

  popd
  return 0
}

update_foros_pgdb_svn() {
  local project_folder="$1"
  local new_version="$2"

  [ -z "$project_folder" ] && { echo ": $FUNCNAME: Undefined project_folder" ; exit 1 ; }
  [ -z "$new_version" ] && { echo ": $FUNCNAME: Undefined new_version" ; exit 1 ; }

  local xml_file=$project_folder/cms-plugin/PGDBConfigDescriptor.xml
  [ ! -f $xml_file ] && { echo "File $xml_file does not exist"; return 1; }

  local svn_ver=`sed -r -e 's/[[:print:]]*?version[[:blank:]]*?=[[:blank:]]*?\"([[:digit:]]+\.[[:digit:]]+\.[[:digit:]]+\.[[:digit:]]+)\"[[:print:]]*|[[:print:]]*/\1/g' < $xml_file | sed '/^$/d'`
  sed "s/version=\"$svn_ver\"/version=\"$new_version\"/" -i $xml_file
  sed "s/version=\"trunk\"/version=\"$new_version\"/" -i $xml_file

  echo "$new_version" | cat > $project_folder/streams-replication.version
  if [ "${new_version##*.}" = "0" ]; then
    local branch=${new_version%.*}
    local major=`echo $branch | sed -n -e 's|[0-9]\+\.\([0-9]\+\)\.[0-9]\+|\1|p'`
    ((major+=1))
    local new_branch="${branch%%.*}.$major.${branch##*.}"
    echo $new_branch | cat > $project_folder/version.txt
  fi

  pushd "$project_folder"
  svn diff

  local auth=
  [ "$LDAP_USER" = "oix.project.coordinator" ] && auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive"
  svn commit $auth -m "Project version updated" || return 1

  popd
  return 0
}

update_foros_bi_svn() {
  local project_folder="$1"
  local new_version="$2"

  [ -z "$project_folder" ] && { echo ": $FUNCNAME: Undefined project_folder" ; exit 1 ; }
  [ -z "$new_version" ] && { echo ": $FUNCNAME: Undefined new_version" ; exit 1 ; }

  if [ "${new_version##*.}" = "0" ]; then
    local branch=${new_version%.*}
    local major=`echo $branch | sed -n -e 's|[0-9]\+\.\([0-9]\+\)\.[0-9]\+|\1|p'`
    ((major+=1))
    local new_branch="${branch%%.*}.$major.${branch##*.}"
    echo $new_branch | cat > $project_folder/version.txt
  fi

  pushd "$project_folder"
  svn diff

  local auth=
  [ "$LDAP_USER" = "oix.project.coordinator" ] && auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive"
  svn commit $auth -m "Project version updated" || return 1

  popd
  return 0
}

update_foros_repl_svn() {
  local project_folder="$1"
  local new_version="$2"

  [ -z "$project_folder" ] && { echo ": $FUNCNAME: Undefined project_folder" ; exit 1 ; }
  [ -z "$new_version" ] && { echo ": $FUNCNAME: Undefined new_version" ; exit 1 ; }

  if [ "${new_version##*.}" = "0" ]; then
    local branch=${new_version%.*}
    local major=`echo $branch | sed -n -e 's|[0-9]\+\.\([0-9]\+\)\.[0-9]\+|\1|p'`
    ((major+=1))
    local new_branch="${branch%%.*}.$major.${branch##*.}"
    echo $new_branch | cat > $project_folder/version.txt
  fi

  pushd "$project_folder"
  svn diff

  local auth=
  [ "$LDAP_USER" = "oix.project.coordinator" ] && auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive"
  svn commit $auth -m "Project version updated" || return 1

  popd
  return 0
}

update_foros_pgadm_svn() {
  local project_folder="$1"
  local new_version="$2"

  [ -z "$project_folder" ] && { echo ": $FUNCNAME: Undefined project_folder" ; exit 1 ; }
  [ -z "$new_version" ] && { echo ": $FUNCNAME: Undefined new_version" ; exit 1 ; }

  if [ "$PGADM_BRANCH" = "3.3.0" ]; then
    local xml_file=$project_folder/cms-plugin/PostgresDbConfigDescriptor.xml
  else
    local xml_file=$project_folder/cms-plugin/PGADMConfigDescriptor.xml
  fi

  [ ! -f $xml_file ] && { echo "File $xml_file does not exist"; return 1; }

  local svn_ver=`sed -r -e 's/[[:print:]]*?version[[:blank:]]*?=[[:blank:]]*?\"([[:digit:]]+\.[[:digit:]]+\.[[:digit:]]+\.[[:digit:]]+)\"[[:print:]]*|[[:print:]]*/\1/g' < $xml_file | sed '/^$/d'`
  sed "s/version=\"$svn_ver\"/version=\"$new_version\"/" -i $xml_file
  sed "s/version=\"trunk\"/version=\"$new_version\"/" -i $xml_file

  pushd "$project_folder"
  svn diff

  local auth=
  [ "$LDAP_USER" = "oix.project.coordinator" ] && auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive"
  svn commit $auth -m "Project version updated" || return 1

  popd
  return 0
}

update_foros_dstr_svn() {
  local project_folder="$1"
  local new_version="$2"

  [ -z "$project_folder" ] && { echo ": $FUNCNAME: Undefined project_folder" ; exit 1 ; }
  [ -z "$new_version" ] && { echo ": $FUNCNAME: Undefined new_version" ; exit 1 ; }

  if [ "$DSTR_BRANCH" = "3.3.0" ]; then
    local xml_file=$project_folder/cms-plugin/DSConfigDescriptor.xml
  else
    local xml_file=$project_folder/cms-plugin/DSTRConfigDescriptor.xml
  fi

  [ ! -f $xml_file ] && { echo "File $xml_file does not exist"; return 1; }

  local svn_ver=`sed -r -e 's/[[:print:]]*?version[[:blank:]]*?=[[:blank:]]*?\"([[:digit:]]+\.[[:digit:]]+\.[[:digit:]]+\.[[:digit:]]+)\"[[:print:]]*|[[:print:]]*/\1/g' < $xml_file | sed '/^$/d'`
  sed "s/version=\"$svn_ver\"/version=\"$new_version\"/" -i $xml_file
  sed "s/version=\"trunk\"/version=\"$new_version\"/" -i $xml_file

  pushd "$project_folder"
  svn diff

  local auth=
  [ "$LDAP_USER" = "oix.project.coordinator" ] && auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive"
  svn commit $auth -m "Project version updated" || return 1

  popd
  return 0
}

upload_cms_plugin() {
  local path=$1

  [ -z "$path" ] && { echo ": $FUNCNAME: Undefined path" ; exit 1 ; }

  local ts=`date +%N`
  CMS_PLUGIN_FILE="/tmp/cms-plugin-"$ts".zip"
  cd "$path"
  ./create_plugin.sh $CMS_PLUGIN_FILE

  # CMS always return 0
  local res=`curl -k -u $LDAP_USER:$LDAP_PASSWORD -F file=@$CMS_PLUGIN_FILE $CMS_BASE_URL/services/uploadPlugin`
  echo "$res"

  echo "$res" | grep -i -q success
  return $?
}

get_app_version() {
  local app=$1
  local colo=$2

  [ -z "$app" ] && { echo "Undefined app" ; exit 1 ; }
  [ -z "$colo" ] && { echo "Undefined colo" ; exit 1 ; }

  curl -k -u $LDAP_USER:$LDAP_PASSWORD --get -d "colo=$colo" $CMS_BASE_URL/services/colocation.xml >/tmp/colo.xml 2>/dev/null || return 1
  APP_NAME=`grep "$app-" /tmp/colo.xml | cut -d '"' -f 2`
  APP_VERSION=${APP_NAME##$app-}

  echo "Current App: $APP_NAME [$APP_VERSION]"
  [ -z "$APP_NAME" ] && return 1
  return 0
}

update_app_version() {
  local app_name=$1
  local new_version=$2
  local colo=$3

  [ -z "$app_name" ] && { echo "Undefined app_name" ; exit 1 ; }
  [ -z "$new_version" ] && { echo "Undefined new_version" ; exit 1 ; }
  [ -z "$colo" ] && { echo "Undefined colo" ; exit 1 ; }

  curl -k -u $LDAP_USER:$LDAP_PASSWORD --get -d "colo=$colo&plugin=$app_name&newVersion=$new_version" $CMS_BASE_URL/services/updateVersion 1>/tmp/cms.log 2>/dev/null
  local status=`grep "<status>" /tmp/cms.log | cut -d '>' -f 2 | cut -d '<' -f 1`
  if [ "$status" != "SUCCEEDED" ] ; then cat /tmp/cms.log ; return 1; fi

  echo "$app_name CMS version update $status"
  return 0
}

build_and_deploy_config() {
  local app_name=$1
  local version=$2
  local colo=$3

  [ -z "$app_name" ] && { echo "Undefined app_name" ; exit 1 ; }
  [ -z "$version" ] && { echo "Undefined version" ; exit 1 ; }
  [ -z "$colo" ] && { echo "Undefined colo" ; exit 1 ; }

  curl -k -u $LDAP_USER:$LDAP_PASSWORD --get -d "colo=$colo&plugin=$app_name-$version" $CMS_BASE_URL/services/buildDeployRpms 1>/tmp/cms.log 2>/dev/null
  local status=`grep "<status>" /tmp/cms.log | cut -d '>' -f 2 | cut -d '<' -f 1`
  if [ "$status" != "SUCCEEDED" ] ; then cat /tmp/cms.log ; return 1; fi
  local rpms=`grep "RPMs:" /tmp/cms.log`
  CMS_RELEASE=`cat /tmp/cms.log  | sed -n 's|^.*RPMs:.*\(cms[0-9]\+\.noarch\)\.rpm.*$|\1|p'`
  echo "Builded RPMs: $rpms"
  echo "Release: $CMS_RELEASE"
  return 0
}

fix_ui_jdbc_test_properties() {
  local branch_version="$1"
  local database_number="$2"
  local old_database_number=0
  ((old_database_number=database_number+1))

  [ -z "$branch_version" ] && { echo "Undefined branch_version"; exit 1; }
  [ -z "$database_number" ] && { echo "Undefined database_number"; exit 1; }

  local auth=
  local protocol="svn+ssh://svn/home"
  [ "$LDAP_USER" = "oix.project.coordinator" ] && { auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive" ; protocol="http://svn.ocslab.com" ; }

  local folder=$WORKING_DIR/svn/$branch_version
  echo ": Checking out $branch_version/ui-ejb/src/test/resources to $folder"
  rm -rf $folder

  svn -q co $auth $protocol/svnroot/oix/ui/branches/$branch_version/ui-ejb/src/test/resources \
    --depth immediates $folder || return 1

  pushd $folder
    sed -i -e "s|_${old_database_number}|_${database_number}|g" jdbc-test.properties
    svn diff
    svn commit $auth -m "jdbc-test.properties updated" || return 1
  popd

  return 0
}

# ConfigParameters.java is used in Java unit tests (saiku)
fix_ui_config_parameters_java() {
  local branch_version="$1"
  local database_number="$2"
  local old_database_number=0
  ((old_database_number=database_number+1))

  [ -z "$branch_version" ] && { echo "Undefined branch_version"; exit 1; }
  [ -z "$database_number" ] && { echo "Undefined database_number"; exit 1; }

  local auth=
  local protocol="svn+ssh://svn/home"
  [ "$LDAP_USER" = "oix.project.coordinator" ] && { auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive" ; protocol="http://svn.ocslab.com" ; }

  local folder=$WORKING_DIR/svn/$branch_version
  echo ": Checking out $branch_version/ui-ejb/src/main/java/com/foros/config to $folder"
  rm -rf $folder

  svn -q co $auth $protocol/svnroot/oix/ui/branches/$branch_version/ui-ejb/src/main/java/com/foros/config \
    --depth immediates $folder || return 1

  pushd $folder
    sed -i -e "s|ui_dev_${old_database_number}|ui_dev_${database_number}|g" ConfigParameters.java
    svn diff
    svn commit $auth -m "ConfigParameters.java updated" || return 1
  popd

  return 0
}

fix_cms_trunk_version() {
  local prefix="$1"
  local project_folder="$2"

  [ -z "$prefix" ] && { echo "Undefined prefix"; exit 1; }
  [ -z "$project_folder" ] && { echo "Undefined project_folder"; exit 1; }

  local xml_file=""
  [ "$prefix" = "UI" ] && xml_file="$project_folder/cms-plugin/UIConfigDescriptor.xml"
  [ "$prefix" = "PGDB" ] && xml_file="$project_folder/cms-plugin/PGDBConfigDescriptor.xml"
  if [ "$prefix" = "PGADM" ]; then
    if [ "$PGADM_BRANCH" = "3.3.0" ]; then
      xml_file="$project_folder/cms-plugin/PostgresDbConfigDescriptor.xml"
    else
      xml_file="$project_folder/cms-plugin/PGADMConfigDescriptor.xml"
    fi
  fi
  [ ! -f $xml_file ] && { echo "$xml_file not found"; exit 1; }

  local svn_ver=`sed -r -e 's/[[:print:]]*?version[[:blank:]]*?=[[:blank:]]*?\"([[:digit:]]+\.[[:digit:]]+\.[[:digit:]]+\.[[:digit:]]+)\"[[:print:]]*|[[:print:]]*/\1/g' < $xml_file | sed '/^$/d'`
  sed "s/version=\"$svn_ver\"/version=\"trunk\"/" -i $xml_file

  pushd "$project_folder"
  svn diff

    local auth=
    [ "$LDAP_USER" = "oix.project.coordinator" ] && auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive"
    svn commit $auth -m "Project version updated" || return 1

  popd

  return 0
}

create_foros_ui_branch() {
  local branch_version="$1"

  [ -z "$branch_version" ] && { echo "Undefined branch_version" ; exit 1 ; }

  local auth=
  local protocol="svn+ssh://svn/home"
  [ "$LDAP_USER" = "oix.project.coordinator" ] && { auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive" ; protocol="http://svn.ocslab.com" ; }

  local ui_branch="svn+ssh://svn/home/svnroot/oix/ui/branches/$branch_version"
  is_svn_path_exists "$ui_branch" ; if [ "$?" = "0" ] ; then
    echo "Branch $ui_branch already exists"
  else
    echo "Branching FOROS UI $branch_version"
    svn copy $auth $protocol/svnroot/oix/ui/trunk $protocol/svnroot/oix/ui/branches/$branch_version -m "FOROS  UI branch" || return 1
  fi

  local wiki_branch="svn+ssh://svn/home/svnroot/jspwiki/branches/$branch_version"
  is_svn_path_exists "$wiki_branch" ; if [ "$?" = "0" ] ; then
    echo "Branch $wiki_branch already exists"
  else
    echo "Branching JSPWiki $branch_version"
    svn copy $auth $protocol/svnroot/jspwiki/trunk $protocol/svnroot/jspwiki/branches/$branch_version -m "JSP Wiki branch" || return 1
  fi

  return 0
}

create_foros_ui_tags() {
  local branch_version="$1"
  local new_version="$2"

  [ -z "$branch_version" ] && { echo "Undefined branch_version" ; exit 1 ; }
  [ -z "$new_version" ] && { echo "Undefined new_version" ; exit 1 ; }

  local auth=
  local protocol="svn+ssh://svn/home"
  [ "$LDAP_USER" = "oix.project.coordinator" ] && { auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive" ; protocol="http://svn.ocslab.com" ; }

  local ui_tag="svn+ssh://svn/home/svnroot/oix/ui/tags/$new_version"
  is_svn_path_exists "$ui_tag" ; if [ "$?" = "0" ] ; then
    echo "Tag $ui_tag already exists"
  else
    echo "Tagging FOROS UI $new_version"
    svn copy $auth $protocol/svnroot/oix/ui/branches/$branch_version $protocol/svnroot/oix/ui/tags/$new_version -m "FOROS UI tag" || return 1
  fi

  local wiki_tag="svn+ssh://svn/home/svnroot/jspwiki/tags/$new_version"
  is_svn_path_exists "$wiki_tag" ; if [ "$?" = "0" ] ; then
    echo "Tag $wiki_tag already exists"
  else
    echo "Tagging JSPWiki $new_version"
    svn copy $auth $protocol/svnroot/jspwiki/branches/$branch_version $protocol/svnroot/jspwiki/tags/$new_version -m "JSP Wiki tag" || return 1
  fi

  return 0
}

fix_pgdb_version_txt()  {
  local branch_version="$1"

  [ -z "$branch_version" ] && { echo "Undefined branch_version" ; exit 1 ; }

  local auth=
  local protocol="svn+ssh://svn/home"
  [ "$LDAP_USER" = "oix.project.coordinator" ] && { auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive" ; protocol="http://svn.ocslab.com" ; }

  local folder=$WORKING_DIR/svn/pgdb
  echo ": Checking out $branch_version to $folder"
  rm -rf $folder

  svn -q co $auth $protocol/svnroot/oix/pgdb/branches/$branch_version --depth immediates $folder || return 1

  pushd $folder
    echo $branch_version | cat > $folder/version.txt
    svn diff
    svn commit $auth -m "Fix version.txt" || return 1
  popd

  folder=$WORKING_DIR/svn/repl
  echo ": Checking out $branch_version to $folder"
  rm -rf $folder

  svn -q co $auth $protocol/svnroot/oix/streams-replication/branches/$branch_version --depth immediates $folder || return 1

  pushd $folder
    echo $branch_version | cat > $folder/version.txt
    svn diff
    svn commit $auth -m "Fix version.txt" || return 1
  popd

  return 0
}

create_foros_pgdb_branch() {
  local branch_version="$1"

  [ -z "$branch_version" ] && { echo "Undefined branch_version" ; exit 1 ; }

  local auth=
  local protocol="svn+ssh://svn/home"
  [ "$LDAP_USER" = "oix.project.coordinator" ] && { auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive" ; protocol="http://svn.ocslab.com" ; }

  local pgdb_branch="svn+ssh://svn/home/svnroot/oix/pgdb/branches/$branch_version"
  is_svn_path_exists "$pgdb_branch" ; if [ "$?" = "0" ] ; then
    echo "Branch $branch_version already exists"
  else
    echo "Branching PG DB $branch_version"
    svn copy $auth $protocol/svnroot/oix/pgdb/trunk $protocol/svnroot/oix/pgdb/branches/$branch_version -m "PG DB branch" || return 1
  fi

  local repl_branch="svn+ssh://svn/home/svnroot/oix/streams-replication/branches/$branch_version"
  is_svn_path_exists "$repl_branch" ; if [ "$?" = "0" ] ; then
    echo "Branch $branch_version already exists"
  else
    echo "Branching REPL $branch_version"
    svn copy $auth $protocol/svnroot/oix/streams-replication/trunk $protocol/svnroot/oix/streams-replication/branches/$branch_version -m "REPL branch" || return 1
  fi

  return 0
}

create_foros_pgdb_tags() {
  local branch_version="$1"
  local new_version="$2"

  [ -z "$branch_version" ] && { echo "Undefined branch_version" ; exit 1 ; }
  [ -z "$new_version" ] && { echo "Undefined new_version" ; exit 1 ; }

  local auth=
  local protocol="svn+ssh://svn/home"
  [ "$LDAP_USER" = "oix.project.coordinator" ] && { auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive" ; protocol="http://svn.ocslab.com" ; }

  local pgdb_tag="svn+ssh://svn/home/svnroot/oix/pgdb/tags/$new_version"
  is_svn_path_exists "$pgdb_tag" ; if [ "$?" = "0" ] ; then
    echo "Tag $pgdb_tag already exists"
  else
    echo "Tagging PG DB $new_version"
    svn copy $auth $protocol/svnroot/oix/pgdb/branches/$branch_version $protocol/svnroot/oix/pgdb/tags/$new_version -m "PG DB tag" || return 1
  fi

  local repl_tag="svn+ssh://svn/home/svnroot/oix/streams-replication/tags/$new_version"
  is_svn_path_exists "$repl_tag" ; if [ "$?" = "0" ] ; then
    echo "Tag $repl_tag already exists"
  else
    echo "Tagging REPL $new_version"
    svn copy $auth $protocol/svnroot/oix/streams-replication/branches/$branch_version $protocol/svnroot/oix/streams-replication/tags/$new_version -m "REPL tag" || return 1
  fi

  return 0
}

create_foros_bi_branch() {
  local branch_version="$1"
  local path="$2"

  [ -z "$branch_version" ] && { echo "Undefined branch_version" ; exit 1 ; }
  [ -z "$path" ] && path="bi"

  local auth=
  local protocol="svn+ssh://svn/home"
  [ "$LDAP_USER" = "oix.project.coordinator" ] && { auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive" ; protocol="http://svn.ocslab.com" ; }

  local bi_branch="svn+ssh://svn/home/svnroot/oix/$path/branches/$branch_version"
  is_svn_path_exists "$bi_branch" ; if [ "$?" = "0" ] ; then
    echo "Branch $branch_version already exists"
  else
    echo "Branching FOROS `echo $path | tr [:lower:] [:upper:]` $branch_version"
    svn copy $auth $protocol/svnroot/oix/$path/trunk $protocol/svnroot/oix/$path/branches/$branch_version -m "FOROS `echo $path | tr [:lower:] [:upper:]` branch" || return 1
  fi

  return 0
}

create_foros_bi_tags() {
  local branch_version="$1"
  local new_version="$2"
  local path="$3"

  [ -z "$branch_version" ] && { echo "Undefined branch_version" ; exit 1 ; }
  [ -z "$new_version" ] && { echo "Undefined new_version" ; exit 1 ; }
  [ -z "$path" ] && path="bi"

  local auth=
  local protocol="svn+ssh://svn/home"
  [ "$LDAP_USER" = "oix.project.coordinator" ] && { auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive" ; protocol="http://svn.ocslab.com" ; }

  local bi_tag="svn+ssh://svn/home/svnroot/oix/$path/tags/$new_version"
  echo ": $FUNCNAME: path $path"
  echo ": $FUNCNAME: url $bi_tag"
  is_svn_path_exists "$bi_tag" ; if [ "$?" = "0" ] ; then
    echo "Tag $bi_tag already exists"
  else
    echo "Tagging FOROS `echo $path | tr [:lower:] [:upper:]` $new_version"
    svn copy $auth $protocol/svnroot/oix/$path/branches/$branch_version $protocol/svnroot/oix/$path/tags/$new_version -m "FOROS `echo $path | tr [:lower:] [:upper:]` tag" || return 1
  fi

  return 0
}

fix_bi_version_txt()  {
  local branch_version="$1"

  [ -z "$branch_version" ] && { echo "Undefined branch_version" ; exit 1 ; }

  local auth=
  local protocol="svn+ssh://svn/home"
  [ "$LDAP_USER" = "oix.project.coordinator" ] && { auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive" ; protocol="http://svn.ocslab.com" ; }

  local folder=$WORKING_DIR/svn/bi
  echo ": Checking out $branch_version to $folder"
  rm -rf $folder

  svn -q co $auth $protocol/svnroot/oix/bi/branches/$branch_version --depth immediates $folder || return 1

  pushd $folder
    echo $branch_version | cat > $folder/version.txt
    svn diff
    svn commit $auth -m "Fix version.txt" || return 1
  popd

  return 0
}

create_foros_pgadm_branch() {
  local branch_version="$1"

  [ -z "$branch_version" ] && { echo "Undefined branch_version" ; exit 1 ; }

  local pgadm_branch="svn+ssh://svn/home/svnroot/oix/pgadm/branches/$branch_version"
  is_svn_path_exists "$pgadm_branch" && { echo "Branch $branch_version already exists" ; return 1 ; }

  echo "Branching PG ADM $branch_version"
  local auth=
  local protocol="svn+ssh://svn/home"
  [ "$LDAP_USER" = "oix.project.coordinator" ] && { auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive" ; protocol="http://svn.ocslab.com" ; }
  svn copy $auth $protocol/svnroot/oix/pgadm/trunk $protocol/svnroot/oix/pgadm/branches/$branch_version -m "PG ADM branch" || return 1
  return 0
}

create_foros_dstr_branch() {
  local branch_version="$1"

  [ -z "$branch_version" ] && { echo "Undefined branch_version" ; exit 1 ; }

  local dstr_branch="svn+ssh://svn/home/svnroot/oix/datastore/branches/$branch_version"
  is_svn_path_exists "$dstr_branch" && { echo "Branch $branch_version already exists" ; return 1 ; }

  echo "Branching DSTR $branch_version"
  local auth=
  local protocol="svn+ssh://svn/home"
  [ "$LDAP_USER" = "oix.project.coordinator" ] && { auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive" ; protocol="http://svn.ocslab.com" ; }
  svn copy $auth $protocol/svnroot/oix/datastore/trunk $protocol/svnroot/oix/datastore/branches/$branch_version -m "DSTR branch" || return 1
  return 0
}

create_foros_pgadm_tags() {
  local branch_version="$1"
  local new_version="$2"

  [ -z "$branch_version" ] && { echo "Undefined branch_version" ; exit 1 ; }
  [ -z "$new_version" ] && { echo "Undefined new_version" ; exit 1 ; }

  local pgadm_tag="svn+ssh://svn/home/svnroot/oix/pgadm/tags/$new_version"
  is_svn_path_exists "$pgadm_tag" && { echo "Tag $pgadm_tag already exists" ; return 0 ; }

  echo "Tagging PG ADM $new_version"
  local auth=
  local protocol="svn+ssh://svn/home"
  [ "$LDAP_USER" = "oix.project.coordinator" ] && { auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive" ; protocol="http://svn.ocslab.com" ; }
  svn copy $auth $protocol/svnroot/oix/pgadm/branches/$branch_version $protocol/svnroot/oix/pgadm/tags/$new_version -m "PG ADM tag" || return 1
  return 0
}

create_foros_addb_tags() {
  local branch_version="$1"
  local new_version="$2"

  [ -z "$branch_version" ] && { echo "Undefined branch_version" ; exit 1 ; }
  [ -z "$new_version" ] && { echo "Undefined new_version" ; exit 1 ; }

  local db_tag="svn+ssh://svn/home/svnroot/oix/db/tags/$new_version"
  is_svn_path_exists "$db_tag" && { echo "Tag $db_tag already exists" ; return 0 ; }

  echo "Tagging ADDB $new_version"
  local auth=
  local protocol="svn+ssh://svn/home"
  [ "$LDAP_USER" = "oix.project.coordinator" ] && { auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive" ; protocol="http://svn.ocslab.com" ; }
  svn copy $auth $protocol/svnroot/oix/db/branches/$branch_version $protocol/svnroot/oix/db/tags/$new_version -m "ADDB tag" || return 1
  return 0
}

create_foros_dstr_tags() {
  local branch_version="$1"
  local new_version="$2"

  [ -z "$branch_version" ] && { echo "Undefined branch_version" ; exit 1 ; }
  [ -z "$new_version" ] && { echo "Undefined new_version" ; exit 1 ; }

  local db_tag="svn+ssh://svn/home/svnroot/oix/datastore/tags/$new_version"
  is_svn_path_exists "$db_tag" && { echo "Tag $db_tag already exists" ; return 0 ; }

  echo "Tagging DSTR $new_version"
  local auth=
  local protocol="svn+ssh://svn/home"
  [ "$LDAP_USER" = "oix.project.coordinator" ] && { auth="--username $LDAP_USER --password $LDAP_PASSWORD --non-interactive" ; protocol="http://svn.ocslab.com" ; }
  svn copy $auth $protocol/svnroot/oix/datastore/branches/$branch_version $protocol/svnroot/oix/datastore/tags/$new_version -m "DSTR tag" || return 1
  return 0
}

print_variables() {
  for name in BAMBOO_SUBTASK COLO LDAP_USER \
              UI_VERSION PGDB_VERSION ADDB_VERSION PGADM_VERSION BI_VERSION BIADM_VERSION DSTR_VERSION \
              UI_BRANCH PGDB_BRANCH ADDB_BRANCH PGADM_BRANCH BI_BRANCH BIADM_BRANCH DSTR_BRANCH \
              UI_NEXT_VERSION PGDB_NEXT_VERSION ADDB_NEXT_VERSION PGADM_NEXT_VERSION BI_NEXT_VERSION BIADM_NEXT_VERSION DSTR_NEXT_VERSION \
              UI_NEXT_MAJOR_VERSION PGDB_NEXT_MAJOR_VERSION PGADM_NEXT_MAJOR_VERSION BI_NEXT_MAJOR_VERSION BIADM_NEXT_MAJOR_VERSION DSTR_NEXT_MAJOR_VERSION \
              UI_NEXT_MAJOR_BRANCH PGDB_NEXT_MAJOR_BRANCH PGADM_NEXT_MAJOR_BRANCH BI_NEXT_MAJOR_BRANCH BIADM_NEXT_MAJOR_BRANCH DSTR_NEXT_MAJOR_BRANCH \
              UI_PREV_VERSION PGDB_PREV_VERSION PGADM_PREV_VERSION BI_PREV_VERSION BIADM_PREV_VERSION DSTR_PREV_VERSION \
              UI_PREV_BRANCH PGDB_PREV_BRANCH PGADM_PREV_BRANCH BI_PREV_BRANCH BIADM_PREV_BRANCH DSTR_PREV_BRANCH \
              MONITORING_HOST UI_HOSTS PG_HOSTS AD_HOSTS BI_HOSTS DSTR_HOSTS \
              UI_CMS_RELEASE PGDB_CMS_RELEASE PGADM_CMS_RELEASE BAMBOO_STOPSERVICES; do
    [ -z "${!name}" ] || echo -e ": $name = '${!name}'"
  done
  return 0
}

setup_trunk_version() {
  local prefix="$1"
  [ -z "$prefix" ] && { echo "Undefined prefix"; return 1; }

  local svnpath=""
  [ "$prefix" = "UI" ] && svnpath="oix/ui"
  [ "$prefix" = "PGDB" ] && svnpath="oix/pgdb"
  [ "$prefix" = "PGADM" ] && svnpath="oix/pgadm"
  [ "$prefix" = "BI" ] && svnpath="oix/bi"
  [ "$prefix" = "BIADM" ] && svnpath="oix/biadm"
  [ "$prefix" = "DSTR" ] && svnpath="oix/datastore"
  [ -z "$svnpath" ] && { echo "Unknown prefix '$prefix'"; return 1; }
  svnpath="svn+ssh://svn/home/svnroot/$svnpath/branches"

  doc get_latests_branch 1 "$svnpath"
  local major=`echo $LATESTS_BRANCH | sed -n -e 's|[0-9]\+\.\([0-9]\+\)\.[0-9]\+|\1|p'`
  ((major+=1))
  readonly "${prefix}_PREV_BRANCH"="$LATESTS_BRANCH"
  readonly "${prefix}_BRANCH"="${LATESTS_BRANCH%%.*}.$major.${LATESTS_BRANCH##*.}"
  readonly "${prefix}_VERSION"="${LATESTS_BRANCH%%.*}.$major.${LATESTS_BRANCH##*.}.0"
  return 0
}

check_tag_version() {
  local prefix="$1"
  local version="$2"
  [ -z "$prefix" ] && { echo "Undefined prefix"; return 1; }
  [ -z "$version" ] && { echo "Undefined version"; return 1; }

  local svnpath=""
  [ "$prefix" = "UI" ] && svnpath="oix/ui"
  [ "$prefix" = "PGDB" ] && svnpath="oix/pgdb"
  [ "$prefix" = "PGADM" ] && svnpath="oix/pgadm"
  [ "$prefix" = "BI" ] && svnpath="oix/bi"
  [ "$prefix" = "BIADM" ] && svnpath="oix/biadm"
  [ "$prefix" = "ADDB" ] && svnpath="oix/db"
  [ "$prefix" = "DSTR" ] && svnpath="oix/datastore"
  [ -z "$svnpath" ] && { echo "Unknown prefix '$prefix'"; return 1; }
  svnpath="svn+ssh://svn/home/svnroot/$svnpath/tags"

  is_svn_path_exists "$svnpath/$version" && return 0
  local prev_version="${version%.*}.$((${version##*.} - 1))"
  is_svn_path_exists "$svnpath/$prev_version" && return 0

  echo ": $FUNCNAME: $prefix tags $version and $prev_version does not exist"
  return 1
}


setup_next_major_version() {
  local prefix="$1"
  [ -z "$prefix" ] && { echo "Undefined prefix"; return 1; }

  local v_version="${prefix}_BRANCH"
  local branch=${!v_version}
  local major=`echo $branch | sed -n -e 's|[0-9]\+\.\([0-9]\+\)\.[0-9]\+|\1|p'`
  ((major+=1))
  readonly "${prefix}_NEXT_MAJOR_BRANCH"="${branch%%.*}.$major.${branch##*.}"
  readonly "${prefix}_NEXT_MAJOR_VERSION"="${branch%%.*}.$major.${branch##*.}.0"
  return 0
}

setup_next_version() {
  local prefix="$1"
  local v_version="${prefix}_VERSION"
  local version="$2"

  [ -z "$prefix" ] && { echo "Undefined prefix"; return 1; }

  local svnpath=""
  [ "$prefix" = "UI" ] && svnpath="oix/ui"
  [ "$prefix" = "PGDB" ] && svnpath="oix/pgdb"
  [ "$prefix" = "PGADM" ] && svnpath="oix/pgadm"
  [ "$prefix" = "ADDB" ] && svnpath="oix/db"
  [ "$prefix" = "BI" ] && svnpath="oix/bi"
  [ "$prefix" = "BIADM" ] && svnpath="oix/biadm"
  [ "$prefix" = "DSTR" ] && svnpath="oix/datastore"
  [ -z "$svnpath" ] && { echo "Unknown prefix '$prefix'"; return 1; }

  if [ -z "$version" ]; then
    version="${!v_version}"
    [ -z "$version" ] && { echo "Undefined version and $v_version"; return 1; }
  fi

  if is_branch $version; then
    doc get_latests_tag $version "svn+ssh://svn/home/svnroot/$svnpath/tags"
    readonly "${prefix}_BRANCH"=$version
    readonly "${prefix}_VERSION"="${LATESTS_TAG%.*}.$(( ${LATESTS_TAG##*.} + 1))"
  else
    readonly "${prefix}_BRANCH"=${version%.*}
    readonly "${prefix}_VERSION"=$version
  fi
  return 0
}

setup_version() {
  local prefix="$1"
  local v_version="${prefix}_VERSION"
  local version="$2"

  [ -z "$prefix" ] && { echo "Undefined prefix"; return 1; }

  local svnpath=""
  [ "$prefix" = "UI" ] && svnpath="oix/ui"
  [ "$prefix" = "PGDB" ] && svnpath="oix/pgdb"
  [ "$prefix" = "PGADM" ] && svnpath="oix/pgadm"
  [ "$prefix" = "ADDB" ] && svnpath="oix/db"
  [ "$prefix" = "BI" ] && svnpath="oix/bi"
  [ "$prefix" = "BIADM" ] && svnpath="oix/biadm"
  [ "$prefix" = "DSTR" ] && svnpath="oix/datastore"
  [ -z "$svnpath" ] && { echo "Unknown prefix '$prefix'"; return 1; }

  if [ -z "$version" ]; then
    version="${!v_version}"
    [ -z "$version" ] && { echo "Undefined version and $v_version"; return 1; }
  fi

  if is_branch $version; then
    doc get_latests_tag $version "svn+ssh://svn/home/svnroot/$svnpath/tags"
    readonly "${prefix}_BRANCH"=$version
    readonly "${prefix}_VERSION"="$LATESTS_TAG"
  else
    readonly "${prefix}_BRANCH"=${version%.*}
    readonly "${prefix}_VERSION"=$version
  fi
  return 0
}

setup_hosts() {
  local colo="$1"
  [ -z "$colo" ] && { echo "Undefined colo"; return 1; }

  case $colo in
    moscow-test-central)
      readonly "UI_HOSTS=voix0 voix1 voix2"
      readonly "PG_HOSTS=stat-test"
      readonly "AD_HOSTS=nnode0"
      readonly "BI_HOSTS=pentaho-test"
      readonly "DSTR_HOSTS=hadoop1"
      readonly "MONITORING_HOST=zenoss"
      ;;
    moscow-stage-central)
      readonly "UI_HOSTS=soix0"
      readonly "PG_HOSTS=spostdb0"
      readonly "AD_HOSTS=snode0"
      readonly "BI_HOSTS=pentaho-stage"
      readonly "DSTR_HOSTS=hadoop1"
      readonly "MONITORING_HOST=zenoss"
      ;;
    moscow-emergency-central)
      readonly "UI_HOSTS=eoix"
      readonly "PG_HOSTS=epostgres"
      readonly "AD_HOSTS=enode0"
      readonly "BI_HOSTS=pentaho-test"
      readonly "DSTR_HOSTS=hadoop1"
      readonly "MONITORING_HOST=zenoss"
      ;;
    *)
      echo "Unsupported colo: $colo"
      return 1
      ;;
  esac
  return 0
}

check_major_version() {
  local package="$1"
  local host=$2
  local version=$3
  [ -z "$package" ] && { echo "Undefined package"; return 1; }
  [ -z "$host" ] && { echo "Undefined host"; return 1; }
  [ -z "$version" ] && { echo "Undefined version"; return 1; }

  local installed
  installed=`ssh -o 'BatchMode yes' $host -- rpm -q --qf '%{version}' $package`
  [ "$?" != "0" ] && { echo ": $FUNCNAME: WARNING: Could not get version of $package on $host"; return 0; }
  if [ "${installed%.*}" != "${version%.*}" ]; then
    echo "Major versions of $package are mismatched: installed = $installed, expected = $version"
    return 1
  fi
  return 0
}

setup_ldap_credentials() {
  local prefix="$1"

  [ -z "$prefix" ] && prefix="UI"
  [ "$prefix" = "BIADM" ] && prefix="BI"

  local v_user="${prefix}_LDAP_USER"
  local v_password="${prefix}_LDAP_PASSWORD"
  [ -z "${!v_user}" ] && { echo ": $FUNCNAME: Undefined $v_user"; return 1; }
  [ -z "${!v_password}" ] && { echo ": $FUNCNAME: Undefined $v_password"; return 1; }

  LDAP_USER="${!v_user}"
  LDAP_PASSWORD="${!v_password}"
  return 0
}

build_project() {
  local colo="$1"
  local svnpath="$2"
  local version="$3"

  [ -z "$colo" ] && { echo ": $FUNCNAME: Undefined colo"; return 1; }
  [ -z "$svnpath" ] && { echo ": $FUNCNAME: Undefined svnpath"; return 1; }
  [ -z "$version" ] && { echo ": $FUNCNAME: Undefined version"; return 1; }

  local repo="test"
  [[ "$colo" = "moscow-stage-central" ]] && repo="stage"
  [[ "$colo" = "moscow-emergency-central" ]] && repo="eme"

  local package_name="`echo $svnpath | tr '/' '-'`"
  local package="${package_name}-${version}"

  if ! repo_is_package_in_repo -r $repo "${package}*.rpm" ; then
    if ! find_product_packages $package_name $version; then
      echo
      local options=""
      if [ "$svnpath" = "oix/bi" -a "$colo" != "moscow-stage-central" ]; then
        options="-D '%skipAnalysisDatasources true'" # do not install analysis datasources
      fi
      doc ssh maint@buildbox "abt -v $version -r $repo $options $svnpath 2>\$HOME/release-project.log || { tail -n 150 \$HOME/release-project.log ; exit 1 ; }"
    else
      doc copy_packages_to_repo $repo $PRODUCT_PACKAGES
    fi
  else
    echo "package '$package' is in repo $repo"
  fi

  return 0
}

run_plan() {
  local stage="$1"
  local plan="$2"

  [ -z "$stage" ] && { echo ": $FUNCNAME: undefined stage"; return 1; }
  [ -z "$plan" ] && { echo ": $FUNCNAME: undefined plan"; return 1; }

  [ -z "$LDAP_USER" ] && { echo ": $FUNCNAME: undefined LDAP_USER"; return 1; }
  [ -z "$LDAP_PASSWORD" ] && { echo ": $FUNCNAME: undefined LDAP_PASSWORD"; return 1; }

  local bamboo_rest_url="https://bamboo.ocslab.com/rest/api/latest"

  echo ": $FUNCNAME: triggering plan $plan ($stage)"

  local output
  output=`curl -v -k --user "$LDAP_USER:$LDAP_PASSWORD" -X POST -d "$stage&ExecuteAllStages" $bamboo_rest_url/queue/${plan}.json 2>$WORKING_DIR/curl.txt`
  if [ "$?" != "0" ]; then
    cat $WORKING_DIR/curl.txt
    return 1
  fi

  echo "$output"
  if echo "$output" | grep -q $plan; then
    return 0
  else
    return 1
  fi
}
