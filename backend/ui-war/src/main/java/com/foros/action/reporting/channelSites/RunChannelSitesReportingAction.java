package com.foros.action.reporting.channelSites;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.reporting.channelSites.ChannelSitesReportParameters;
import com.foros.session.reporting.channelSites.ChannelSitesReportService;

import javax.ejb.EJB;

public class RunChannelSitesReportingAction extends RunReportingActionSupport<ChannelSitesReportParameters> {
    @EJB
    private ChannelSitesReportService reportsService;

    @ReadOnly
    public String execute() {
        return safelyExecuteGeneric(reportsService, "channelSites");
    }
}
