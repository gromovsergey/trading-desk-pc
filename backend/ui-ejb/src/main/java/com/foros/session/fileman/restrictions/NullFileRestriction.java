package com.foros.session.fileman.restrictions;

import com.foros.session.fileman.PathProvider;

import java.io.File;
import java.io.IOException;

public class NullFileRestriction implements FileRestriction {
    public static final FileRestriction INSTANCE = new NullFileRestriction();

    public void checkNewFolderAllowed(Quota quota, PathProvider pathProvider, File file) throws IOException {
    }

    public void checkNewFileAllowed(Quota quota, PathProvider pathProvider, File dir) {
    }
}
