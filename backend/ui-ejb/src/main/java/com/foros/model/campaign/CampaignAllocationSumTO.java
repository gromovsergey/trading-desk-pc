package com.foros.model.campaign;

import com.foros.session.NamedTO;

import java.io.Serializable;
import java.math.BigDecimal;

public class CampaignAllocationSumTO implements Serializable {

    private NamedTO campaign;
    private BigDecimal amount;
    private BigDecimal utilizedAmount;

    public CampaignAllocationSumTO(Long campaignId, String campaignName, BigDecimal amount, BigDecimal utilizedAmount) {
        campaign = new NamedTO(campaignId, campaignName);
        this.amount = amount;
        this.utilizedAmount = utilizedAmount;
    }

    public NamedTO getCampaign() {
        return campaign;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getUtilizedAmount() {
        return utilizedAmount;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((campaign == null) ? 0 : campaign.hashCode());
        result = prime * result + ((utilizedAmount == null) ? 0 : utilizedAmount.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CampaignAllocationSumTO other = (CampaignAllocationSumTO) obj;
        if (amount == null) {
            if (other.amount != null)
                return false;
        } else if (!amount.equals(other.amount))
            return false;
        if (campaign == null) {
            if (other.campaign != null)
                return false;
        } else if (!campaign.equals(other.campaign))
            return false;
        if (utilizedAmount == null) {
            if (other.utilizedAmount != null)
                return false;
        } else if (!utilizedAmount.equals(other.utilizedAmount))
            return false;
        return true;
    }
}
