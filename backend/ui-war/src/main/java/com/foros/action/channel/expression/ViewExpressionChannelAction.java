package com.foros.action.channel.expression;

import com.foros.action.channel.ChannelBreadcrumbsElement;
import com.foros.action.channel.ViewAdvertisingChannelActionSupport;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.InternalAccount;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.model.channel.ExpressionChannel;
import com.foros.security.AccountRole;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.campaign.ChannelRatesTO;
import com.foros.session.channel.service.AdvertisingChannelRestrictions;
import com.foros.session.channel.service.ExpressionChannelService;
import com.foros.session.channel.service.ExpressionService;
import com.foros.util.CCGChannelRatesUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;

public class ViewExpressionChannelAction extends ViewAdvertisingChannelActionSupport<ExpressionChannel> implements RequestContextsAware, BreadcrumbsSupport {
    @EJB
    private ExpressionChannelService expressionChannelService;

    @EJB
    private CampaignCreativeGroupService campaignCreativeGroupService;

    @EJB
    private ExpressionService expressionService;

    @EJB
    private AdvertisingChannelRestrictions advertisingChannelRestrictions;

    private String populatedRate;
    private Map<Long, Channel> usedChannels;

    @ReadOnly
    public String view() {
        loadChannel();
        loadCategories();
        loadExpressionAssociations();
        loadAdvertiserChannelProperties();
        populateRate();
        populateUsedChannels();
        return SUCCESS;
    }

    private void populateUsedChannels() {
        Collection<Channel> channels = expressionService.findChannelsFromExpression(model.getExpression());
        usedChannels = new HashMap<Long, Channel>();
        for (Channel channel : channels) {
            usedChannels.put(channel.getId(), channel);
        }
    }

    private void populateRate() {
        if (model.getVisibility() != ChannelVisibility.PRI || model.getAccount() instanceof InternalAccount) {
            return;
        }

        ChannelRatesTO rates = campaignCreativeGroupService.getChannelTargetingRates(getId());

        if (rates != null) {
            populatedRate = CCGChannelRatesUtil.getPopulatedTargetingRates(rates);
        }
    }

    @Override
    protected ExpressionChannel findChannel(Long id) {
        return expressionChannelService.view(id);
    }

    public String getPopulatedRate() {
        return populatedRate;
    }

    public Channel usedChannel(Long id) {
        return usedChannels.get(id);
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = null;
        if (model.getAccount().getRole() == AccountRole.INTERNAL && advertisingChannelRestrictions.canView(model.getAccount())) {
            breadcrumbs = ChannelBreadcrumbsElement.getChannelBreadcrumbs(getModel());
        }

        return breadcrumbs;
    }
}
