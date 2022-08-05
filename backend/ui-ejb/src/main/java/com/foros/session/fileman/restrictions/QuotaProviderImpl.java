package com.foros.session.fileman.restrictions;

import com.foros.session.fileman.ExcludeTempFileFilter;
import com.foros.session.fileman.PathProvider;
import org.apache.commons.io.DirectoryWalker;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;

public class QuotaProviderImpl implements QuotaProvider {
    private long maxFolderSize;
    private PathProvider rootPathProvider;

    public QuotaProviderImpl(long maxFolderSize) {
        this.maxFolderSize = maxFolderSize;
    }

    public QuotaProviderImpl(long maxFolderSize, PathProvider rootPathProvider) {
        this.maxFolderSize = maxFolderSize;
        this.rootPathProvider = rootPathProvider;
    }

    public Quota get(PathProvider pathProvider) throws IOException {
        Counter counter = new Counter(new ExcludeTempFileFilter());
        if (rootPathProvider != null) {
            counter.count(rootPathProvider.getPath(""));
        } else {
            counter.count(pathProvider.getPath(""));
        }
        Quota quota = new Quota();
        quota.setFileSizeAvailable(maxFolderSize - counter.getSize());
        return quota;
    }

    private static class Counter extends DirectoryWalker {
        private long size = 0;

        public Counter(FileFilter fileFilter) {
            super(fileFilter, -1);
        }

        public long getSize() {
            return size;
        }

        @Override
        protected void handleFile(File file, int depth, Collection results) throws IOException {
            size += file.length();
        }

        public void count(File dir) throws IOException {
            walk(dir, new ArrayList());
        }
    }
}
