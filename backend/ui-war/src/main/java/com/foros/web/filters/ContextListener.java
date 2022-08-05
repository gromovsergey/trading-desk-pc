package com.foros.web.filters;

import com.foros.model.ApproveStatus;
import com.foros.model.VersionHelper;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ContextListener implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(ContextListener.class.getName());
    private ServletContext context = null;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        context = event.getServletContext();

        logBuildInformation();
        prepareContext();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        context = event.getServletContext();

        clearContext();
    }

    private void prepareContext() {
        context.setAttribute("approveStatuses", ApproveStatus.values());
        context.setAttribute(VersionHelper.VERSION_PROPERTY, VersionHelper.getVersion());
        context.setAttribute(VersionHelper.TIMESTAMP_PROPERTY, VersionHelper.getBuildTimestamp());
    }

    private void clearContext() {
        context.removeAttribute("approveStatuses");
        context.removeAttribute(VersionHelper.VERSION_PROPERTY);
        context.removeAttribute(VersionHelper.TIMESTAMP_PROPERTY);
    }

    private void logBuildInformation() {
        logger.log(Level.INFO, "FOROS Version: {0}", new Object[] {VersionHelper.getVersion()});
    }
}
