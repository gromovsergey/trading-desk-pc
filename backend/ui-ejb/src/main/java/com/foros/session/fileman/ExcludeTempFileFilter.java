package com.foros.session.fileman;

import com.foros.model.template.OptionValueUtils;

import java.io.File;
import java.io.FileFilter;

public class ExcludeTempFileFilter implements FileFilter {

    public boolean accept(File pathname) {
        if (SharedFileOutputStream.isTempFileName(pathname.getName()) ||
                pathname.getName().equals(OptionValueUtils.IMAGE_RESIZED_FOLDER) ||
                pathname.getName().equals(OptionValueUtils.HTML_FOLDER)) {
            return false;
        }
        return true;
    }
}
