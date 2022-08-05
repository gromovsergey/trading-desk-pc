package com.foros.action.channel;

import static com.foros.util.DateHelper.getRelativeDate;

import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.restriction.RestrictionService;
import com.foros.session.admin.CurrencyConverter;
import com.foros.session.channel.ChannelLiveAssociationsStatsTO;
import com.foros.session.channel.ChannelPerformanceTO;
import com.foros.session.channel.ExpressionAssociationTO;
import com.foros.session.channel.service.SearchChannelService;

import java.util.Collection;
import javax.ejb.EJB;
import java.util.TimeZone;

public abstract class ViewAdvertisingChannelActionSupport<T extends Channel> extends ViewChannelActionSupport<T> {

    @EJB
    protected RestrictionService restrictionService;

    @EJB
    protected SearchChannelService searchChannelService;

    private ChannelLiveAssociationsStatsTO liveAssociations;
    private ChannelPerformanceTO performance;
    private String lastUsedRelativeDate;
    private CurrencyConverter currencyExchangeRate;
    private Collection<ExpressionAssociationTO> expressionAssociations;

    protected void loadExpressionAssociations() {
        expressionAssociations = searchChannelService.findExpressionAssociations(getId());
    }

    protected void loadAdvertiserChannelProperties() {
        Channel channel = searchChannelService.find(getModel().getId());
        if (!restrictionService.isPermitted("AdvertisingChannel.viewStats", channel)) {
            return;
        }

        performance = searchChannelService.findChannelPerformanceStats(channel.getId());
        if (performance != null) {
            lastUsedRelativeDate = getRelativeDate(performance.getLastUsed(), TimeZone.getTimeZone("GMT"));
        }

        if (ChannelVisibility.PUB == channel.getVisibility() || ChannelVisibility.CMP == channel.getVisibility()) {
            liveAssociations = searchChannelService.findChannelAssociationsStats(channel.getId());
        }

        currencyExchangeRate = ChannelHelper.getCurrencyConverterForStats(channel);
    }

    public Collection<ExpressionAssociationTO> getExpressionAssociations() {
        return expressionAssociations;
    }

    public ChannelLiveAssociationsStatsTO getLiveAssociations() {
        return liveAssociations;
    }

    public ChannelPerformanceTO getPerformance() {
        return performance;
    }

    public String getLastUsedRelativeDate() {
        return lastUsedRelativeDate;
    }

    public CurrencyConverter getCurrencyExchangeRate() {
        return currencyExchangeRate;
    }
}
