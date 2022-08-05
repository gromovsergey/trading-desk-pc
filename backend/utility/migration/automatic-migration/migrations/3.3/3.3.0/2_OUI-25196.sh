#!/bin/bash

echo "Remove image previews"

creativesFolder=`/opt/foros/ui/bin/get_colocation_property.sh oui_creatives_folder`
textAdImagesFolder=`/opt/foros/ui/bin/get_colocation_property.sh oui_textad_images_folder`
rm -rf ${creativesFolder}/*/*/${textAdImagesFolder}/~resized/*
rm -rf ${creativesFolder}/*/${textAdImagesFolder}/~resized/*

echo "Create new image previews"

/opt/foros/ui/bin/run-java-migration.sh com.foros.migration.UpdateImagePreviews
