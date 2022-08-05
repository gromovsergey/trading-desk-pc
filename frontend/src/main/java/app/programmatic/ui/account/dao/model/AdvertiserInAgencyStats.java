package app.programmatic.ui.account.dao.model;

import java.math.BigDecimal;

public class AdvertiserInAgencyStats {
    private Long imps;
    private Long clicks;
    private BigDecimal ctr;
    private BigDecimal targetingCost;
    private BigDecimal inventoryCost;
    private BigDecimal totalCost;
    private BigDecimal totalValue;
    private BigDecimal campaignCreditUsed;
    private BigDecimal ecpm;
    private BigDecimal selfServiceCost;
    private Long ccgsPendingUser;
    private Long creativesPendingUser;
    private Long creativesPendingForos;
    private Long advertiserId;
    private String advertiserName;
    private String displayStatus;

    public Long getImps() {
        return imps;
    }

    public void setImps(Long imps) {
        this.imps = imps;
    }

    public Long getClicks() {
        return clicks;
    }

    public void setClicks(Long clicks) {
        this.clicks = clicks;
    }

    public BigDecimal getCtr() {
        return ctr;
    }

    public void setCtr(BigDecimal ctr) {
        this.ctr = ctr;
    }

    public BigDecimal getTargetingCost() {
        return targetingCost;
    }

    public void setTargetingCost(BigDecimal targetingCost) {
        this.targetingCost = targetingCost;
    }

    public BigDecimal getInventoryCost() {
        return inventoryCost;
    }

    public void setInventoryCost(BigDecimal inventoryCost) {
        this.inventoryCost = inventoryCost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public BigDecimal getCampaignCreditUsed() {
        return campaignCreditUsed;
    }

    public void setCampaignCreditUsed(BigDecimal campaignCreditUsed) {
        this.campaignCreditUsed = campaignCreditUsed;
    }

    public BigDecimal getEcpm() {
        return ecpm;
    }

    public void setEcpm(BigDecimal ecpm) {
        this.ecpm = ecpm;
    }

    public BigDecimal getSelfServiceCost() {
        return selfServiceCost;
    }

    public void setSelfServiceCost(BigDecimal selfServiceCost) {
        this.selfServiceCost = selfServiceCost;
    }

    public Long getCcgsPendingUser() {
        return ccgsPendingUser;
    }

    public void setCcgsPendingUser(Long ccgsPendingUser) {
        this.ccgsPendingUser = ccgsPendingUser;
    }

    public Long getCreativesPendingUser() {
        return creativesPendingUser;
    }

    public void setCreativesPendingUser(Long creativesPendingUser) {
        this.creativesPendingUser = creativesPendingUser;
    }

    public Long getCreativesPendingForos() {
        return creativesPendingForos;
    }

    public void setCreativesPendingForos(Long creativesPendingForos) {
        this.creativesPendingForos = creativesPendingForos;
    }

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

    public String getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }
}
