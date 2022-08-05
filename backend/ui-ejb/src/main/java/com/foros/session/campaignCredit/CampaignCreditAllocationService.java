package com.foros.session.campaignCredit;

import com.foros.model.campaign.CampaignCreditAllocation;
import com.foros.model.campaign.CampaignCreditAllocationTO;

import javax.ejb.Local;
import java.util.List;

@Local
public interface CampaignCreditAllocationService {
    Long create(CampaignCreditAllocation allocation);

    CampaignCreditAllocation update(CampaignCreditAllocation allocation);

    CampaignCreditAllocation find(Long id);

    List<CampaignCreditAllocationTO> findCreditAllocations(Long campaignCreditId);

    CampaignCreditAllocationTO findCreditAllocationForCampaign(Long campaignId);

    boolean hasAllocations(Long campaignId);
}
