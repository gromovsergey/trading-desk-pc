#!/bin/bash

download_plugin_colocation_xml() {
  local COLO_NAME=$1
  local PLUGIN=$2
  local VERSION=$3
  local PLUGIN_VERSION=$4

  [ -z "$COLO_NAME" ] && { echo ": $FUNCNAME: Undefined COLO_NAME" ; exit 1 ; }
  [ -z "$PLUGIN" ] && { echo ": $FUNCNAME: Undefined PLUGIN" ; exit 1 ; }
  [ -z "$VERSION" ] && { echo ": $FUNCNAME: Undefined VERSION" ; exit 1 ; }
  [ -z "$PLUGIN_VERSION" ] && PLUGIN_VERSION=trunk

  COLO_XML_FILE="$WORKING_DIR/$COLO_NAME-$PLUGIN-$PLUGIN_VERSION-colocation.xml"
  echo ": $FUNCNAME: downloading $COLO_NAME-$PLUGIN-$PLUGIN_VERSION"

  while true ; do
    local credentials=`ssh cms-test "(cat /opt/cms/config/api.creds | head -n 1)"`
    curl -v -k -u $credentials --get -d "colo=$COLO_NAME&plugin=$PLUGIN-$PLUGIN_VERSION" \
      https://cms-test.ocslab.com:8181/services/colocation.xml -o $COLO_XML_FILE >> $WORKING_DIR/curl.log 2>&1
    local result=$?

    # CURLE_SSL_CONNECT_ERROR (35) A problem occurred somewhere in the SSL/TLS handshake.
    [ "$result" = "35" ] && { sleep 20; continue; }
    [ "$result" != "0" ] && { cat $WORKING_DIR/curl.log; echo ": $FUNCNAME: curl exit code: $result"; }

    break
  done

  # Set the version in the downloaded colocation.xml
  sed -i -e "s/descriptor=\"$PLUGIN-trunk\"/descriptor=\"$PLUGIN-$VERSION\"/" -i $COLO_XML_FILE
  sed -i -e "s/descriptor=\"$PLUGIN-[0-9]\+.[0-9]\+.[0-9]\+.[0-9]\+\"/descriptor=\"$PLUGIN-$VERSION\"/" -i $COLO_XML_FILE
  return 0
}

create_plugin_colocation_xml() {
  local HOST=$1
  local PLUGIN=$2
  local VERSION=$3
  local BRANCH=$4
  shift; shift; shift; shift;
  local PARAMS="$@"

  [ -z "$HOST" ] && { echo ": $FUNCNAME: Undefined HOST" ; exit 1 ; }
  [ -z "$PLUGIN" ] && { echo ": $FUNCNAME: Undefined PLUGIN" ; exit 1 ; }
  [ -z "$BRANCH" ] && { echo ": $FUNCNAME: Undefined BRANCH" ; exit 1 ; }
  [ -z "$VERSION" ] && { echo ": $FUNCNAME: Undefined VERSION" ; exit 1 ; }

  case $PLUGIN in
    "FOROS-UI")
      local SVN_ROOT="svn+ssh://svn/home/svnroot/oix/ui";
      local TEMPLATE_GENERATOR="dev-ui-config-generator.sh";
      local TEMPLATE_FILENAME="dev-ui-config-template.xml";
      ;;
    "FOROS-PGDB")
      local SVN_ROOT="svn+ssh://svn/home/svnroot/oix/pgdb";
      local TEMPLATE_GENERATOR="dev-pgdb-config-generator.sh";
      local TEMPLATE_FILENAME="dev-pgdb-config-template.xml";
      ;;
    "FOROS-DSTR")
      local SVN_ROOT="svn+ssh://svn/home/svnroot/oix/datastore";
      local TEMPLATE_GENERATOR="dev-datastore-config-generator.sh";
      local TEMPLATE_FILENAME="dev-datastore-config-template.xml";
      ;;
    "FOROS-GPDB")
      local SVN_ROOT="svn+ssh://svn/home/svnroot/oix/gpdb";
      local TEMPLATE_GENERATOR="dev-gpdb-config-generator.sh";
      local TEMPLATE_FILENAME="dev-gpdb-config-template.xml";
      ;;
    "AdClient")
      local SVN_ROOT="svn+ssh://svn/home/svnroot/oix/ui";
      local TEMPLATE_GENERATOR="dev-ui-config-generator.sh";
      local TEMPLATE_FILENAME="creatives-dev-ui-config-template.xml";
      ;;
    *) echo ": $FUNCNAME: Unknown PLUGIN: $PLUGIN" ; exit 1;
      ;;
  esac

  docl svn_export_folder "$SVN_ROOT/$BRANCH/cms-plugin"
  COLO_XML_FILE="$WORKING_DIR/moscow-dev-ui-$HOST-$PLUGIN-`basename $BRANCH`-colocation.xml"

  # Replace all variables with values
  $CHECKOUT_FOLDER/configs/dev/$TEMPLATE_GENERATOR \
    $CHECKOUT_FOLDER/configs/dev/$TEMPLATE_FILENAME \
    $CHECKOUT_FOLDER/configs/dev/$HOST \
    $COLO_XML_FILE \
    "CMS_CURRENT_VERSION=$VERSION $PARAMS" || exit 1
  return 0
}


