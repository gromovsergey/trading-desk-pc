package com.foros.action.channel.behavioral;

import com.foros.action.channel.ChannelBreadcrumbsElement;
import com.foros.action.channel.ViewAdvertisingChannelActionSupport;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.channel.BehavioralChannel;
import com.foros.security.AccountRole;
import com.foros.session.channel.service.AdvertisingChannelRestrictions;
import com.foros.session.channel.service.BehavioralChannelService;

import javax.ejb.EJB;

public class ViewBehavioralChannelAction extends ViewAdvertisingChannelActionSupport<BehavioralChannel> implements RequestContextsAware, BreadcrumbsSupport {
    @EJB
    private BehavioralChannelService behavioralChannelService;

    @EJB
    private AdvertisingChannelRestrictions advertisingChannelRestrictions;

    @ReadOnly
    public String view() {
        loadChannel();
        loadCategories();
        loadExpressionAssociations();
        loadAdvertiserChannelProperties();

        return SUCCESS;
    }

    @Override
    protected BehavioralChannel findChannel(Long id) {
        return behavioralChannelService.view(id);
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
