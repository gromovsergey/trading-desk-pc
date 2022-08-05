#!/bin/bash

set -e

process_properties_file() {
    ls "$1" >/dev/null
    IFS="
"
    for exportCmd in `egrep -hv "^#|^[^[:print:][:blank:]]*$" "$1" | sed "s/[.]/$DOT_PLACEHOLDER/g;" | sed "s/-/$DASH_PLACEHOLDER/g;" | sed -re 's/([[:alnum:][:blank:][:punct:]]+).*/export \"\1\"/g;'`; do
        eval "$exportCmd"
    done
}

USAGE="Required parameter not found!
USAGE: $0 <profile name> <name to search for value>"

PROFILE=$1
if [ -z "$PROFILE" ]; then
    echo $USAGE>&2
    exit 1
fi

NAME_TO_SEARCH=$2
if [ -z "$NAME_TO_SEARCH" ]; then
    echo $USAGE>&2
    exit 1
fi

DOT_PLACEHOLDER="DOT_START0DOT_END"
DASH_PLACEHOLDER="DASH_START0DASH_END"

CURRENT_LOCATION=`dirname "$0"`
process_properties_file "$CURRENT_LOCATION/../../src/main/resources/application.properties"
process_properties_file "$CURRENT_LOCATION/../../conf/application-$PROFILE.properties"

TMP=`echo "$NAME_TO_SEARCH" | sed "s/[.]/$DOT_PLACEHOLDER/g;"`
eval echo "\${$TMP}" | sed "s/$DOT_PLACEHOLDER/./g;" | sed "s/$DASH_PLACEHOLDER/-/g;"
