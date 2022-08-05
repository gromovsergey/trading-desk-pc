package com.foros.birt.web.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import org.eclipse.birt.report.IBirtConstants;
import org.eclipse.birt.report.listener.ViewerHttpSessionListener;

public class AuditViewerHttpSessionListener extends ViewerHttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        session.setAttribute(IBirtConstants.TASK_MAP, new AuditTaskMap());
    }
}
