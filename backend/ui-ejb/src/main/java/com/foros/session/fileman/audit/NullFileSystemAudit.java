package com.foros.session.fileman.audit;

import com.foros.changes.inspection.ChangeType;

import java.io.File;

public class NullFileSystemAudit implements FileSystemAudit {
    public static final FileSystemAudit INSTANCE = new NullFileSystemAudit();

    @Override
    public Long getAuditObjectId(File path) {
        return null;
    }

    @Override
    public boolean isAuditable(File file) {
        return false;
    }

    @Override
    public void add(ChangeType changeType, File file) {
    }

    @Override
    public void log() {
    }
}
