package com.foros.session.fileman;

import com.foros.changes.inspection.ChangeType;
import com.foros.session.fileman.audit.FileSystemAudit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

public class SharedFileOutputStream extends FileOutputStream {
    private final static String PREFIX = ".";
    private final static String SUFFIX = "~forostmp";

    private File originalFile;
    private FileSystemAudit fileSystemAudit;
    private File tempFile;
    private boolean commit = false;

    public SharedFileOutputStream(File originalFile, File tempFile, FileSystemAudit fileSystemAudit) throws FileNotFoundException {
        super(tempFile);

        this.originalFile = originalFile;
        this.tempFile = tempFile;
        this.fileSystemAudit = fileSystemAudit;
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();

            if (commit && tempFile.exists()) {
                ChangeType changeType = getChangeType();
                if (originalFile.exists()) {
                    originalFile.delete();
                }

                if (!tempFile.renameTo(originalFile)) {
                    throw new IOException();
                }

                fileSystemAudit.add(changeType, originalFile);
            }
        } finally {
            tempFile.delete();
        }
    }

    private ChangeType getChangeType() throws IOException {
        if (!fileSystemAudit.isAuditable(originalFile)) {
            return ChangeType.UNCHANGED;
        }

        if (!originalFile.exists()) {
            return ChangeType.ADD;
        }

        return FileUtils.contentEquals(tempFile, originalFile) ? ChangeType.UNCHANGED : ChangeType.UPDATE;
    }

    public void commitOnClose() {
        this.commit = true;
    }

    public static boolean isTempFileName(String name) {
        if (name == null) {
            throw new NullPointerException("File name is not set");
        }
        return name.startsWith(PREFIX) && name.endsWith(SUFFIX);
    }

    public static File createTempFile(File file) {
        String name = UUID.randomUUID().toString();
        File tempFile = new File(file.getParentFile(), PREFIX + name + SUFFIX);
        return tempFile;
    }
}