create_config_rpms() {
  local PLUGIN=$1
  local VERSION=$2
  local BRANCH=$3
  local COLO_XML_FILE=$4

  [ -z "$PLUGIN" ] && { echo ": $FUNCNAME: Undefined PLUGIN" ; exit 1 ; }
  [ -z "$BRANCH" ] && { echo ": $FUNCNAME: Undefined BRANCH" ; exit 1 ; }
  [ -z "$VERSION" ] && { echo ": $FUNCNAME: Undefined VERSION" ; exit 1 ; }
  [ -z "$COLO_XML_FILE" ] && { echo ": $FUNCNAME: Undefined COLO_XML_FILE" ; exit 1 ; }

  case $PLUGIN in
    "FOROS-UI")
      local SVN_ROOT="svn+ssh://svn/home/svnroot/oix/ui";
      local PLUGIN_FOLDER="cms-plugin";
      local CHECKOUT_ADDITIONAL=""
      ;;
    "FOROS-GPDB")
      local SVN_ROOT="svn+ssh://svn/home/svnroot/oix/gpdb";
      local PLUGIN_FOLDER="cms-plugin";
      local CHECKOUT_ADDITIONAL=""
      ;;
    "FOROS-GPADM")
      local SVN_ROOT="svn+ssh://svn/home/svnroot/oix/gpadm";
      local PLUGIN_FOLDER="cms-plugin";
      local CHECKOUT_ADDITIONAL=""
      ;;
    "FOROS-PGDB")
      local SVN_ROOT="svn+ssh://svn/home/svnroot/oix/pgdb";
      local PLUGIN_FOLDER="cms-plugin";
      local CHECKOUT_ADDITIONAL="$SVN_ROOT/$BRANCH/utility/cluster/jobs#utility/cluster/jobs"
      ;;
    "FOROS-DSTR")
      local SVN_ROOT="svn+ssh://svn/home/svnroot/oix/datastore";
      local PLUGIN_FOLDER="cms-plugin";
      local CHECKOUT_ADDITIONAL="$SVN_ROOT/$BRANCH/oozie#oozie $SVN_ROOT/$BRANCH/patches#patches"
      ;;
    "AdClient")
      local SVN_ROOT="svn+ssh://svn/home/svnroot/oix/creatives";
      local PLUGIN_FOLDER="CMS";
      local CHECKOUT_ADDITIONAL=""
      ;;
    "PGADM")
      local SVN_ROOT="svn+ssh://svn/home/svnroot/oix/pgadm";
      local PLUGIN_FOLDER="cms-plugin";
      local CHECKOUT_ADDITIONAL=""
      ;;
    *) echo ": $FUNCNAME: Unknown PLUGIN: $PLUGIN" ; exit 1;
      ;;
  esac

  # Build the CMS plugin
  execute_remote_ex `whoami` cms-test "*.sh *.xml" "$VERSION" "$SVN_ROOT/$BRANCH/$PLUGIN_FOLDER" "$COLO_XML_FILE" "$PLUGIN" "$CHECKOUT_ADDITIONAL" <<-"EOF"
    WORKING_DIR=`pwd`
    . $WORKING_DIR/commons.sh

    VERSION=$1
    PLUGIN_SVN_PATH=$2
    COLO_XML_FILE=$3
    PLUGIN=$4
    shift; shift; shift; shift;
    CHECKOUT_ADDITIONAL="$@"

    docl svn_export_folder "$PLUGIN_SVN_PATH"
    PLUGIN_FOLDER=$CHECKOUT_FOLDER


    for FOLDER in $CHECKOUT_ADDITIONAL; do
      docl svn_export_folder `echo $FOLDER | sed -e 's|#| |g'`
    done

    if [ "$PLUGIN" = "AdClient" ]; then
      echo ": create_config_rpms: fixing $PLUGIN_FOLDER/data/AdClient.spec"
      sed 's|Requires: foros-config-ui.*$||g' -i $PLUGIN_FOLDER/data/AdClient.spec
    fi

    if [ -e "$PLUGIN_FOLDER/create_plugin.sh" ]; then
      docl $PLUGIN_FOLDER/create_plugin.sh $PLUGIN_FOLDER/plugin-$VERSION.zip $PLUGIN_FOLDER/..
    fi

    sed "s/version=\\"[0-9]\\+\\.[0-9]\\+\\.[0-9]\\+\\.[0-9]\\+\\"/version=\\"$VERSION\\"/" -i $PLUGIN_FOLDER/*.xml
    sed "s/version=\\"trunk\\"/version=\\"$VERSION\\"/" -i $PLUGIN_FOLDER/*.xml

    doc /opt/cms/bin/cfgen -o res buildRpm $COLO_XML_FILE $PLUGIN_FOLDER
EOF
  [ "$?" != "0" ] && exit 1

  # download the generated rpms
  mkdir -p $WORKING_DIR/rpms
  for file in `ssh cms-test "cd $WORKING_DIR ; find res -type f -name *.rpm"`; do
    docl scp cms-test:$WORKING_DIR/$file $WORKING_DIR/rpms/
    local package_basename="`basename $file`"
    local package_name="`rpm -qp --qf '%{name}' $WORKING_DIR/rpms/$package_basename`"
    local package_version="`rpm -qp --qf '%{version}' $WORKING_DIR/rpms/$package_basename`"
    local branches="oix/`basename $SVN_ROOT`/$BRANCH/$PLUGIN_FOLDER"
    if [ ! -z "$CHECKOUT_ADDITIONAL" ]; then
      for folder in ${CHECKOUT_ADDITIONAL}; do
        branches="$branches oix/`basename $SVN_ROOT`/$BRANCH/${folder#*\#}"
      done
    fi
    docl mkdir -p /tmp/OUI-COMMON/repo/config
    docl cp $WORKING_DIR/rpms/$package_basename /tmp/OUI-COMMON/repo/config
    cache_save_package_version "$package_name" "$package_version" $branches
  done

  echo ": $FUNCNAME: $PLUGIN configuration rpms created successfully"
  return 0
}
