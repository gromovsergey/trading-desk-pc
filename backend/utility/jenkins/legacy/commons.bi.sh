#!/bin/bash

### Set PENTAHO_CONNECTION
get_pentaho_connection() {
  local db_name=$1
  [ -z "$db_name" ] && { echo ": $FUNCNAME: undefined db_name" ; exit 1 ; }
  if [ "$db_name" = "skip" ]; then
    PENTAHO_CONNECTION="skip"
    return 0
  fi

  PENTAHO_CONNECTION="moscow-dev-ui-${db_name}"
}

bi_install_analisys_datasources() {
  local host="$1"
  local connection="$2"

  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host"; exit 1; }
  [ -z "$connection" ] && { echo ": $FUNCNAME: undefined connection"; exit 1; }
  [ "$connection" = "skip" ] && { echo ": $FUNCNAME: skipped"; return 0; }

  doc ssh -o BatchMode=yes bi@$host -- \
    "/opt/foros/manager/bin/cmgr -f foros-bi InstallAnalisysDatasources connection=$connection"
}

bi_custom_build() {
  local svnpath="$1"
  local target="$2"

  [ -z "$svnpath" ] && { echo ": $FUNCNAME: undefined svnpath"; exit 1; }
  [ -z "$target" ] && target="el6"
  local centos_release=""

  if [ "${target%%[0-9]}" = "el" ]; then
    abt_setup_custom_build_mock_config "oix/bi" $target
    centos_release="$target"
  else
    abt_setup_custom_build_mock_config_with_host "oix/bi" $target
    centos_release="$CENTOS_RELEASE"
  fi
  abt_custom_build_with_cache "oix/bi" "foros-bi-pgdb" \
    -A "oix/bi oix/bi/$svnpath" \
    -A "oix/db oix/db/trunk" \
    -F "fake/centos:$centos_release" \
    -D "%skipAnalysisDatasources true" \
    -m "$ABT_MOCK_CONFIG"
  abt_save_custom_build_version "oix/bi" "$ABT_VERSION"
}

bi_update_pgdb() {
  local host="$1"
  local version="$2"

  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host"; exit 1; }
  [ "$host" = "skip" ] && { echo ": $FUNCNAME: skipped"; return 0; }
  [ -z "$version" ] && { echo ": $FUNCNAME: undefined version"; exit 1; }

  echo ": $FUNCNAME: installing foros-bi-pgdb-$version to $host"

  doc install_packages "$host" "foros-bi-pgdb-${version}*.rpm"
}

bi_update_ui() {
  local connection="$1"
  local version="$2"
  local pentaho_host="$3"

  [ -z "$connection" ] && { echo ": $FUNCNAME: undefined connection"; exit 1; }
  [ "$connection" = "skip" ] && { echo ": $FUNCNAME: skipped"; return 0; }
  [ -z "$version" ] && { echo ": $FUNCNAME: undefined version"; exit 1; }
  [ -z "$pentaho_host" ] && { echo ": $FUNCNAME: undefined pentaho_host"; exit 1; }

  echo ": $FUNCNAME: installing foros-bi-mondrian-$connection-$version to $pentaho_host"

  doc install_packages "$pentaho_host" "foros-bi-mondrian-${connection}-${version}*.rpm"
}

bi_install_patches() {
  local host="$1"
  [ -z "$host" ] && { echo ": $FUNCNAME: undefined host"; exit 1; }
  [ "$host" = "skip" ] && { echo ": $FUNCNAME: skipped"; return 0; }

  doc ssh -o BatchMode=yes uiuser@$host -- "/opt/foros/manager/bin/cmgr -f pgdb bi start"
}

bi_make_reports() {
  local svnpath="$1"
  local dst="$2"
  local connection="$3"

  local host="oix-dev1"

  [ -z "$svnpath" ] && { echo ": $FUNCNAME: undefined svnpath "; exit 1; }
  [ -z "$dst" ] && { echo ": $FUNCNAME: undefined dst "; exit 1; }
  [ -z "$connection" ] && connection="production-uk-central"

  execute_remote_as "maint" $host $svnpath $connection <<-"EOF"
    SVNPATH="$1"
    CONNECTION="$2"
    [ -z "$SVNPATH" ] && { echo ": bi_make_reports: undefined SVNPATH" ; exit 1 ; }
    [ -z "$CONNECTION" ] && { echo ": bi_make_reports: undefined CONNECTION" ; exit 1 ; }

    WORKING_DIR=$(cd `dirname $0`; pwd)
    . $WORKING_DIR/commons.svn.sh

    svn_export_folder "svn+ssh://svn.ocslab.com/home/svnroot/oix/bi/$SVNPATH"

    pushd $CHECKOUT_FOLDER
    ./make_reports.sh -c $CONNECTION
    popd
    if [ ! -d $CHECKOUT_FOLDER/target/$CONNECTION ]; then
      echo ": bi_make_reports: could not find target directory in $CHECKOUT_FOLDER"
      exit 1
    fi
    cp -r $CHECKOUT_FOLDER/target/$CONNECTION $WORKING_DIR || exit 1
    cp -r $CHECKOUT_FOLDER/target-predefined/$CONNECTION $WORKING_DIR/$CONNECTION-predefined || exit 1
    exit 0
EOF
  [ "$?" != "0" ] && return 1
  rm -rf $dst/reports
  rm -rf $dst/reports-predefined
  doc scp -r $host:/$WORKING_DIR/$connection $dst/reports
  doc scp -r $host:/$WORKING_DIR/$connection-predefined $dst/reports-predefined
  return 0
}

