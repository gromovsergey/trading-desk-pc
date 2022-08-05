package com.foros.action.reporting.profiling;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.profiling.ProfilingReportParameters;
import com.foros.session.reporting.profiling.ProfilingReportService;

import javax.ejb.EJB;

public class RunProfilingReportingAction extends RunReportingActionSupport<ProfilingReportParameters> {

    @EJB
    private ProfilingReportService reportsService;

    @Override
    @ReadOnly
    @Restrict(restriction = "Report.AdvancedISPReports.run", parameters = "#target.model.accountId")
    public String execute() {
        return safelyExecuteGeneric(reportsService, ReportType.PROFILING.getName());
    }
}


