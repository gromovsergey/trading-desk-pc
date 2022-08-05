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
  host=oix-dev7
  if is_svn_branch_ge "ui" "$SVN_PATH" "3.6.0" ; then host=oix-dev8 ; fi

  execute_remote $host $SVN_PATH $version_name "svn+ssh://svn/home/svnroot/oix/ui/$SVN_PATH" <<-"EOF"
    #!/bin/bash
    WORKING_DIR=`pwd`
    . $WORKING_DIR/commons.sh

    VERSION=$1
    VERSION_NAME=$2
    SVNPATH=$3

    doc svn_export_folder $SVNPATH

    # JAVA_VERSION="1.7.0"
    # if is_svn_branch_ge "ui" "$VERSION" "3.6.0" ; then JAVA_VERSION="1.8.0" ; fi
    # sudo alternatives --set java  /usr/lib/jvm/jre-$JAVA_VERSION-oracle.x86_64/bin/java
    # sudo alternatives --set javac /usr/lib/jvm/java-$JAVA_VERSION-oracle.x86_64/bin/javac
    # export JAVA_HOME=/usr/lib/jvm/java-$JAVA_VERSION-oracle.x86_64
    # export PATH=$JAVA_HOME/bin:$PATH
    # echo ": JAVA_HOME=$JAVA_HOME ; PATH=$PATH"

    echo ": Building & installing FOROS UI RS Client ($VERSION_NAME) from $SVNPATH"
    docl mkdir $WORKING_DIR/m2
    docl cp ~/.m2/settings.xml $WORKING_DIR/m2
    pushd $CHECKOUT_FOLDER
      docl mvn clean install -B -Dmaven.repo.local=$WORKING_DIR/m2 -Dmaven.test.skip.exec=true -P RsClient -pl rs-client/java,rs-client/php -am
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

    JAVA_VERSION="1.7.0"
    if is_svn_branch_ge "ui" "$SVNPATH" "3.6.0" ; then JAVA_VERSION="1.8.0" ; fi
    sudo alternatives --set java  /usr/lib/jvm/jre-$JAVA_VERSION-oracle.x86_64/bin/java
    sudo alternatives --set javac /usr/lib/jvm/java-$JAVA_VERSION-oracle.x86_64/bin/javac

    echo ; echo ": Building and installing FOROS UI from $SVNPATH"
    cd $CHECKOUT_FOLDER
    docl mvn install -Dmaven.test.skip.exec=true

    result=0
    echo ; echo ": Testing rs-client"
    echo "Command: $CHECKOUT_FOLDER/mvn install --projects rs-client -am -amd -P RsClient"
    cd $CHECKOUT_FOLDER
      pushd rs-client
        mvn install -am -amd
      popd
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
  local database="$3" # empty to use a default one

  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host "; exit 1; }
  [ -z "$branch" ] && { echo ": $FUNCNAME: undefined branch "; exit 1; }

  execute_remote $host $branch $database <<-"EOF"
    #!/bin/bash
    WORKING_DIR=`pwd`
    . $WORKING_DIR/commons.sh

    UI_BRANCH=$1
    DATABASE=$2
    [ -z "$UI_BRANCH" ] && { echo ": ui_unittest: undefined UI_BRANCH" ; exit 1 ; }

    M2_REPO=$HOME/.m2-`basename $UI_BRANCH`
    echo ": ui_unittest: ui branch: $UI_BRANCH ; m2 repo: $M2_REPO"

    svn_export_folder "svn+ssh://svn/home/svnroot/oix/ui/$UI_BRANCH" || exit 1
    cd $CHECKOUT_FOLDER

    if [ -z "$DATABASE" ] ; then
      # do not check unit test database for dev branches
      echo "$UI_BRANCH" | grep -q "branches/dev/"
      if [ "$?" = "1" ] ; then
        doc get_latests_branch 1 "svn+ssh://svn/home/svnroot/oix/ui/branches" && TEST_BRANCH=$LATESTS_BRANCH
        doc get_latests_branch 2 "svn+ssh://svn/home/svnroot/oix/ui/branches" && EME_BRANCH=$LATESTS_BRANCH
        get_ancestor "svn+ssh://svn/home/svnroot/oix/ui/$UI_BRANCH"
        UNITTEST_UI_DB="unittest_ui_12"
        [ "$SVN_ANCESTOR_NAME" = "$TEST_BRANCH" ] && UNITTEST_UI_DB="unittest_ui_11"
        [ "$SVN_ANCESTOR_NAME" = "$EME_BRANCH" ] && UNITTEST_UI_DB="unittest_ui_10"

        cat ui-ejb/src/test/resources/test.properties | grep "jdbc:postgresql" | grep -q $UNITTEST_UI_DB
        [ "$?" != "0" ] && { echo ": ui_unitest: svn+ssh://svn/home/svnroot/oix/ui/$UI_BRANCH/ui-ejb/src/test/resources/test.properties should use $UNITTEST_UI_DB" ; exit 1 ; }
      fi
    else
      dbname=`cat ui-ejb/src/test/resources/test.properties | grep "jdbc:postgresql" | head -n 1 | cut -d \/ -f 4`
      doc sed -i 's/$dbname/$DATABASE/g' ui-ejb/src/test/resources/test.properties
    fi

    JAVA_VERSION="1.7.0"
    if is_svn_branch_ge "ui" "$UI_BRANCH" "3.6.0" ; then JAVA_VERSION="1.8.0" ; fi
    export JAVA_HOME=/usr/lib/jvm/java-$JAVA_VERSION

    # Uncomment with https://jira.ocslab.com/browse/OUI-28307
    # echo ; echo ": Check JSP compile"
    # docl mvn -Dmaven.repo.local=$M2_REPO -Dmaven.test.skip.exec=true install -P PrecompileJsp

    export MAVEN_OPTS="-XX:MaxPermSize=256m"
    echo "==================================="
    printenv | sort
    echo "==================================="
    echo ": Start unit tests"
    doc cat ui-ejb/src/test/resources/test.properties
    echo
    
    mvn -q -Dmaven.repo.local=$M2_REPO install
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
  return $result
}

