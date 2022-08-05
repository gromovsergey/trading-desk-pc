package com.foros.session.campaign.bulk;

import com.foros.model.Status;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignType;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Selector;

import java.util.List;

public class CampaignSelector implements Selector<Campaign> {

    private List<Long> advertiserIds;
    private List<Long> campaigns;
    private List<Status> statuses;
    private CampaignType campaignType;
    private Paging paging;

    public List<Long> getAdvertiserIds() {
        return advertiserIds;
    }

    public void setAdvertiserIds(List<Long> advertiserIds) {
        this.advertiserIds = advertiserIds;
    }

    public List<Long> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(List<Long> campaigns) {
        this.campaigns = campaigns;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    public CampaignType getCampaignType() {
        return campaignType;
    }

    public void setCampaignType(CampaignType campaignType) {
        this.campaignType = campaignType;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }
}
