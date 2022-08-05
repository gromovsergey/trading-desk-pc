#!/bin/bash

# add this to ~/.m2/settings.xml
# <server><id>foros</id><username>admin</username><password>******</password></server><server><id>foros-snapshots</id><username>admin</username><password>******</password></server>
ui_install_rs_client() {
  local version="$1"
  local version_name="$2"

  [ -z "$version" ] && { echo ": $FUNCNAME: Undefined version"; exit 1; }
  [ -z "$version_name" ] && { echo ": $FUNCNAME: Undefined version_name"; exit 1; }

  local rs_client_maven_url="http://maven.ocslab.com/nexus/content/repositories/foros/com/foros/ui/rs-client-java/${version}/rs-client-java-${version}.jar"
  echo "Checking, is RS Client in Maven repo at $rs_client_maven_url"
  wget $rs_client_maven_url 2>/dev/null
  [ "$?" = "0" ] && { echo "Yes, it is" ; return 0; }

  get_svn_path $version
  local svnpath="svn+ssh://svn/home/svnroot/oix/ui/$SVN_PATH"

  execute_remote oix-dev7 $version $version_name $svnpath <<-"EOF"
    #!/bin/bash
    WORKING_DIR=`pwd`
    . $WORKING_DIR/commons.sh

    VERSION=$1
    VERSION_NAME=$2
    SVNPATH=$3

    svn_export_folder $SVNPATH

    sudo alternatives --set java  /usr/lib/jvm/jre-1.7.0-oracle.x86_64/bin/java
    sudo alternatives --set javac /usr/lib/jvm/java-1.7.0-oracle.x86_64/bin/javac

    echo ; echo ": Building & installing FOROS UI RS Client ($VERSION_NAME) from $SVNPATH"
    docl mkdir $WORKING_DIR/m2
    docl cp ~/.m2/settings.xml $WORKING_DIR/m2
    pushd $CHECKOUT_FOLDER
      docl mvn -Dmaven.repo.local=$WORKING_DIR/m2 -Dmaven.test.skip.exec=true clean install -P RsClient
      pushd rs-client
        doc mvn -Dmaven.repo.local=$WORKING_DIR/m2 -Dmaven.test.skip.exec=true -Ddeploy.version="${VERSION_NAME}" source:jar deploy -PRemoteInstall
      popd
    popd

    exit 0
EOF
  local result=$?
  return $result
}

