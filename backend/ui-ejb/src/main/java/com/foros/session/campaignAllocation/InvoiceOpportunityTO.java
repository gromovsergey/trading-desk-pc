package com.foros.session.campaignAllocation;

import java.io.Serializable;
import java.math.BigDecimal;

public class InvoiceOpportunityTO implements Serializable {
    private Long opportunityId;
    private String ioNumber;
    private String poNumber;
    private Long campaignId;
    private String campaignName;
    private BigDecimal amount;

    public InvoiceOpportunityTO(Long opportunityId, String ioNumber, String poNumber,
                                Long campaignId, String campaignName, BigDecimal amount) {
        this.opportunityId = opportunityId;
        this.ioNumber = ioNumber;
        this.poNumber = poNumber;
        this.campaignId = campaignId;
        this.campaignName = campaignName;
        this.amount = amount;
    }

    public Long getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(Long opportunityId) {
        this.opportunityId = opportunityId;
    }

    public String getIoNumber() {
        return ioNumber;
    }

    public void setIoNumber(String ioNumber) {
        this.ioNumber = ioNumber;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}