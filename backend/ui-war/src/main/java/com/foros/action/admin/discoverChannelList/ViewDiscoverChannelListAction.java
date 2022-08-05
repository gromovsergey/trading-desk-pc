package com.foros.action.admin.discoverChannelList;

import com.foros.action.channel.ChannelHelper;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.account.Account;
import com.foros.model.channel.DiscoverChannelList;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.EntityTO;
import com.foros.session.admin.categoryChannel.CategoryChannelService;
import com.foros.session.channel.ChannelStatsTO;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.util.EntityUtils;

import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;

public class ViewDiscoverChannelListAction extends DiscoverChannelListActionSupport implements BreadcrumbsSupport {
    @EJB
    private SearchChannelService searchChannelService;

    @EJB
    private CategoryChannelService categoryChannelService;

    private List<List<EntityTO>> populatedCategories = Collections.emptyList();
    private ChannelStatsTO channelStatistic;

    public ViewDiscoverChannelListAction() {
        model = new DiscoverChannelList();
    }

    public ChannelStatsTO getChannelStatistic() {
        return channelStatistic;
    }

    public List<List<EntityTO>> getPopulatedCategories() {
        return populatedCategories;
    }

    @ReadOnly
    @Restrict(restriction = "DiscoverChannel.view")
    public String view() throws Exception {
        model = discoverChannelListService.view(getModel().getId());
        populateForView();
        Account account = model.getAccount();
        setAccountId(account.getId());
        setAccountName(EntityUtils.appendStatusSuffix(account.getName(), account.getStatus()));
        return SUCCESS;
    }

    private void populateForView() throws Exception {
        populatedCategories = ChannelHelper.populateCategories(getModel(), categoryChannelService);
        channelStatistic = searchChannelService.findChannelStatistics(getModel().getId());
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new DiscoverChannelListsBreadcrumbsElement()).add(new DiscoverChannelListBreadcrumbsElement(model));
    }
}
