package com.foros.session.campaign;

public class AdvertiserDashboardTO extends DashboardTO {

    private Long advertiserId;

    private String advertiserName;

    private Integer advertiserDisplayStatusId;

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public String getAdvertiserName() {
        return advertiserName;
    }

    public void setAdvertiserName(String advertiserName) {
        this.advertiserName = advertiserName;
    }

    public Integer getAdvertiserDisplayStatusId() {
        return advertiserDisplayStatusId;
    }

    public void setAdvertiserDisplayStatusId(Integer advertiserDisplayStatusId) {
        this.advertiserDisplayStatusId = advertiserDisplayStatusId;
    }
}
