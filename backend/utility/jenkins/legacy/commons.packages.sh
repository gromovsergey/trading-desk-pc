#!/bin/bash

. $WORKING_DIR/commons.packages.repo.sh
. $WORKING_DIR/commons.packages.cache.sh
. $WORKING_DIR/commons.packages.utils.sh

YUM_LOCK_FILENAME="/tmp/OUI-COMMON/yum.lock"
YUM_LOCK_TIMEOUT="600"  # 10 minutes

uninstall_packages() {
  local host=$1; shift
  local names="$@"

  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host" ; exit 1 ; }
  [ -z "$names" ] && return

  execute_remote_ex `whoami` $host "*.sh" $names <<-"EOF"
    WORKING_DIR=`pwd`
    . $WORKING_DIR/commons.sh
    NAMES="$@"

    echo ": uninstall_packages: $NAMES"
    (
      flock -x -w $YUM_LOCK_TIMEOUT 200
      BEFORE=`rpm -qa | grep foros | sort`
      sudo yum -y remove $NAMES || { echo "$BEFORE"; exit 1; }
      AFTER=`rpm -qa | grep foros | sort`
      diff -u -d --label before --label after <(echo "$BEFORE") <(echo "$AFTER")
      echo
    ) 200>$YUM_LOCK_FILENAME
EOF
  [ "$?" != "0" ] && exit 1
  return 0
}

