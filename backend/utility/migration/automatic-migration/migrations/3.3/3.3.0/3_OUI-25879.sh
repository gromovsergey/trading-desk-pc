#!/bin/bash

creativePreviewFolder=`/opt/foros/ui/bin/get_colocation_property.sh oui_preview_folder`

echo "Removing creative previews from folder $creativePreviewFolder"

rm -rf ${creativePreviewFolder}/*
