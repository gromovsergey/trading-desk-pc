package com.foros.session.campaignAllocation;

import com.foros.session.NamedTO;

import java.math.BigDecimal;

public class OpportunityTO extends NamedTO {
    private String ioNumber;
    private String notes;
    private BigDecimal amount;
    private BigDecimal spentAmount;
    private BigDecimal unallocatedAmount;
    private BigDecimal availableAmount;

    public OpportunityTO(Long id, String name, String ioNumber, String notes, BigDecimal amount, BigDecimal spentAmount, BigDecimal unallocatedAmount) {
        super(id, name);
        this.ioNumber = ioNumber;
        this.notes = notes;
        this.amount = amount;
        this.spentAmount = spentAmount;

        availableAmount = amount.subtract(spentAmount);


        // if "Unallocated Amount" < 0, then show 0
        this.unallocatedAmount = unallocatedAmount.max(BigDecimal.ZERO);
    }

    public String getIoNumber() {
        return ioNumber;
    }

    public void setIoNumber(String ioNumber) {
        this.ioNumber = ioNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getUnallocatedAmount() {
        return unallocatedAmount;
    }

    public void setUnallocatedAmount(BigDecimal unallocatedAmount) {
        this.unallocatedAmount = unallocatedAmount;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }
}