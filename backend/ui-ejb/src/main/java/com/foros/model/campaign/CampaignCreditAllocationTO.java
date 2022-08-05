package com.foros.model.campaign;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class CampaignCreditAllocationTO implements Serializable {
    private Long id;
    private Timestamp version;
    private CampaignCredit campaignCredit;
    private Campaign campaign;
    private BigDecimal allocatedAmount;
    private BigDecimal usedAmount;
    private BigDecimal availableAmount;

    public CampaignCreditAllocationTO() { }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getVersion() {
        return version;
    }

    public void setVersion(Timestamp version) {
        this.version = version;
    }

    public CampaignCredit getCampaignCredit() {
        return campaignCredit;
    }

    public void setCampaignCredit(CampaignCredit campaignCredit) {
        this.campaignCredit = campaignCredit;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public BigDecimal getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(BigDecimal allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }

    public BigDecimal getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(BigDecimal usedAmount) {
        this.usedAmount = usedAmount;
    }

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }
}
