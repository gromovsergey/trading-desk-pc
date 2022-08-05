package com.foros.session.fileman.restrictions;

import com.foros.session.fileman.PathProvider;
import com.foros.session.fileman.TooManyDirLevelsException;

import java.io.File;
import java.io.IOException;

public class FileDepthRestriction implements FileRestriction {
    private int maxDepth;
    private PathProvider rootPathProvider;

    public FileDepthRestriction(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public FileDepthRestriction(int maxDepth, PathProvider rootPathProvider) {
        this.maxDepth = maxDepth;
        this.rootPathProvider = rootPathProvider;
    }

    public void checkNewFolderAllowed(Quota quota, PathProvider pathProvider, File file) throws IOException {
        File root;
        if (rootPathProvider != null) {
            root = rootPathProvider.getPath("");
        } else {
            root = pathProvider.getPath("");
        }

        int depth = calculateDepth(file, root);
        if (depth > maxDepth) {
            throw new TooManyDirLevelsException("To many dir levels: " + depth);
        }
    }

    public void checkNewFileAllowed(Quota quota, PathProvider pathProvider, File file) throws IOException {
        checkNewFolderAllowed(quota, pathProvider, file.getParentFile());
    }

    private int calculateDepth(File file, File root) {
        int depth = 0;
        File current = file;
        while (!root.equals(current)) {
            current = current.getParentFile();
            depth++;
        }
        return depth;
    }
}
