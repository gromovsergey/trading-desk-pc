package com.foros.session.birt;

import com.foros.model.report.birt.BirtReportInstance;
import com.foros.model.report.birt.BirtReportSession;
import com.foros.timer.AbstractTimedBean;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timer;

@LocalBean
@Startup
@Singleton
public class BirtReportSessionCleaner extends AbstractTimedBean {

    public static final long CLEARING_INTERVAL = 60L * 1000L;

    @EJB
    private BirtReportService birtReportService;

    @PostConstruct
    public void init() {
        startTimer("custom-report-session-cleaner", CLEARING_INTERVAL);
    }

    public void cleanExpiredSessions() {
        BirtReportService.InstancesAndSessions instancesAndSessions = birtReportService.expireInstancesAndSessions();

        for (BirtReportInstance instance : instancesAndSessions.getInstances()) {
            birtReportService.clearInstance(instance);
        }

        for (BirtReportSession session : instancesAndSessions.getSessions()) {
            birtReportService.clearSession(session);
        }
    }

    @Override
    protected void proceed(Timer timer) {
        cleanExpiredSessions();
    }
}
