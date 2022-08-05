package com.foros.action.channel.audience;

import com.foros.action.channel.ChannelBreadcrumbsElement;
import com.foros.action.channel.ViewAdvertisingChannelActionSupport;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.channel.AudienceChannel;
import com.foros.security.AccountRole;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.channel.service.AudienceChannelService;

import javax.ejb.EJB;

public class ViewAudienceChannelAction extends ViewAdvertisingChannelActionSupport<AudienceChannel> implements RequestContextsAware, BreadcrumbsSupport {
    @EJB
    private AudienceChannelService audienceChannelService;

    @EJB
    private CampaignCreativeGroupService campaignCreativeGroupService;

    @ReadOnly
    public String view() {
        loadChannel();
        loadExpressionAssociations();
        loadAdvertiserChannelProperties();
        return SUCCESS;
    }

    @Override
    protected AudienceChannel findChannel(Long id) {
        return audienceChannelService.view(id);
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = null;
        if (model.getAccount().getRole() == AccountRole.INTERNAL) {
            breadcrumbs = ChannelBreadcrumbsElement.getChannelBreadcrumbs(getModel());
        }

        return breadcrumbs;
    }
}
