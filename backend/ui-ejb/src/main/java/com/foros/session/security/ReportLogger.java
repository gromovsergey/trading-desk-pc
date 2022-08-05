package com.foros.session.security;

import com.foros.session.ServiceLocator;

public class ReportLogger {
    private ReportRunTO to;
    private AuditService auditService;
    private long start;
    private long end;
    private Long auditId;

    public ReportLogger() {
        this.auditService = ServiceLocator.getInstance().lookup(AuditService.class);
    }

    public ReportLogger logStart(ReportRunTO to) {
        this.to = to;
        this.to.setExecutionTime(null);
        this.to.setRowsCount(null);
        this.to.setSize(null);
        this.to.clearError();

        this.start = System.currentTimeMillis();
        this.auditId = auditService.logReportStarted(to);

        return this;
    }

    public void logSuccess(Number rowsCount, Number size) {
        to.setRowsCount(rowsCount == null ? null : rowsCount.longValue());
        to.setSize(size == null ? null : size.longValue());
        logEnd();
    }

    public void logFailure(String errorMessage) {
        if (to == null || auditId == null) {
            // logStart didn't called (something was broken before it)
            return;
        }

        to.setErrorMessage(errorMessage);
        logEnd();
    }

    private void logEnd() {
        end = System.currentTimeMillis();
        to.setExecutionTime(end - start);
        auditService.logReportFinished(auditId, to);
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public long getExecutionTime() {
        return end - start;
    }
}
