#!/bin/bash

TMP_DIR=~/tmp/check-oui-eol-styles-$$
ALL_FILES_LIST=$TMP_DIR/all-files-info.log
PROJECT_DIR=`dirname $0`/../../../

NEED_HELP=`echo "$1" | grep -i help`
if [ "X$NEED_HELP" != "X" ]; then
    echo "Usage: $0 [oui project location]
              default location is '$PROJECT_DIR'"
    exit 0
fi

if [ -d "$1" ]; then
    PROJECT_DIR="$1"
fi

mkdir "$TMP_DIR"

svn pg svn:eol-style `find -L "$PROJECT_DIR" -type f | egrep -v '\.svn|\.png$|\.zip$|\.ai$|/abt|server-startup/ui|ui\-test'` >"$ALL_FILES_LIST"
if [ "$?" != "0" ]; then
    echo "Fix errors and run '$0' again. (Note: Files are not under version control must be REMOVED OR COMMITTED.)"
    rm -rf "$TMP_DIR"
    exit 1
fi

echo
echo "Searching '$PROJECT_DIR' for incorrect EOL styles"
echo

echo "Files with unexpected EOL style:"
cat "$ALL_FILES_LIST" | grep -v native | grep -v LF
echo

echo "Files that must have LF style, but they don't:"
cat "$ALL_FILES_LIST" | egrep '\.sql |\.sh |\.sh\.templ |\.py |\.spec ' | grep -v LF
echo

echo "Files that must have \"native\" style, but they don't:"
cat "$ALL_FILES_LIST" | egrep -v '\.sql |\.sh |\.sh\.templ |\.py |\.spec ' | grep -v native
echo

rm -rf "$TMP_DIR"
