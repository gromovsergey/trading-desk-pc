package com.foros.session.fileman.audit;

import com.foros.changes.inspection.ChangeType;
import com.foros.session.security.AuditService;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

public class FileSystemAuditImpl implements FileSystemAudit {

    private String rootPath;
    private Long auditObjectId;
    private Set<String> added = new TreeSet<>();
    private Set<String> updated = new TreeSet<>();
    private Set<String> removed = new TreeSet<>();

    private AuditService auditService;

    public FileSystemAuditImpl(AuditService auditService, String rootPath, Long auditObjectId) {
        this.auditService = auditService;
        this.rootPath = rootPath;
        this.auditObjectId = auditObjectId;
    }

    @Override
    public Long getAuditObjectId(File file) {
        return isAuditable(file) ? auditObjectId : null;
    }

    @Override
    public boolean isAuditable(File file) {
        String path = file.getAbsolutePath();
        return isAuditable(path);
    }

    private boolean isAuditable(String path) {
        return path.startsWith(rootPath);
    }

    @Override
    public void add(ChangeType changeType, File file) {

        if (changeType == ChangeType.UNCHANGED) {
            return;
        }

        String path = file.getAbsolutePath();

        if (!isAuditable(path)) {
            return;
        }

        String relativePath = path.substring(rootPath.length());

        switch (changeType) {
            case ADD:
                added.add(relativePath);
                break;
            case UPDATE:
                updated.add(relativePath);
                break;
            case REMOVE:
                removed.add(relativePath);
                break;
        }
    }

    @Override
    public void log() {
        if (added.isEmpty() && updated.isEmpty() && removed.isEmpty()) {
            return;
        }

        auditService.logFileSystem(auditObjectId, added, updated, removed);

        added.clear();
        updated.clear();
        removed.clear();
    }
}
