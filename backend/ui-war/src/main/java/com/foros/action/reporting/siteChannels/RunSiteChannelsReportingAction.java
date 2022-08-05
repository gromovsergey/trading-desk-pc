package com.foros.action.reporting.siteChannels;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.reporting.siteChannels.SiteChannelsReportParameters;
import com.foros.session.reporting.siteChannels.SiteChannelsReportService;

import javax.ejb.EJB;

public class RunSiteChannelsReportingAction extends RunReportingActionSupport<SiteChannelsReportParameters> {
    @EJB
    private SiteChannelsReportService reportsService;

    @ReadOnly
    public String execute() {
        return safelyExecuteGeneric(reportsService, "siteChannels");
    }
}
