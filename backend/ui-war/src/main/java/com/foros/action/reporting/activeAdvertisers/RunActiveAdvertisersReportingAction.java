package com.foros.action.reporting.activeAdvertisers;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.activeAdvertisers.ActiveAdvertisersReportParameters;
import com.foros.session.reporting.activeAdvertisers.ActiveAdvertisersReportService;

import javax.ejb.EJB;

public class RunActiveAdvertisersReportingAction extends RunReportingActionSupport<ActiveAdvertisersReportParameters> {

    @EJB
    private ActiveAdvertisersReportService reportsService;

    @Override
    @ReadOnly
    @Restrict(restriction = "Report.AdvancedISPReports.run", parameters = "#target.model.accountId")
    public String execute() {
        return safelyExecuteGeneric(reportsService, ReportType.ACTIVE_ADVERTISERS.getName());
    }
}