ui_build_ear() {
  local svnpath="$1"
  local outputfile="$2"
  local target="$3"

  [ -z "$svnpath" ] && { echo ": $FUNCNAME: undefined svnpath "; exit 1; }
  [ -z "$outputfile" ] && { echo ": $FUNCNAME: undefined outputfile "; exit 1; }
  [ -z "$target" ] && target="el6"

  local java_version="1.7"
  if is_svn_branch_ge "ui" "$svnpath" "3.6.0"; then
    java_version="1.8"
  fi

  local centos_release=""
  if [ "${target%%[0-9]}" = "el" ]; then
    abt_setup_custom_build_mock_config "oix/ui" $target "foros_ui_ear_custombuild_${java_version}"
    centos_release="$target"
  else
    abt_setup_custom_build_mock_config_with_host "oix/ui" $target "foros_ui_ear_custombuild_${java_version}"
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
    "'cd /builddir/ui; mvn install -B -Dmaven.test.skip.exec=true -Dmaven.repo.local=/builddir/m2'"

  echo ": $FUNCNAME: coping files to chroot"
  doc mock_copyout $mock_config_dir $mock_config "/builddir/ui/ui-ear/target/foros-ui.ear" "$outputfile"

  echo ": $FUNCNAME: deleting mock"
  doc mock_clean $mock_config_dir $mock_config
  echo ": $FUNCNAME: done"
  return 0
}

ui_custom_build() {
  local ui_svnpath="$1"
  local unixcommons_svnpath="$2"
  local hosts="$3"

  [ -z "$ui_svnpath" ] && { echo ": $FUNCNAME: undefined ui_svnpath"; exit 1; }
  [ -z "$unixcommons_svnpath" ] && { echo ": $FUNCNAME: undefined unixcommons_svnpath "; exit 1; }
  [ -z "$hosts" ] && { echo ": $FUNCNAME: undefined hosts "; exit 1; }

  # Get CentOS version from the host
  abt_setup_custom_build_mock_config_with_host "oix/ui" $hosts
  local centos_release="$CENTOS_RELEASE"

  local notbuild=""
  local unixcommons="-A unixcommons unixcommons/$unixcommons_svnpath"
  if echo $notBuild | grep -q "unixcommons"; then
    get_centos_release "oui-nbmaster0"
    if [ "X$CENTOS_RELEASE" = "X$centos_release" ]; then
      unixcommons="-a unixcommons unixcommons/trunk"
      notbuild="unixcommons"
    fi
  fi

  echo ": $FUNCNAME: notBuild: $notbuild"
  export notBuild="$notbuild"

  abt_custom_build_with_cache "oix/ui" "foros-ui" \
    -A "oix/ui oix/ui/$ui_svnpath" "$unixcommons" \
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
