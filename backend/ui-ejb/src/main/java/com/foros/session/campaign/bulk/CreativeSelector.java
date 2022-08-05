package com.foros.session.campaign.bulk;

import com.foros.model.Status;
import com.foros.model.campaign.CampaignType;
import com.foros.model.creative.Creative;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Selector;

import java.util.List;

public class CreativeSelector implements Selector<Creative> {

    private List<Long> advertiserIds;
    private List<Status> statuses;
    private CampaignType campaignType;
    private List<Long> creatives;
    private List<Long> templates;
    private List<Long> excludedTemplates;
    private List<Long> sizes;
    private List<Long> excludedSizes;

    private Paging paging;

    public List<Long> getAdvertiserIds() {
        return advertiserIds;
    }

    public void setAdvertiserIds(List<Long> advertiserIds) {
        this.advertiserIds = advertiserIds;
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

    public List<Long> getCreatives() {
        return creatives;
    }

    public void setCreatives(List<Long> creatives) {
        this.creatives = creatives;
    }

    public List<Long> getTemplates() {
        return templates;
    }

    public void setTemplates(List<Long> templates) {
        this.templates = templates;
    }

    public List<Long> getExcludedTemplates() {
        return excludedTemplates;
    }

    public void setExcludedTemplates(List<Long> excludedTemplates) {
        this.excludedTemplates = excludedTemplates;
    }

    public List<Long> getSizes() {
        return sizes;
    }

    public void setSizes(List<Long> sizes) {
        this.sizes = sizes;
    }

    public List<Long> getExcludedSizes() {
        return excludedSizes;
    }

    public void setExcludedSizes(List<Long> excludedSizes) {
        this.excludedSizes = excludedSizes;
    }
}
