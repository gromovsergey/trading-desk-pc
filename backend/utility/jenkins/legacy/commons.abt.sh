#!/bin/bash
abt_clean_prev_data() {
  local host=$1
  [ -z "$host" ] && { echo "Undefined host" ; exit 1 ; }

  echo ": $FUNCNAME: Cleaning previous custom build's data"
  [[ -f /etc/abt/.abtrc ]] && . /etc/abt/.abtrc
  [[ -f ~/.abtrc ]] && . ~/.abtrc
  [ -z "workDir" ] && { echo "Undefined $HOME:/.abtrc#workDir" ; exit 1 ; }
  find $workDir -maxdepth 1 -name '*-5000\.5\.5*' -type d -ctime +30 -exec -rm -r "{}" \;
  find /home/maint/RPM/SOURCES -maxdepth 1 -name '*-5000\.5\.5*' -type f -ctime +30 -delete
}

abt_get_nb_version() {
  local timestamp=`date +%Y%m%d%H%M`
  local ver1=`echo $timestamp | cut -c1-4`
  local ver2=`echo $timestamp | cut -c5-6`
  local ver3=`echo $timestamp | cut -c7-8`
  local ver4=`echo $timestamp | cut -c9-12`

  echo "$ver1.$ver2.$ver3.$ver4"
}

abt_get_cb_version() {
  local version="5000.5.5.`date +%Y%m%d%H%M%S`"
  echo $version
}

abt_build_trunk() {
  local project="$1"; shift
  local version="$1"; shift
  local args="$@"

  [ -z "$version" ] && { echo ": $FUNCNAME: undefined version"; exit 1; }
  [ "X`expr match $version '\([0-9]\+\.[0-9]\+\.[0-9]\+\.[0-9]\+\)'`" != "X$version" ] && \
    { echo ": $FUNCNAME: invalid version '$version'"; exit 1; }
  [ -z "$project" ] && { echo ": $FUNCNAME: undefined project"; exit 1; }

  local cmd="abt -d 3 -v \"$version\" -s \"20 60 70\" -b trunk $args $project"
  local logfile="$WORKING_DIR/${project//\//_}_build.log"
  local buildboxStorageDir="/tmp/OUI-COMMON/repo/nb/${project##oix/}"

  doc mkdir -p $buildboxStorageDir
  export buildboxStorageDir

  echo ": $FUNCNAME: $cmd"
  echo ": $FUNCNAME: see log: $logfile"
  eval $cmd >> $logfile 2>&1 || { tail -n 200 $logfile ; exit 1 ; }
  echo ": $FUNCNAME: done"; echo
}

abt_setup_custom_build_mock_config_with_host() {
  local project="$1"
  local target_host="$2"
  local name="$3"

  [ -z "$project" ] && { echo ": $FUNCNAME: undefined project"; exit 1; }
  [ -z "$target_host" ] && { echo ": $FUNCNAME: undefined traget host"; exit 1; }
  [ -z "$name" ] && name="foros_ui_custombuild"

  get_centos_release $target_host
  abt_setup_custom_build_mock_config "${project}" "${CENTOS_RELEASE}" "${name}"
}

abt_setup_custom_build_mock_config() {
  local project="$1"
  local centos_release="$2"
  local name="$3"

  [ -z "$project" ] && { echo ": $FUNCNAME: undefined project"; exit 1; }
  [ -z "$centos_release" ] && centos_release="el6"
  [ -z "$name" ] && name="foros_ui_custombuild"

  local mock_config="${project##oix/}_${name}_${centos_release}_${BAMBOO_MOCK_NO:-any}"
  local mock_config_file="/home/maint/mock/cfg/$mock_config-x86_64.cfg"

  doc checkout_file \
    "svn+ssh://svn/home/svnroot/oix/ui/trunk/utility/bamboo/mock/${centos_release}/${name}.cfg"

  docl "cp $CHECKOUT_FILE $mock_config_file"
  sed -i -e "s|config_opts\['root'\] = 'trunk|config_opts['root'] = 'trunk-${BAMBOO_MOCK_NO:-any}|" $mock_config_file

  echo ": $FUNCNAME: mock config = $mock_config"
  ABT_MOCK_CONFIG=$mock_config
  ABT_MOCK_CONFIG_FILE=$mock_config_file
}

abt_copy_rpms_to_cache() {
  local src="$1"; shift
  local dst="$1"; shift
  local branches="$@"

  [ ! -d "$src" ] && { echo ": $FUNCNAME: src - '$src' is not directory"; exit 1; }
  [ ! -d "$dst" ] && { echo ": $FUNCNAME: dst - '$dst' is not directory"; exit 1; }

  echo ": $FUNCNAME: updating cache"
  ABT_RPMS=""
  local package=""
  for package in `find $src -name '*.rpm' -type f ! -name '*.src.rpm'`; do
    local name=`rpm -qp --qf '%{name}' $package`
    local version=`rpm -qp --qf '%{version}' $package`
    local basename=`basename $package`
    cp -v $package $dst && \
      cache_save_package_version "$name" "$version" $branches || \
      { echo ": $FUNCNAME: could not save package $package"; exit 1; }
    ABT_RPMS="$ABT_RPMS $dst/$basename"
  done
  echo ": $FUNCNAME: done"
}

abt_get_branches() {
  local branches=""
  local OPTIND opt
  while getopts ":A:a:F:D:m:c:" opt; do
    case "${opt}" in
        A)  branches="${branches} ${OPTARG##* }"
            ;;
        F)  branches="${branches} ${OPTARG}"
            ;;
        *)
            ;;
    esac
  done
  shift $((OPTIND-1))
  echo $branches
}

