#!/bin/bash

set -e

HELP_REQ=`echo " $@ " | egrep '[[:blank:]]-h[[:blank:]]|[[:blank:]]-?-?help[[:blank:]]' ||:`
if [ -n "$HELP_REQ" ]; then
    echo "Usage: $0 [clean]
    W/o <clean> param the utility generates -sources and -javadoc jars with 0 size
    With <clean> param deletes these files
    All work will done recursively from current dir"
    exit 0
fi

PARAM=$1

if [ "$PARAM" == "clean" ]; then
    for jar in `find -L ./ -type f -size 0 | egrep "[.]jar$"`; do
        rm -f "$jar"
    done
    exit 0
fi

for jar in `find -L ./ -type f | egrep "[.]jar$" | egrep -v "\-sources[.]jar$|\-javadoc[.]jar$" | sed -re 's/(.*?)[.]jar/\1/'`; do
    touch "$jar-sources.jar"
    touch "$jar-javadoc.jar"
done
