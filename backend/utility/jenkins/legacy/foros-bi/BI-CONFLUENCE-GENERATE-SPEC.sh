#!/bin/bash

WORKING_DIR=`pwd`
. $WORKING_DIR/commons.sh
. $WORKING_DIR/commons.bi.sh
. $WORKING_DIR/commons.confluence.sh

CONFLUENCE_SPACE="BI"
CONFLUENCE_ROOT_PAGE_TITLE=${bamboo__1_ConfluenceRootPageTitle}
CONFLUENCE_BASE_URL="https://confluence.ocslab.com"
CONFLUENCE_USERNAME=${bamboo__2_Confluence_username}
CONFLUENCE_PASSWORD=${bamboo__3_Confluence_password}
BI_VERSION=${bamboo__4_BiVersion}

[ -z "$CONFLUENCE_ROOT_PAGE_TITLE" ] && { echo "Undefined ROOT_PAGE_TITLE"; exit 1; }
[ -z "$CONFLUENCE_USERNAME" ] && { echo "Undefined USERNAME"; exit 1; }
[ -z "$CONFLUENCE_PASSWORD" ] && { echo "Undefined PASSWORD"; exit 1; }
[ -z "$BI_VERSION" ] && { echo "Undefined BI_VERSION"; exit 1; }


get_svn_path $BI_VERSION
BI_SVNPATH=$SVN_PATH

doc bi_make_reports $SVN_PATH $WORKING_DIR
doc bi_generate_reports_pages $WORKING_DIR/reports $WORKING_DIR/pages
doc bi_generate_predefined_reports_pages $WORKING_DIR/reports-predefined $WORKING_DIR/pages-predefined
doc cp $WORKING_DIR/pages-predefined/w-predefined-reports.html $WORKING_DIR/pages
doc bi_collect_pages $WORKING_DIR/pages $BI_VERSION $WORKING_DIR/result.html
save_artifacts "html" $WORKING_DIR/result.html
confluence_store_page \
  "$CONFLUENCE_USERNAME" "$CONFLUENCE_PASSWORD" \
  "$CONFLUENCE_SPACE" "BI Specification, version $BI_VERSION" \
  "$CONFLUENCE_ROOT_PAGE_TITLE" --file "$WORKING_DIR/result.html" --noConvert
