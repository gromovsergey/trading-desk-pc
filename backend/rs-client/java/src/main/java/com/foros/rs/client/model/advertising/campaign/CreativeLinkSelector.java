package com.foros.rs.client.model.advertising.campaign;

import com.foros.rs.client.model.PagingSelectorContainer;
import com.foros.rs.client.util.QueryEntity;
import com.foros.rs.client.util.QueryParameter;
import com.foros.rs.client.model.entity.Status;
import com.foros.rs.client.model.operation.PagingSelector;

import java.lang.Long;
import java.util.List;


@QueryEntity
public class CreativeLinkSelector implements PagingSelectorContainer {

    
    @QueryParameter("paging")
    private PagingSelector paging;

    
    @QueryParameter("advertiser.ids")
    private List<Long> advertiserIds;

    
    @QueryParameter("campaign.ids")
    private List<Long> campaignIds;

    
    @QueryParameter("group.ids")
    private List<Long> groupIds;

    
    @QueryParameter("creative.ids")
    private List<Long> creativeIds;

    
    @QueryParameter("link.ids")
    private List<Long> creativeLinkIds;

    
    @QueryParameter("link.statuses")
    private List<Status> creativeLinkStatuses;


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

    
    public List<Long> getCreativeIds() {
        return this.creativeIds;
    }

    public void setCreativeIds(List<Long> creativeIds) {
        this.creativeIds = creativeIds;
    }

    
    public List<Long> getCreativeLinkIds() {
        return this.creativeLinkIds;
    }

    public void setCreativeLinkIds(List<Long> creativeLinkIds) {
        this.creativeLinkIds = creativeLinkIds;
    }

    
    public List<Status> getCreativeLinkStatuses() {
        return this.creativeLinkStatuses;
    }

    public void setCreativeLinkStatuses(List<Status> creativeLinkStatuses) {
        this.creativeLinkStatuses = creativeLinkStatuses;
    }
}