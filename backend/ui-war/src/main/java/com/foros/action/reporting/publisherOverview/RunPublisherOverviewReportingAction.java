package com.foros.action.reporting.publisherOverview;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.publisherOverview.PublisherOverviewReportParameters;
import com.foros.session.reporting.publisherOverview.PublisherOverviewReportService;

import javax.ejb.EJB;

public class RunPublisherOverviewReportingAction extends RunReportingActionSupport<PublisherOverviewReportParameters> {

    @EJB
    private PublisherOverviewReportService reportsService;

    @Override
    @ReadOnly
    @Restrict(restriction = "Report.AdvancedISPReports.run", parameters = "#target.model.accountId")
    public String execute() {
        return safelyExecuteGeneric(reportsService, ReportType.PUBLISHER_OVERVIEW.getName());
    }
}

