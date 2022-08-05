package com.foros.rs.client.model.advertising.campaign;

import com.foros.rs.client.model.PagingSelectorContainer;
import com.foros.rs.client.util.QueryEntity;
import com.foros.rs.client.util.QueryParameter;
import com.foros.rs.client.model.entity.Status;
import com.foros.rs.client.model.operation.PagingSelector;

import java.lang.Long;
import java.util.List;


@QueryEntity
public class CampaignCreativeGroupSelector implements PagingSelectorContainer {

    @QueryParameter("campaign.type")
    private CampaignType campaignType;

    @QueryParameter("paging")
    private PagingSelector paging;

    @QueryParameter("advertiser.ids")
    private List<Long> advertiserIds;

    @QueryParameter("campaign.ids")
    private List<Long> campaignIds;

    
    @QueryParameter("group.ids")
    private List<Long> groupIds;

    @QueryParameter("group.statuses")
    private List<Status> groupStatuses;

    public CampaignType getCampaignType() {
        return this.campaignType;
    }

    public void setCampaignType(CampaignType campaignType) {
        this.campaignType = campaignType;
    }

    @Override
    public PagingSelector getPaging() {
        return this.paging;
    }

    @Override
    public void setPaging(PagingSelector paging) {
        this.paging = paging;
    }

    public List<Long> getAdvertiserIds() {
        return this.advertiserIds;
    }

    public void setAdvertiserIds(List<Long> advertiserIds) {
        this.advertiserIds = advertiserIds;
    }

    public List<Long> getCampaignIds() {
        return this.campaignIds;
    }

    public void setCampaignIds(List<Long> campaignIds) {
        this.campaignIds = campaignIds;
    }

    public List<Long> getGroupIds() {
        return this.groupIds;
    }

    public void setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
    }

    public List<Status> getGroupStatuses() {
        return this.groupStatuses;
    }

    public void setGroupStatuses(List<Status> groupStatuses) {
        this.groupStatuses = groupStatuses;
    }
}