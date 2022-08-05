package com.foros.session.fileman.audit;

import com.foros.changes.inspection.ChangeType;

import java.io.File;

public interface FileSystemAudit {
    Long getAuditObjectId(File file);
    boolean isAuditable(File file);
    void add(ChangeType changeType, File file);
    void log();
}
