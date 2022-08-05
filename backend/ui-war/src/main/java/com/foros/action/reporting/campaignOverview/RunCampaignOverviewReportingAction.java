package com.foros.action.reporting.campaignOverview;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.campaignOverview.CampaignOverviewReportParameters;
import com.foros.session.reporting.campaignOverview.CampaignOverviewReportService;

import javax.ejb.EJB;

public class RunCampaignOverviewReportingAction extends RunReportingActionSupport<CampaignOverviewReportParameters> {

    @EJB
    private CampaignOverviewReportService reportsService;

    @Override
    @ReadOnly
    @Restrict(restriction = "Report.AdvancedISPReports.run", parameters = "#target.model.accountId")
    public String execute() {
        return safelyExecuteGeneric(reportsService, ReportType.CAMPAIGN_OVERVIEW.getName());
    }
}

