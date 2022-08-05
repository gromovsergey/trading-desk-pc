package com.foros.birt.services.birt.session;

import com.foros.birt.services.birt.SynchronizationReportSessionService;
import com.foros.model.report.birt.BirtReportSession;
import com.foros.model.report.birt.BirtReportSessionState;
import com.foros.session.birt.BirtReportService;

import java.util.Date;
import org.eclipse.birt.report.session.IViewingSession;

public class CustomSession implements IViewingSession {

    private BirtReportSession reportSession;
    private final BirtReportService reportService;
    private final SynchronizationReportSessionService synchronizationReportSessionService;

    public CustomSession(
            BirtReportSession reportSession,
            BirtReportService reportService,
            SynchronizationReportSessionService synchronizationReportSessionService) {
        this.reportSession = reportSession;
        this.reportService = reportService;
        this.synchronizationReportSessionService = synchronizationReportSessionService;
    }

    @Override
    public String getId() {
        return reportSession.getSessionId();
    }

    public BirtReportSession getReportSession() {
        return reportSession;
    }

    @Override
    public Date getLastAccess() {
        return new Date();
    }

    @Override
    public void refresh() {
        // todo
    }

    @Override
    public String getCachedReportDocument(String reportFile, String viewerId) {
        return reportSession.getBirtReportInstance() != null ?
                reportService.getReportDocumentAbsoluteFileName(reportSession.getBirtReportInstance()) : null;
    }

    @Override
    public String getImageTempFolder() {
        return reportService.getReportImagesFolder(reportSession);
    }

    @Override
    public boolean isExpired() {
        return reportSession.getState() == BirtReportSessionState.EXPIRED;
    }

    @Override
    public void invalidate() {
        reportService.setSessionExpired(reportSession.getId());
    }

    @Override
    public void lock() {
        synchronizationReportSessionService.lock(reportSession.getId());
    }

    @Override
    public void unlock() {
        synchronizationReportSessionService.unlock(reportSession.getId());
    }

    @Override
    public boolean isLocked() {
        return synchronizationReportSessionService.isLocked(reportSession.getId());
    }
}
