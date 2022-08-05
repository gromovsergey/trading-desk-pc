package com.foros.model.campaign;

import com.foros.session.campaignAllocation.OpportunityTO;

import java.io.Serializable;
import java.math.BigDecimal;

public class CampaignAllocationTO implements Serializable {
    private OpportunityTO opportunity;

    private Long order;
    private BigDecimal amount;
    private BigDecimal utilizedAmount;
    private BigDecimal availableAmount;

    public CampaignAllocationTO() { }

    public OpportunityTO getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(OpportunityTO opportunity) {
        this.opportunity = opportunity;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getUtilizedAmount() {
        return utilizedAmount;
    }

    public void setUtilizedAmount(BigDecimal utilizedAmount) {
        this.utilizedAmount = utilizedAmount;
    }

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }
}
