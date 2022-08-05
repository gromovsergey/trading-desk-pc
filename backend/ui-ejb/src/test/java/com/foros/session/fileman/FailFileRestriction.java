package com.foros.session.fileman;

import com.foros.session.fileman.restrictions.FileRestriction;
import com.foros.session.fileman.restrictions.Quota;

import java.io.File;
import java.io.IOException;

public class FailFileRestriction implements FileRestriction {

    public void checkNewFolderAllowed(Quota quota, PathProvider pathProvider, File file) throws IOException {
        throw new TooManyDirLevelsException("fail");
    }

    public void checkNewFileAllowed(Quota quota, PathProvider pathProvider, File dir) {
        throw new TooManyDirLevelsException("fail");
    }
}