abt_get_args() {
  local args=""
  local OPTIND opt
  while getopts ":A:a:D:m:F:c:" opt; do
    case "${opt}" in
        A)  args="$args -A '${OPTARG}'"
            ;;
        a)  args="$args -A '${OPTARG}'"
            ;;
        D)  args="$args -D '${OPTARG}'"
            ;;
        m)  args="$args -m ${OPTARG}"
            ;;
        c)  args="$args -c ${OPTARG}"
            ;;
        *)
            ;;
    esac
  done
  shift $((OPTIND-1))
  echo $args
}

# args:
#  project, e.g.: oix/ui
#  version, e.g.: 5000.1.1.1
#  other, e.g.: -A 'oix/ui oix/ui/trunk' - wile be passed to abt
# returns:
#  ABT_RPMS - builded packages
abt_custom_build() {
  local project="$1"; shift
  local version="$1"; shift

  local branches=`abt_get_branches "$@"`
  local args=`abt_get_args "$@"`

  [ -z "$version" ] && { echo ": $FUNCNAME: undefined version"; exit 1; }
  [ "X`expr match $version '\([0-9]\+\.[0-9]\+\.[0-9]\+\.[0-9]\+\)'`" != "X$version" ] && \
    { echo ": $FUNCNAME: invalid version '$version'"; exit 1; }
  [ -z "$project" ] && { echo ": $FUNCNAME: undefined project"; exit 1; }

  local cmd="abt -d 3 -v \"$version\" -s \"20 60 70\" -b custom -r custom-build $args $project"
  local logfile="$WORKING_DIR/${project//\//_}_custom_build_${BAMBOO_MOCK_NO:-any}.log"
  local storageDir="/tmp/OUI-COMMON/repo/custom/${project##oix/}"
  doc mkdir -p -m 777 /tmp/oix
  local tempStorageDir=`mktemp -d /tmp/${project##oix/}-custom-build.XXXXXXXXX`

  echo "checkpoint 1: tempStorageDir = $tempStorageDir"
  echo "mktemp -d /tmp/${project##oix/}-custom-build.XXXXXXXXX"

  doc mkdir -p $storageDir
  export buildboxStorageDir=${tempStorageDir}

  echo ": $FUNCNAME: branches: $branches"
  echo ": $FUNCNAME: cache: $storageDir($tempStorageDir)"

  echo ": $FUNCNAME: $cmd"
  echo ": $FUNCNAME: see log: $logfile"
  eval $cmd >> $logfile 2>&1 || { tail -n 200 $logfile ; rm -rf $tempStorageDir; exit 1 ; }
  abt_copy_rpms_to_cache "$tempStorageDir" "$storageDir" "$branches"

  echo ": $FUNCNAME: done"

  rm -rf ${tempStorageDir}
}

# args:
#  project, e.g.: oix/ui
#  expected_package, e.g.: oix-ui - this name will be checked in cache
#  other, e.g.: -A 'oix/ui oix/ui/trunk' - wile be passed to abt
# returns:
#  ABT_VERSION - pacakges version
#  ABT_RPMS - builded packages
abt_custom_build_with_cache() {
  local project="$1"; shift
  local expected_package="$1"; shift

  [ -z "$expected_package" ] && { echo ": $FUNCNAME: undefined expected_package"; exit 1; }
  [ -z "$project" ] && { echo ": $FUNCNAME: undefined project"; exit 1; }

  echo ": $FUNCNAME: $@"

  local branches=`abt_get_branches "$@"`
  local version=""
  local buildboxStorageDir="/tmp/OUI-COMMON/repo/custom/${project##oix/}"
  local cached_version=`cache_get_package_version "$expected_package" $branches`
  local cached_package=""
  [ -n "$cached_version" ] && cached_package=`repo_get_packages -r local "${expected_package}-${cached_version}*.rpm"`
  if [ "$?" != "0" ] ; then
    version=`abt_get_cb_version`
    abt_custom_build $project $version "$@"
  else
    echo ": $FUNCNAME: package '$cached_package' found"
    version=`rpm -qp --qf '%{version}' $cached_package`
  fi
  ABT_VERSION=$version

  echo "checkpoint 2: ABT_VERSION=$version"
  pwd
  echo "version=rpm -qp --qf '%{version}' $cached_package"
  echo "cached_version=cache_get_package_version "$expected_package" $branches"
}

abt_get_custom_build_version_file() {
  local project="$1"
  [ -z "$project" ] && { echo ": $FUNCNAME: undefined project"; exit 1; }

  local file="/tmp/OUI-COMMON/${project##oix/}-custom-build-${BAMBOO_MOCK_NO:-any}.version" 
  mkdir -p `dirname "$file"`>/dev/null 2>/dev/null
  echo "$file"
}

abt_get_custom_build_version() {
  local project="$1"
  [ -z "$project" ] && { echo ": $FUNCNAME: undefined project"; exit 1; }
  local filename=`abt_get_custom_build_version_file $project`
  cat $filename
}

abt_save_custom_build_version() {
  local project="$1"
  local version="$2"
  [ -z "$project" ] && { echo ": $FUNCNAME: undefined project"; exit 1; }
  [ -z "$version" ] && { echo ": $FUNCNAME: undefined version"; exit 1; }


  echo -n $version > `abt_get_custom_build_version_file $project`
  return 0
}
