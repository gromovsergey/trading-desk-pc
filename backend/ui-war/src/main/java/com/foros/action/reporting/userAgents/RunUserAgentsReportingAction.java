package com.foros.action.reporting.userAgents;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.reporting.userAgents.UserAgentsReportParameters;
import com.foros.session.reporting.userAgents.UserAgentsReportService;

import javax.ejb.EJB;

public class RunUserAgentsReportingAction extends RunReportingActionSupport<UserAgentsReportParameters> {

    @EJB
    private UserAgentsReportService reportsService;

    @ReadOnly
    public String execute() {
        return safelyExecuteGeneric(reportsService, "userAgents");
    }
}
