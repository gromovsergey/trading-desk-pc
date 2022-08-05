package com.foros.session.campaign;

public class CampaignDashboardTO extends DashboardTO {

    private long campaignId;

    private String campaignName;

    private int campaignDisplayStatusId;

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public Integer getCampaignDisplayStatusId() {
        return campaignDisplayStatusId;
    }

    public void setCampaignDisplayStatusId(Integer campaignDisplayStatusId) {
        this.campaignDisplayStatusId = campaignDisplayStatusId;
    }
}
