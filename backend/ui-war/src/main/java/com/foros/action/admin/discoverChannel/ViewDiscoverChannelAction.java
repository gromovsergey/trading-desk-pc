package com.foros.action.admin.discoverChannel;

import com.foros.action.channel.ViewChannelActionSupport;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.channel.DiscoverChannel;
import com.foros.session.channel.service.DiscoverChannelService;

import javax.ejb.EJB;

public class ViewDiscoverChannelAction extends ViewChannelActionSupport<DiscoverChannel> implements BreadcrumbsSupport {
    @EJB
    private DiscoverChannelService discoverChannelService;

    @ReadOnly
    public String view() {
        loadChannel();
        loadCategories();

        return SUCCESS;
    }

    @Override
    protected DiscoverChannel findChannel(Long id) {
        return discoverChannelService.view(id);
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new DiscoverChannelsBreadcrumbsElement()).add(new DiscoverChannelBreadcrumbsElement(getModel()));
    }
}
