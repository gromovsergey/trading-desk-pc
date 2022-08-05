package com.foros.action.reporting.referrer;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.reporting.referrer.ReferrerReportParameters;
import com.foros.session.reporting.referrer.ReferrerReportService;

import javax.ejb.EJB;

public class RunReferrerReportingAction extends RunReportingActionSupport<ReferrerReportParameters> {

    @EJB
    private ReferrerReportService reportsService;

    @ReadOnly
    public String execute() {
        return safelyExecuteGeneric(reportsService, "referrer");
    }
}
