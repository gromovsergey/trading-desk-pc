package com.foros.action.channel;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.channel.Channel;
import com.foros.restriction.RestrictionService;
import com.foros.session.admin.CurrencyConverter;
import com.foros.session.channel.ChannelStatsTO;
import com.foros.session.channel.service.SearchChannelService;

import javax.ejb.EJB;

public class ShowChannelStatsAction extends BaseActionSupport {

    @EJB
    protected RestrictionService restrictionService;

    @EJB
    protected SearchChannelService searchChannelService;

    private Long id;

    private Channel channel;
    private ChannelStatsTO channelStatistic;
    private CurrencyConverter currencyExchangeRate;

    @ReadOnly
    public String loadChannelStats() {
        channel = searchChannelService.find(id);
        if (restrictionService.isPermitted("AdvertisingChannel.viewStats", channel)) {
            channelStatistic = searchChannelService.findChannelStatistics(channel.getId());
            currencyExchangeRate = ChannelHelper.getCurrencyConverterForStats(channel);
        }

        return SUCCESS;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Channel getChannel() {
        return channel;
    }

    public ChannelStatsTO getChannelStatistic() {
        return channelStatistic;
    }

    public CurrencyConverter getCurrencyExchangeRate() {
        return currencyExchangeRate;
    }
}
