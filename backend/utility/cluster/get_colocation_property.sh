#!/bin/bash

COLOCATION_PROPERTIES_FILE="/opt/foros/ui/lib/domains/domain1/config/colocation.properties"
if ! [ -f "$COLOCATION_PROPERTIES_FILE" ] ; then
  echo "Failed to find '$COLOCATION_PROPERTIES_FILE' file"
  exit 1
fi

cat "$COLOCATION_PROPERTIES_FILE" | grep "^$1=" | sed -e "s/$1=//g" 2>/dev/null

