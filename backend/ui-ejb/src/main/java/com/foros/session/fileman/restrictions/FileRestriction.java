package com.foros.session.fileman.restrictions;

import com.foros.session.fileman.PathProvider;

import java.io.File;
import java.io.IOException;

public interface FileRestriction {
    void checkNewFolderAllowed(Quota quota, PathProvider pathProvider, File file) throws IOException;
    void checkNewFileAllowed(Quota quota, PathProvider pathProvider, File file) throws IOException;
}
