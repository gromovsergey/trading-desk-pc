package com.foros.rs.client.model.advertising.campaign;

import com.foros.rs.client.model.PagingSelectorContainer;
import com.foros.rs.client.util.QueryEntity;
import com.foros.rs.client.util.QueryParameter;
import com.foros.rs.client.model.entity.Status;
import com.foros.rs.client.model.operation.PagingSelector;

import java.lang.Long;
import java.util.List;


@QueryEntity
public class CCGKeywordSelector implements PagingSelectorContainer {

    @QueryParameter("paging")
    private PagingSelector paging;

    @QueryParameter("advertiser.ids")
    private List<Long> advertiserIds;

    @QueryParameter("campaign.ids")
    private List<Long> campaignIds;

    @QueryParameter("group.ids")
    private List<Long> groupIds;

    @QueryParameter("keyword.ids")
    private List<Long> keywordIds;

    @QueryParameter("keyword.statuses")
    private List<Status> keywordStatuses;

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

    public List<Long> getKeywordIds() {
        return this.keywordIds;
    }

    public void setKeywordIds(List<Long> keywordIds) {
        this.keywordIds = keywordIds;
    }

    public List<Status> getKeywordStatuses() {
        return this.keywordStatuses;
    }

    public void setKeywordStatuses(List<Status> keywordStatuses) {
        this.keywordStatuses = keywordStatuses;
    }
}