bi_generate_reports_pages() {
  local reportsdir="$1"
  local outputdir="$2"

  [ -z "$reportsdir" ] && { echo ": $FUNCNAME: undefined reportsdir "; exit 1; }
  [ -z "$outputdir" ] && { echo ": $FUNCNAME: undefined outputdir "; exit 1; }

  local reports=`find $reportsdir -name '*.xml'`
  doc rm -rf $outputdir
  doc mkdir -p $outputdir

  doc svn_export_folder svn+ssh://svn/home/svnroot/oix/ui/trunk/utility/bamboo/foros-bi/xslt

  local report
  for report in $reports; do
    local name=`basename $report`
    local dir=`dirname $report`
    local prefix=`basename $dir`
    xsltproc -o $outputdir/$prefix-$name $CHECKOUT_FOLDER/bischema.xslt $report || return 1
  done
}

bi_generate_predefined_reports_pages() {
  local reportsdir="$1"
  local outputdir="$2"

  [ -z "$reportsdir" ] && { echo ": $FUNCNAME: undefined reportsdir "; exit 1; }
  [ -z "$outputdir" ] && { echo ": $FUNCNAME: undefined outputdir "; exit 1; }

  local reports=`find $reportsdir -name '*.prpt'`
  doc rm -rf $outputdir
  doc mkdir -p $outputdir

  doc svn_export_folder svn+ssh://svn/home/svnroot/oix/ui/trunk/utility/bamboo/foros-bi/xslt

  local report
  for report in $reports; do
    local name=`basename $report`
    local dir=`dirname $report`
    mkdir -p $WORKING_DIR/pr/${name%.*}/
    local tempdir=`mktemp -d $WORKING_DIR/pr.XXXXXXXX`
    doc unzip $report layout.xml datasources/sql-ds.xml datadefinition.xml -d $tempdir

    local layout=$tempdir/layout.xml
    local datadefinition=$tempdir/datadefinition.xml
    local sqlds=$tempdir/datasources/sql-ds.xml

    echo "<h2>Predefined report: $name</h2><p>Path: /public/predefined/$name</p>" > $outputdir/w-pr-${name%.*}.html
    xsltproc $CHECKOUT_FOLDER/layout.xslt $layout >> $outputdir/w-pr-${name%.*}.html || return 1
    xsltproc $CHECKOUT_FOLDER/datadefinition.xslt $datadefinition >> $outputdir/w-pr-${name%.*}.html || return 1
    xsltproc $CHECKOUT_FOLDER/sqlds.xslt $sqlds >> $outputdir/w-pr-${name%.*}.html || return 1
    rm -rf $tempdir
  done

  echo "<h1>Predefined reports</h1>" >  $outputdir/w-predefined-reports.html
  for filename in $outputdir/*; do
    cat $filename >> $outputdir/w-predefined-reports.html
  done
}

bi_collect_pages() {
  local pagedir="$1"
  local version="$2"
  local outputfile="$3"

  [ -z "$pagedir" ] && { echo ": $FUNCNAME: undefined pagedir "; exit 1; }
  [ -z "$version" ] && { echo ": $FUNCNAME: undefined version "; exit 1; }
  [ -z "$outputfile" ] && { echo ": $FUNCNAME: undefined outputfile "; exit 1; }

  version=`echo $version | tr -d ' .,'`

  echo '<h1><ac:macro ac:name="toc"/></h1>' > $outputfile
  for filename in $pagedir/*; do
    cat $filename >> $outputfile
  done

  sed -i -e 's|ac--|ac:|g' $outputfile
  sed -i -e 's|&lt;|<|g' -e 's|&gt;|>|g' $outputfile
  sed -i -e "s|#VERSION#|$version|g" $outputfile
}

bi_get_package_list() {
  local version=""
  local flag=""
  local colo=""
  version=`product_get_package_list_version $@`
  [ "$?" != "0" ] && { echo ": $FUNCNAME: could not parse version" >&2; exit 1; }
  colo=`product_get_package_list_colo $@`
  [ "$?" != "0" ] && { echo ": $FUNCNAME: could not parse colo" >&2; exit 1; }
  flag=`product_get_package_list_flag $@`
  [ "$?" != "0" ] && { echo ": $FUNCNAME: could not parse flags" >&2; exit 1; }

  echo ": $FUNCNAME: colo=$colo, version=$version, flag=$flag" >&2
  if [ -z "$colo" ] || echo $flag | grep -q S; then
    local binary="foros-bi-pgdb"
  else
    local binary="foros-bi-mondrian-${colo}"
  fi
  [ ! -z "$version" ] && binary="${binary}-${version}"

  if echo $flag | grep -q i; then
    echo "${binary}*.rpm"
  else
    echo "${binary}"
  fi
}
