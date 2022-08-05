package com.foros.session.fileman;

import java.io.File;
import java.util.List;

public class FileTypesRestrictionImpl implements FileTypesRestriction {
    public static final FileTypesRestriction INSTANCE = new FileTypesRestrictionImpl();

    @Override
    public boolean check(File file, List<String> allowedFileTypes) {
        String mimeTypeByExtension = FileUtils.getMimeTypeByExtension(file.getName());
        String mimeTypeByContent = FileUtils.getMimeTypeByMagic(file);
        if (!mimeTypeByExtension.equals(mimeTypeByContent)) {
            return false;
        }

        List<String> allowedMimeTypes = FileUtils.fileTypesToMimeTypes(allowedFileTypes);
        return allowedMimeTypes.contains(mimeTypeByExtension);
    }
}
