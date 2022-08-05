#!/bin/sh

dir=`dirname $0`
absdir=`cd "$dir"; pwd`

lastdir=""

if [ "X$1" != "X" ] 
then
  local_repo="-Dmaven.repo.local=$1"
else
  local_repo="-Dmaven.repo.local=~/.m2"
fi

MIGRATIONS_DIR="$absdir/migrations"
TARGET_PREFIX="$absdir/target/migrations"

mkdir -p $TARGET_PREFIX

find "$MIGRATIONS_DIR"/* -not -regex '.*/\.svn.*' | while read entry
do
    relname="${entry:${#MIGRATIONS_DIR}+1}"
    basename=`basename "$entry"`

    target="${TARGET_PREFIX}/${relname}"
    targetdir="`dirname $target`"

    if [ ! -z "$lastdir" ]
    then
        # entry starts with lastdir
        if [ "${entry#${lastdir}}/" != "${entry}/" ]
        then
            continue
        else
            lastdir=""
        fi
    fi

    # Entry is file
    if [ -f "$entry" ]
    then
        [ -d "$targetdir" ] || mkdir "$targetdir"
        cp "$entry" "$target"
        [[ "$basename" =~ ^[1-9][\.0-9]*_ ]] && chmod a+x "$target"
    fi

    # Entry is directory
    if [ -d $entry ]
    then
        if [ -f "$entry/pom.xml" ]
        then
            mkdir "$target"
            
            pushd "$entry"
            mvn -DoutputDir="$target" package $local_repo || exit 1
            popd

            lastdir="$entry"
        else
            mkdir $target
        fi
    fi
done
