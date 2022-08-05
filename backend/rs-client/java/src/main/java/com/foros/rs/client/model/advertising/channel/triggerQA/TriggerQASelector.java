package com.foros.rs.client.model.advertising.channel.triggerQA;

import com.foros.rs.client.model.PagingSelectorContainer;
import com.foros.rs.client.util.QueryEntity;
import com.foros.rs.client.util.QueryParameter;
import com.foros.rs.client.model.entity.QaStatus;
import com.foros.rs.client.model.operation.PagingSelector;

import java.lang.Long;


@QueryEntity
public class TriggerQASelector implements PagingSelectorContainer {

    @QueryParameter("campaign.id")
    private Long campaignId;

    @QueryParameter("group.id")
    private Long groupId;

    @QueryParameter("channel.id")
    private Long channelId;

    @QueryParameter("trigger.status")
    private QaStatus triggerStatus;

    @QueryParameter("paging")
    private PagingSelector paging;

    public Long getCampaignId() {
        return this.campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Long getGroupId() {
        return this.groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getChannelId() {
        return this.channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public QaStatus getTriggerStatus() {
        return this.triggerStatus;
    }

    public void setTriggerStatus(QaStatus triggerStatus) {
        this.triggerStatus = triggerStatus;
    }

    @Override
    public PagingSelector getPaging() {
        return this.paging;
    }

    @Override
    public void setPaging(PagingSelector paging) {
        this.paging = paging;
    }
}