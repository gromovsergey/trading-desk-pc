package com.foros.action.reporting.isp;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.reporting.isp.ISPReportParameters;
import com.foros.session.reporting.isp.ISPReportService;

import javax.ejb.EJB;

public class RunISPReportingAction extends RunReportingActionSupport<ISPReportParameters> {

    @EJB
    private ISPReportService reportsService;

    @ReadOnly
    public String execute() {
        return safelyExecuteGeneric(reportsService, "isp");
    }
}