ui_test_rs_client() {
  local ui_branch="$1"
  [ -z "$ui_branch" ] && { echo ": $FUNCNAME: Undefined ui_branch"; exit 1; }

  get_svn_path $ui_branch
  local svnpath="svn+ssh://svn/home/svnroot/oix/ui/$SVN_PATH"

  execute_remote oix-dev5 "$svnpath" <<-"EOF"
    #!/bin/bash
    WORKING_DIR=`pwd`
    . $WORKING_DIR/commons.sh

    SVNPATH=$1
    svn_export_folder "$SVNPATH"

    echo ; echo ": Building and installing FOROS UI from $SVNPATH"
    cd $CHECKOUT_FOLDER
    docl mvn install -Dmaven.test.skip.exec=true

    result=0
    echo ; echo ": Testing rs-client"
    echo "Command: $CHECKOUT_FOLDER/mvn install --projects rs-client -am -amd -P RsClient"
    cd $CHECKOUT_FOLDER
    mvn install --projects rs-client -am -amd -P RsClient
    if [ "$?" -ne "0" ] ; then
      result=1

      echo ; echo "Failed test(s):"
      for file in $CHECKOUT_FOLDER/rs-client/java/target/surefire-reports/*.txt ; do
        cat $file | grep 'FAILURE' > /dev/null
        if [ "$?" = "0" ] ; then
          cat $file
          echo
        fi
      done
    fi

    exit $result
EOF
  local result=$?
  return $result
}

ui_unittest() {
  local host="$1"
  local branch="$2"

  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host "; exit 1; }
  [ -z "$branch" ] && { echo ": $FUNCNAME: undefined branch "; exit 1; }

  execute_remote $host $branch <<-"EOF"
  #!/bin/bash
  WORKING_DIR=`pwd`
  . $WORKING_DIR/commons.sh

  UI_BRANCH=$1
  [ -z "$UI_BRANCH" ] && { echo ": ui_unittest: undefined UI_BRANCH" ; exit 1 ; }
  M2_REPO=$HOME/.m2-`basename $UI_BRANCH`
  echo ": ui_unittest: ui branch: $UI_BRANCH ; m2 repo: $M2_REPO"

  svn_export_folder "svn+ssh://svn/home/svnroot/oix/ui/$UI_BRANCH" || exit 1
  cd $CHECKOUT_FOLDER

  echo ; echo ": Check JSP compile"
  docl mvn -Dmaven.repo.local=$M2_REPO -Dmaven.test.skip.exec=true install -P PrecompileJsp

  echo ; echo ": Start Unit tests"
  export MAVEN_OPTS="-XX:MaxPermSize=256m"
  mvn -Dmaven.repo.local=$M2_REPO test
  result=$?
  echo ": result=$result"
  [ "$result" = "0" ] && exit 0

  echo ": Failed tests:"
  for module in "ui-ejb" "ui-war" ; do
    if [ -e "$CHECKOUT_FOLDER/$module/target/surefire-reports" ] ; then
      for file in `grep -lr "FAILURE\!" $CHECKOUT_FOLDER/$module/target/surefire-reports` ; do
        cat $file
      done
    fi
  done
  exit 1
EOF
  result=$?

  local module=""
  for module in ui-ejb ui-war; do
    scp -r -o BatchMode=yes $host:/$WORKING_DIR/svn/`basename $branch`/$module/target/surefire-reports $WORKING_DIR/$module
  done

  return $result
}

ui_build_ear() {
  local svnpath="$1"
  local outputfile="$2"
  local target="$3"

  [ -z "$svnpath" ] && { echo ": $FUNCNAME: undefined svnpath "; exit 1; }
  [ -z "$outputfile" ] && { echo ": $FUNCNAME: undefined outputfile "; exit 1; }
  [ -z "$target" ] && target="el6"

  local centos_release=""
  if [ "${target%%[0-9]}" = "el" ]; then
    abt_setup_custom_build_mock_config "oix/ui" $target "foros_ui_ear_custombuild"
    centos_release="$target"
  else
    abt_setup_custom_build_mock_config_with_host "oix/ui" $target "foros_ui_ear_custombuild"
    centos_release="$CENTOS_RELEASE"
  fi
  local mock_config_dir=`dirname $ABT_MOCK_CONFIG_FILE`
  local mock_config="${ABT_MOCK_CONFIG}-x86_64"

  local root_dir="`mock -v --configdir=$mock_config_dir -r $mock_config --print-root`"
  [ -z "$root_dir" ] && { echo ": $FUNCNAME: undefined root_dir"; exit 1; }
  echo ": $FUNCNAME: mock root is '$root_dir'"

  echo ": $FUNCNAME: export svnpath"
  doc svn_export_folder svn+ssh://svn/home/svnroot/oix/ui/$svnpath

  echo ": $FUNCNAME: creating mock"
  doc mock_create $mock_config_dir $mock_config
  echo ": $FUNCNAME: done"

  echo ": $FUNCNAME: coping '$CHECKOUT_FOLDER' to chroot"
  doc mock_copyin $mock_config_dir $mock_config $CHECKOUT_FOLDER "/builddir/ui"

  echo ": $FUNCNAME: building EAR"
  doc mock_run $mock_config_dir $mock_config \
    "'cd /builddir/ui; mvn install -B -Dmaven.test.skip.exec=true -Dmaven.repo.local=/builddir/m2 -P PrecompileJsp'"

  echo ": $FUNCNAME: coping files to chroot"
  doc mock_copyout $mock_config_dir $mock_config "/builddir/ui/ui-ear/target/foros-ui.ear" "$outputfile"

  echo ": $FUNCNAME: deleting mock"
  doc mock_clean $mock_config_dir $mock_config
  echo ": $FUNCNAME: done"
  return 0
}

ui_custom_build() {
  local ui_svnpath="$1"
  local jspwiki_svnpath="$2"
  local unixcommons_svnpath="$3"
  local target="$4" # "oix-devN" or "el6"

  [ -z "$ui_svnpath" ] && { echo ": $FUNCNAME: undefined ui_svnpath"; exit 1; }
  [ -z "$jspwiki_svnpath" ] && { echo ": $FUNCNAME: undefined jspwiki_svnpath "; exit 1; }
  [ -z "$unixcommons_svnpath" ] && { echo ": $FUNCNAME: undefined unixcommons_svnpath "; exit 1; }
  [ -z "$target" ] && target="el6"
  local centos_release=""

  if [ "${target%%[0-9]}" = "el" ]; then
    abt_setup_custom_build_mock_config "oix/ui" $target
    centos_release="$target"
  else
    abt_setup_custom_build_mock_config_with_host "oix/ui" $target
    centos_release="$CENTOS_RELEASE"
  fi

  local unixcommons="-A unixcommons unixcommons/$unixcommons_svnpath"
  local jspwiki="-A jspwiki jspwiki/$jspwiki_svnpath"

  local notbuild=""
  if echo $notBuild | grep -q "unixcommons"; then
    get_centos_release "oui-nbmaster0"
    if [ "X$CENTOS_RELEASE" = "X$centos_release" ]; then
      unixcommons="-a unixcommons unixcommons/trunk"
      notbuild="unixcommons"
    fi
  fi

  if echo $notBuild | grep -q "jspwiki"; then
    jspwiki="-a jspwiki jspwiki/trunk"
    notbuild="$notbuild jspwiki"
  fi
  export notBuild="$notbuild"

  abt_custom_build_with_cache "oix/ui" "foros-ui" \
    -A "oix/ui oix/ui/$ui_svnpath" "$jspwiki" "$unixcommons" \
    -F "fake/centos:$centos_release" \
    -c "abt-cb" \
    -m "$ABT_MOCK_CONFIG" \
    -D "%precompileJsp true"
  abt_save_custom_build_version "oix/ui" "$ABT_VERSION"
  return $?
}

ui_get_package_list() {
  product_get_package_list "ui" $@
}
