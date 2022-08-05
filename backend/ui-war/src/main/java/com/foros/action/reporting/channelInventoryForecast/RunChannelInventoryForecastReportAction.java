package com.foros.action.reporting.channelInventoryForecast;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.reporting.channelInventoryForecast.ChannelInventoryForecastReportParameters;
import com.foros.session.reporting.channelInventoryForecast.ChannelInventoryForecastReportService;

import javax.ejb.EJB;

public class RunChannelInventoryForecastReportAction
        extends RunReportingActionSupport<ChannelInventoryForecastReportParameters> {
    @EJB
    private ChannelInventoryForecastReportService reportService;

    @ReadOnly
    public String execute() {
        return safelyExecuteGeneric(reportService, "channelInventoryForecast");
    }

}
