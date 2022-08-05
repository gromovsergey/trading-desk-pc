package com.foros.action.creative.display;

import com.foros.action.SearchForm;

public class CreativeSearchForm extends SearchForm {
    private Long campaignId = null;
    private Long sizeId = null;
    private Long displayStatusId = null;
    private String orderBy;

    public Long getCampaignId() {
        return campaignId;
    }

    public Long getDisplayStatusId() {
        return displayStatusId;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public Long getSizeId() {
        return sizeId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public void setDisplayStatusId(Long displayStatusId) {
        this.displayStatusId = displayStatusId;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public void setSizeId(Long sizeId) {
        this.sizeId = sizeId;
    }

    @Override
    public String toString() {
        return "CreativeSearchForm [campaignId=" + campaignId + ", sizeId="
                + sizeId + ", displayStatusId=" + displayStatusId + "]";
    }
}
