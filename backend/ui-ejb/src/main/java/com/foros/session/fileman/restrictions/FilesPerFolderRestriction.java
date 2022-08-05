package com.foros.session.fileman.restrictions;

import com.foros.session.fileman.AccountSizeExceededException;
import com.foros.session.fileman.ExcludeTempFileFilter;
import com.foros.session.fileman.PathProvider;

import java.io.File;
import java.io.IOException;

public class FilesPerFolderRestriction implements FileRestriction {
    private int maxFilesPerFolder;

    public FilesPerFolderRestriction(int maxFilesPerFolder) {
        this.maxFilesPerFolder = maxFilesPerFolder;
    }

    public void checkNewFolderAllowed(Quota quota, PathProvider pathProvider, File file) throws IOException {
        check(file);
    }

    public void checkNewFileAllowed(Quota quota, PathProvider pathProvider, File file) throws IOException {
        check(file);
    }

    private void check(File file) {
        if (file.exists()) {
            return;
        }

        checkFolders(file.getParentFile());
    }

    private void checkFolders(File folder) {
        if (folder.exists()) {
            int count = folder.listFiles(new ExcludeTempFileFilter()).length;

            if (count >= maxFilesPerFolder) {
                throw new AccountSizeExceededException("Number of files more than " + maxFilesPerFolder);
            }
        } else {
            checkFolders(folder.getParentFile());
        }
    }
}
