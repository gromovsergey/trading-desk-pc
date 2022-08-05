package com.foros.session.channel;

import java.io.Serializable;

public class ChannelLiveAssociationsStatsTO implements Serializable {
    private long liveAdvertisers;
    private long liveCampaigns;
    private long liveCreativeGroups;

    public ChannelLiveAssociationsStatsTO() { }

    public long getLiveAdvertisers() {
        return liveAdvertisers;
    }

    public void setLiveAdvertisers(long liveAdvertisers) {
        this.liveAdvertisers = liveAdvertisers;
    }

    public long getLiveCampaigns() {
        return liveCampaigns;
    }

    public void setLiveCampaigns(long liveCampaigns) {
        this.liveCampaigns = liveCampaigns;
    }

    public long getLiveCreativeGroups() {
        return liveCreativeGroups;
    }

    public void setLiveCreativeGroups(long liveCreativeGroups) {
        this.liveCreativeGroups = liveCreativeGroups;
    }
}