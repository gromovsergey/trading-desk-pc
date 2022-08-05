package com.foros.test.factory;

import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignAllocation;
import com.foros.model.campaign.CampaignAllocationStatus;
import com.foros.model.opportunity.Opportunity;
import com.foros.session.campaignAllocation.CampaignAllocationService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.math.BigDecimal;

@Stateless
@LocalBean
public class CampaignAllocationTestFactory extends TestFactory<CampaignAllocation> {
    @EJB
    private CampaignAllocationService campaignAllocationService;

    @EJB
    private DisplayCampaignTestFactory campaignTF;

    @EJB
    private OpportunityTestFactory opportunityTF;

    @Override
    public CampaignAllocation create() {
        CampaignAllocation allocation = new CampaignAllocation();
        allocation.setOrder(1L);
        allocation.setStatus(CampaignAllocationStatus.ACTIVE);
        allocation.setAmount(BigDecimal.ONE);
        return allocation;
    }

    @Override
    public void persist(CampaignAllocation allocation) {
        assert(allocation.getOpportunity() != null);
        assert(allocation.getCampaign() != null);

        campaignAllocationService.updateCampaignAllocations(allocation.getCampaign());
        entityManager.flush();
    }

    @Override
    public void update(CampaignAllocation allocation) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public CampaignAllocation createPersistent() {
        Campaign campaign = campaignTF.createPersistent();
        Opportunity opportunity = opportunityTF.createPersistent(campaign.getAccount());
        return createPersistent(campaign, opportunity);
    }

    public CampaignAllocation create(Campaign campaign, Opportunity opportunity) {
        CampaignAllocation allocation = create();
        allocation.setOpportunity(opportunity);
        campaign.getAllocations().add(allocation);
        return allocation;
    }

    public CampaignAllocation create(Campaign campaign, Opportunity opportunity, BigDecimal amount) {
        CampaignAllocation allocation = create();
        allocation.setAmount(amount);
        allocation.setOpportunity(opportunity);
        allocation.setCampaign(campaign);
        return allocation;
    }

    public CampaignAllocation createPersistent(Campaign campaign, Opportunity opportunity) {
        CampaignAllocation allocation = create(campaign, opportunity);
        persist(allocation);
        return allocation;
    }
}
