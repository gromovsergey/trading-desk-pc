package com.foros.session.channel.triggerQA;

import com.foros.model.ApproveStatus;
import com.foros.session.bulk.Paging;


public class TriggerQASelector {

    private Long campaignId;
    private Long ccgId;
    private Long channelId;
    private ApproveStatus triggerStatus;
    private Paging paging;


    public TriggerQASelector(Long campaignId, Long ccgId, Long channelId, ApproveStatus triggerStatus, Integer pagingFirst, Integer pagingCount) {
        this.campaignId = campaignId;
        this.ccgId = ccgId;
        this.channelId = channelId;
        this.triggerStatus = triggerStatus;
        this.paging = new Paging(pagingFirst, pagingCount);
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public Long getCcgId() {
        return ccgId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public ApproveStatus getTriggerStatus() {
        return triggerStatus;
    }

    public Paging getPaging() {
        return paging;
    }
}
