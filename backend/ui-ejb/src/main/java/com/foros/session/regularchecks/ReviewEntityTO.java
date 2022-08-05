package com.foros.session.regularchecks;

public class ReviewEntityTO {

    private String accountName;

    private String advertiserName;

    private String campaignName;

    private String entityName;

    private Long entityId;

    private Double hoursAgo;

    private boolean hourlyCheck;

    private String dueCaption;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAdvertiserName() {
        return advertiserName;
    }

    public void setAdvertiserName(String advertiserName) {
        this.advertiserName = advertiserName;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Double getHoursAgo() {
        return hoursAgo;
    }

    public void setHoursAgo(Double hoursAgo) {
        this.hoursAgo = hoursAgo;
    }

    public boolean isHourlyCheck() {
        return hourlyCheck;
    }

    public void setHourlyCheck(boolean hourlyCheck) {
        this.hourlyCheck = hourlyCheck;
    }

    public String getDueCaption() {
        return dueCaption;
    }

    public void setDueCaption(String dueCaption) {
        this.dueCaption = dueCaption;
    }
}
