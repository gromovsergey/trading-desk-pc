package com.foros.session.campaignCredit;

import java.io.Serializable;
import java.math.BigDecimal;

public class CampaignCreditStatsTO implements Serializable {
    private BigDecimal amount;
    private BigDecimal spentAmount;
    private BigDecimal allocatedAmount;
    private BigDecimal maxAllocationAmount;
    private BigDecimal availableAmount;
    private BigDecimal unallocatedAmount;

    public CampaignCreditStatsTO() { }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }

    public BigDecimal getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(BigDecimal allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }

    public BigDecimal getMaxAllocationAmount() {
        return maxAllocationAmount;
    }

    public void setMaxAllocationAmount(BigDecimal maxAllocationAmount) {
        this.maxAllocationAmount = maxAllocationAmount;
    }

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }

    public BigDecimal getUnallocatedAmount() {
        return unallocatedAmount;
    }

    public void setUnallocatedAmount(BigDecimal unallocatedAmount) {
        this.unallocatedAmount = unallocatedAmount;
    }
}