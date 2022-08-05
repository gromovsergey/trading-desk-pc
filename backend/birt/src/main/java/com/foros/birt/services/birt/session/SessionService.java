package com.foros.birt.services.birt.session;

import com.foros.birt.services.birt.SynchronizationReportSessionService;
import com.foros.model.report.birt.BirtReportSession;
import com.foros.session.birt.BirtReportService;

import javax.servlet.http.HttpServletRequest;
import org.eclipse.birt.report.utility.ParameterAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    @Autowired
    private BirtReportService reportService;

    @Autowired
    private SynchronizationReportSessionService synchronizationReportSessionService;

    public CustomSession getSession(BirtReportSession reportSession, HttpServletRequest request) {
        CustomSession session = getSessionFromRequest(request);

        if (session != null) {
            if (checkSession(session, reportSession)) {
                return session;
            }
        }

        session = createSession(reportSession);

        storeSessionToRequest(session, request);

        return session;
    }

    private boolean checkSession(CustomSession sessionFromRequest, BirtReportSession reportSession) {
        return sessionFromRequest.getReportSession().getId().equals(reportSession.getId());
    }

    private CustomSession createSession(BirtReportSession reportSession) {
        return new CustomSession(reportSession, reportService, synchronizationReportSessionService);
    }

    private CustomSession getSessionFromRequest(HttpServletRequest request) {
        return (CustomSession) request.getAttribute(ParameterAccessor.ATTR_VIEWING_SESSION);
    }

    private void storeSessionToRequest(CustomSession customSession, HttpServletRequest request) {
        request.setAttribute(ParameterAccessor.ATTR_VIEWING_SESSION, customSession);
    }

}
