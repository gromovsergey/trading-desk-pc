package com.foros.action.admin;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.campaign.Campaign;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.campaign.CampaignService;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.session.creative.CreativeService;

import java.util.Collection;
import javax.ejb.EJB;

public class AdopsDashboardAction extends BaseActionSupport {

    private static final int MAX_RECENTLY_CHANGED_CAMPAIGNS = 50;
    private static final int MAX_CCGS_IN_CAMPAIGN = 10;

    @EJB
    private SearchChannelService channelSearchService;

    @EJB
    private CreativeService creativeService;

    @EJB
    private CampaignService campaignService;

    private int creativePendingCount;
    private Integer channelPendingCount;
    private int discoverChannelPendingCount;
    private Collection<Campaign> campaigns;

    @ReadOnly
    @Restrict(restriction = "AdopsDashboard.run")
    public String main() throws Exception {
        creativePendingCount = creativeService.findPendingFOROSCreativesCount();
        discoverChannelPendingCount = channelSearchService.getPendingDiscoverChannelsCount();

        campaigns = campaignService.findRecentlyChanged(MAX_RECENTLY_CHANGED_CAMPAIGNS, MAX_CCGS_IN_CAMPAIGN);

        return "success";
    }

    public int getCreativesPendingCount() {
        return creativePendingCount;
    }

    public int getChannelsPendingCount() {
        if (channelPendingCount == null) {
            channelPendingCount = channelSearchService.getPendingAdvertisingChannelsCount();
        }

        return channelPendingCount;
    }

    public int getDiscoverChannelsPendingCount() {
        return discoverChannelPendingCount;
    }

    public Collection<Campaign> getCampaigns() {
        return campaigns;
    }
}
