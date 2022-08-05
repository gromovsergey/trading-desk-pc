package com.foros.action.reporting.channelInventoryForecast;

import com.foros.action.reporting.CancellablePageReportingAction;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.session.reporting.channelInventoryForecast.ChannelInventoryForecastReportParameters;

import com.opensymphony.xwork2.ModelDriven;
import org.springframework.beans.factory.annotation.Autowired;

public class CancellableChannelInventoryForecastReportAction
        extends CancellablePageReportingAction implements ModelDriven<ChannelInventoryForecastReportParameters> {

    private ChannelInventoryForecastReportParameters parameters = new ChannelInventoryForecastReportParameters();

    @Autowired
    private SearchChannelService searchChannelService;

    public String getChannelName() {
        String name = null;
        if (parameters.getChannelIds().size() == 1) {
            Long channelId = parameters.getChannelIds().iterator().next();
            name = searchChannelService.find(channelId).getName();
        }
        return name;
    }

    @Override
    public ChannelInventoryForecastReportParameters getModel() {
        return parameters;
    }
}
