package com.foros.model.campaign;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class CampaignAllocationsTotalTO implements Serializable {
    private List<CampaignAllocationTO> allocations;
    private BigDecimal amount;
    private BigDecimal utilisedAmount;
    private BigDecimal availableAmount;

    public CampaignAllocationsTotalTO() { }

    public List<CampaignAllocationTO> getAllocations() {
        return allocations;
    }

    public void setAllocations(List<CampaignAllocationTO> allocations) {
        this.allocations = allocations;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getUtilisedAmount() {
        return utilisedAmount;
    }

    public void setUtilisedAmount(BigDecimal utilisedAmount) {
        this.utilisedAmount = utilisedAmount;
    }

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }
}
