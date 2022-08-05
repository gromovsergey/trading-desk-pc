package com.foros.session.campaign;

import java.math.BigDecimal;

public class DashboardTO {

    private long imps;

    private long clicks;

    private BigDecimal ctr;

    private BigDecimal targetingCost;

    private BigDecimal inventoryCost;

    private BigDecimal totalCost;

    private BigDecimal totalValue;

    private BigDecimal campaignCreditUsed;

    private BigDecimal ecpm;

    private BigDecimal selfServiceCost;

    private long ccgsPendingUser;

    private long creativesPendingUser;

    private long creativesPendingForos;

    private long uniqueUsers;

    public long getImps() {
        return imps;
    }

    public void setImps(long imps) {
        this.imps = imps;
    }

    public long getClicks() {
        return clicks;
    }

    public void setClicks(long clicks) {
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

    public BigDecimal getSelfServiceCost() {
        return selfServiceCost;
    }

    public void setSelfServiceCost(BigDecimal selfServiceCost) {
        this.selfServiceCost = selfServiceCost;
    }

    public BigDecimal getCampaignCreditUsed() {
        return campaignCreditUsed;
    }

    public void setCampaignCreditUsed(BigDecimal campaignCreditUsed) {
        this.campaignCreditUsed = campaignCreditUsed;
    }

    public long getCcgsPendingUser() {
        return ccgsPendingUser;
    }

    public void setCcgsPendingUser(long ccgsPendingUser) {
        this.ccgsPendingUser = ccgsPendingUser;
    }

    public long getCreativesPendingUser() {
        return creativesPendingUser;
    }

    public void setCreativesPendingUser(long creativesPendingUser) {
        this.creativesPendingUser = creativesPendingUser;
    }

    public BigDecimal getEcpm() {
        return ecpm;
    }

    public void setEcpm(BigDecimal ecpm) {
        this.ecpm = ecpm;
    }

    public long getUniqueUsers() {
        return uniqueUsers;
    }

    public void setUniqueUsers(long uniqueUsers) {
        this.uniqueUsers = uniqueUsers;
    }

    public long getCreativesPendingForos() {
        return creativesPendingForos;
    }

    public void setCreativesPendingForos(long creativesPendingForos) {
        this.creativesPendingForos = creativesPendingForos;
    }
}
