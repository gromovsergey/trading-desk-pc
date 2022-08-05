package com.foros.session.fileman.restrictions;

import com.foros.session.fileman.PathProvider;

import java.io.File;
import java.io.IOException;

public class CompositeFileRestriction implements FileRestriction {
    private FileRestriction[] restrictions;

    public CompositeFileRestriction(FileRestriction... restrictions) {
        this.restrictions = restrictions == null ? new FileRestriction[0] : restrictions;
    }

    public void checkNewFolderAllowed(Quota quota, PathProvider pathProvider, File file) throws IOException {
        for (FileRestriction restriction : restrictions) {
            restriction.checkNewFolderAllowed(quota, pathProvider, file);
        }
    }

    public void checkNewFileAllowed(Quota quota, PathProvider pathProvider, File file) throws IOException {
        for (FileRestriction restriction : restrictions) {
            restriction.checkNewFileAllowed(quota, pathProvider, file);
        }
    }
}