install_foros_packages() {
  local host="$1"; shift
  local repo=`repo_parse_args_get_repo_name $@`
  local packages=`repo_parse_args_get_packages $@`

  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host "; exit 1; }
  [ -z "$packages" ] && { echo ": $FUNCNAME: undefined packages "; exit 1; }
  [ -z "$repo" ] && repo="any"

  local package=""
  local dst=`mktemp -d $WORKING_DIR/rpms.XXXXXXXX`
  repo_collect_packages $dst -r $repo $packages
  [ "$?" != "0" ] && { echo ": $FUNCNAME: not found" >&2; return 1; }

  local output=`mktemp $WORKING_DIR/install.XXXXXXX`

  execute_remote_ex `whoami` $host "*.sh `basename $dst`" $dst 2>&1 <<-"EOF" |
    WORKING_DIR=`pwd`
    . $WORKING_DIR/commons.sh
    FOLDER="$1"

    echo ": install_foros_packages: `ls $FOLDER`"
    (
      flock -x -w $YUM_LOCK_TIMEOUT 200
      BEFORE=`rpm -qa | grep foros | sort`
      sudo yum -y clean metadata && \\
        sudo yum -y install $FOLDER/*.rpm || { echo "$BEFORE"; exit 1; }
      AFTER=`rpm -qa | grep foros | sort`
      diff -u -d --label before --label after <(echo "$BEFORE") <(echo "$AFTER")
      echo
    ) 200>$YUM_LOCK_FILENAME
    exit $?
EOF
  tee -a $output
  local result="${PIPESTATUS[0]}"
  if [ "$result" != "0" ]; then
    result="2"
    cat $output | grep -q 'Error: Nothing to do' && result=0
    cat $output | grep -qE 'No package .* available' && result=1
  fi
  return $result
}

install_system_packages() {
  local host="$1"; shift
  local packages="$@"

  local output=`mktemp $WORKING_DIR/install.XXXXXXX`

  execute_remote_ex `whoami` $host "*.sh" $packages 2>&1 <<-"EOF" |
    WORKING_DIR=`pwd`
    . $WORKING_DIR/commons.sh
    PACKAGES="$@"

    echo ": install_foros_packages: $PACKAGES"
    (
      flock -x -w $YUM_LOCK_TIMEOUT 200
      sudo yum -y clean metadata && \\
        sudo yum -y install $PACKAGES || exit 1
      echo
    ) 200>$YUM_LOCK_FILENAME
    exit $?
EOF
  tee -a $output
  local result="${PIPESTATUS[0]}"
  if [ "$result" != "0" ]; then
    result="2"
    cat $output | grep -q 'Error: Nothing to do' && result=0
    cat $output | grep -qE 'No package .* available' && result=1
  fi
  return $result
}

install_packages() {
  local host="$1"; shift
  local repo=`repo_parse_args_get_repo_name $@`
  local packages=`repo_parse_args_get_packages $@`

  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host "; exit 1; }
  [ -z "$packages" ] && { echo ": $FUNCNAME: undefined packages "; exit 1; }
  [ -z "$repo" ] && repo="any"

  local package=""
  local foros_packages=""
  local system_packages=""
  for package in $packages; do
    if basename $package | grep -qE '^foros'; then
      foros_packages="$foros_packages $package"
    else
      system_packages="$system_packages $package"
    fi
  done

  [ -z "$foros_packages" ] || doc install_foros_packages $host -r $repo $foros_packages
  [ -z "$system_packages" ] || doc install_system_packages $host $system_packages
  return 0
}

update_package() {
  local package="$1"; shift
  local hosts="$@"

  [ -z "$package" ] && { echo ": $FUNCNAME: undefined package" ; exit 1 ; }
  [ -z "$hosts" ] && { echo ": $FUNCNAME: undefined hosts" ; exit 1 ; }

  local host
  for host in $hosts ; do
    install_system_packages $host $package
    local result="$?"
    if [ "$result" != "0" ]; then
      return $result
    fi
  done
  return 0
}

update_product() {
  echo ": $FUNCNAME: $@"
  local product="$1"; shift
  local colo="$1"; shift
  local hosts="$1"; shift
  local args=$@

  [ -z "$product" ] && { echo ": $FUNCNAME: undefined product "; exit 1; }
  [ -z "$colo" ] && { echo ": $FUNCNAME: undefined colo "; exit 1; }
  [ -z "$hosts" ] && { echo ": $FUNCNAME: undefined hosts "; exit 1; }

  [ "X`type -t ${product}_get_package_list`" = "Xfunction" ] || \
    { echo ": $FUNCNAME: ${product}_get_package_list is not a function or undefined"; exit 1; }

  local packages=`eval ${product}_get_package_list -c $colo $args`
  [ -z "$packages" ] && { echo ": $FUNCNAME: undefined packages "; exit 1; }

  local repo=`repo_get_repo_by_colo $colo`
  [ -z "$repo" ] && { echo ": $FUNCNAME: could not define repository for $colo"; exit 1; }

  [ "$repo" = "test" -a "$product" = "biadm" ] && repo="test6"
  [ "$repo" = "test" -a "$product" = "bi" ] && repo="test6"

  echo ": $FUNCNAME: ${product} on colo ${colo}: ${hosts}"
  echo "${packages}"

  local host=""
  for host in $hosts; do
    install_packages $host -r $repo $packages
  done
}

is_package_installed() {
  local host="$1"
  local package="$2"
  local version="$3"

  local installed_version="`ssh -o BatchMode=yes $host -- rpm -q --qf '%{version}' $package 2>/dev/null`"
  [ "X$version" = "X$installed_version" ]
}

get_package_ui_branch_from_cache_file() {
  local version="$1"
  local cache_file="/tmp/OUI-COMMON/packages.cache"

  CACHE_PACKAGE_UI_BRANCH=`grep "foros-ui $version" $cache_file | cut -d ' ' -f 3  | cut -d ':' -f 1 | tail -n 1`
}

find_product_packages() {
  local package="$1"
  local version="$2"
  local centos_release="$3"
  local host="repo"

  [ -z "$centos_release" ] && centos_release="el6"

  # it is special case due to foros-datastore src package
  # does not provide foros-datastore binary package
  if [ "$package" = "foros-datastore-hdfs-load" ]; then
    package="foros-datastore"
  fi

  echo ": $FUNCNAME: looking for package $package-${version} in all repositories"

  PRODUCT_PACKAGES=""
  execute_remote $host $package $version $centos_release <<-"EOF"

  WORKING_DIR=`pwd`
  . $WORKING_DIR/commons.sh

  PACKAGE="$1"
  VERSION="$2"
  CENTOS_RELEASE="$3"

  SRCPATH=`find /u01/ks/repos -name "$PACKAGE-$VERSION-*$CENTOS_RELEASE*.src.rpm" | head -n 1`
  [ -z "$SRCPATH" ] && { echo ": find_product_packages: Source package $PACKAGE-$VERSION-*$CENTOS_RELEASE*.src.rpm not found"; exit 1; }

  rpm2cpio $SRCPATH | cpio -d -i $PACKAGE.spec
  SPECFILE="$PACKAGE.spec"
  [ ! -e "$SPECFILE" ] && { echo ": find_product_packages: Cound not extract spec"; exit 1; }

  PATTERN=`python -c 'import rpm, sys, re; \\
    s=rpm.spec(sys.argv[1]); \\
    print " -or ".join(map(lambda x: "-name " + re.sub(r"%{_.+?}", "*", x.header["nevra"] + ".rpm", re.S), s.packages))' $SPECFILE`
  [ -z "$PATTERN" ] && { echo ": find_product_packages: Cound not create search pattern"; exit 1; }

  echo ": find_product_packages: $PATTERN"
  find /u01/ks/repos $PATTERN | \\
    python -c 'import sys; \\
      pairs = map(lambda x:(x,x.split("/")[-1]), sys.stdin); acc = set(); \\
      reduce(lambda x,y: sys.stdout.write(y[0]) if y[1] not in acc and not acc.add(y[1]) else x, pairs, "")' | \\
    tee $PACKAGE.list

  [ -s $PACKAGE.list ] || { echo ": find_product_packages: No package found"; exit 1; }
  exit 0
EOF
  local result=$?
  [ "$result" != "0" ] && return $result

  scp $host:/$WORKING_DIR/$package.list $WORKING_DIR/$package.list || return 1
  PRODUCT_PACKAGES=`cat $package.list`
  return 0
}

copy_packages_to_repo() {
  local repo="$1"; shift
  local centos_release="$1"; shift
  local packages="$@"
  local host="repo"
  local path="/u01/ks/repos/$centos_release/RPMS.$repo"

  echo ": $FUNCNAME: copy packages $packages to $path"
  ssh -o 'BatchMode yes' $host -- cp -f -v -t $path $packages || return 1
  ssh -o 'BatchMode yes' $host -- sudo -Hiu rpm /bin/createrepo -q --update --unique-md-filenames --pretty --skip-stat $path
  return $?
}

downgrade_packages() {
  local host="$1"; shift
  local packages="$@"

  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host "; exit 1; }
  [ -z "$packages" ] && { echo ": $FUNCNAME: undefined packages "; exit 1; }

  echo ": $FUNCNAME: downgrade packages $packages on $host"
  ssh -o 'BatchMode yes' $host -- sudo yum -y downgrade $packages
  [ "$?" != "0" ] && exit 1
}

what_requires_package() {
  local host="$1"
  local package="$2"

  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host "; exit 1; }
  [ -z "$package" ] && { echo ": $FUNCNAME: undefined package "; exit 1; }

  local output=""
  output=`ssh -o 'BatchMode yes' $host -- rpm -q --whatrequires $package`
  [ "$?" != "0" ] && exit 1
  if echo "$output" | grep -q 'no package requires'; then
    return 0
  fi
  echo "$output"
  return 0
}
