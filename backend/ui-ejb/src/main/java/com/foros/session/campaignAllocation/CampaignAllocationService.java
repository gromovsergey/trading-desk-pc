package com.foros.session.campaignAllocation;

import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignAllocation;
import com.foros.model.campaign.CampaignAllocationTO;
import com.foros.model.campaign.CampaignAllocationsTotalTO;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

@Local
public interface CampaignAllocationService {
    List<InvoiceOpportunityTO> findInvoiceOpportunities(Long invoiceId, boolean includeCampaigns);

    Map<Long, OpportunityTO> getOpportunitiesMap(Long accountId);

    List<OpportunityTO> getAvailableOpportunities(Long accountId);

    List<CampaignAllocation> getCampaignAllocations(Long campaignId);

    void updateCampaignAllocations(Campaign campaign);

    CampaignAllocationsTotalTO getCampaignAllocationsTotal(Long campaignId);

    Collection<Long> findRemovableAllocationIds(Long campaignId);

    BigDecimal getEndedBudget(Long campaignId);
}
