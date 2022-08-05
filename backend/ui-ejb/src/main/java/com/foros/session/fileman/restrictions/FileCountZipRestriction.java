package com.foros.session.fileman.restrictions;

import com.foros.session.fileman.TooManyEntriesZipException;

import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.io.InputStream;
import java.io.IOException;

public class FileCountZipRestriction implements ZipRestriction {
    private int maxFilesCount;

    public FileCountZipRestriction(int maxFilesCount) {
        this.maxFilesCount = maxFilesCount;
    }

    public ZipInputStream create(InputStream is) {
        return new RestrictedZipInputStream(is, maxFilesCount);
    }

    private static class RestrictedZipInputStream extends ZipInputStream {
        private int maxFilesCount;
        private int counter = 0;

        public RestrictedZipInputStream(InputStream in, int maxFilesCount) {
            super(in);
            this.maxFilesCount = maxFilesCount;
        }

        @Override
        public ZipEntry getNextEntry() throws IOException {
            ZipEntry entry = super.getNextEntry();
            if (entry != null) {
                counter++;
            }
            if (counter > maxFilesCount) {
                throw new TooManyEntriesZipException("Number of files in the archive more than " + maxFilesCount);
            }
            return entry;
        }
    }
}
