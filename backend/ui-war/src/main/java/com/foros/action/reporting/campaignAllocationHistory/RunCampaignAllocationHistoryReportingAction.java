package com.foros.action.reporting.campaignAllocationHistory;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.reporting.campaignAllocationHistory.CampaignAllocationHistoryReportService;

import javax.ejb.EJB;

public class RunCampaignAllocationHistoryReportingAction extends RunReportingActionSupport<Long> {

    @EJB
    private CampaignAllocationHistoryReportService reportsService;

    @ReadOnly
    public String execute() {
        return safelyExecuteGeneric(reportsService, "campaignAllocationHistory");
    }

    public void setCampaignId(Long id) {
        parameters = id;
    }

    @Override
    protected void initParameters() {
    }
}
