package com.foros.test.factory;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCredit;
import com.foros.model.campaign.CampaignCreditAllocation;
import com.foros.session.campaignCredit.CampaignCreditAllocationService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.math.BigDecimal;

@Stateless
@LocalBean
public class CampaignCreditAllocationTestFactory extends TestFactory<CampaignCreditAllocation> {
    @EJB
    private CampaignCreditAllocationService campaignCreditAllocationService;

    @EJB
    private AgencyAccountTestFactory agencyAccountTestFactory;

    @EJB
    private AdvertiserAccountTestFactory advertiserAccountTestFactory;

    @EJB
    private CampaignCreditTestFactory campaignCreditTestFactory;

    @EJB
    private DisplayCampaignTestFactory displayCampaignTestFactory;

    @Override
    public CampaignCreditAllocation create() {
        CampaignCredit campaignCredit = campaignCreditTestFactory.createPersistent();
        return create(campaignCredit);
    }

    public CampaignCreditAllocation create(CampaignCredit campaignCredit) {
        AgencyAccount account = (AgencyAccount) campaignCredit.getAccount();
        AdvertiserAccount advertiser = advertiserAccountTestFactory.createPersistentAdvertiserInAgency(account);
        return create(campaignCredit, advertiser);
    }

    public CampaignCreditAllocation create(CampaignCredit campaignCredit, AdvertiserAccount advertiser) {
        Campaign campaign = displayCampaignTestFactory.createPersistent(advertiser);
        return create(campaignCredit, campaign);
    }

    public CampaignCreditAllocation create(CampaignCredit campaignCredit, Campaign campaign) {
        CampaignCreditAllocation allocation = new CampaignCreditAllocation();
        allocation.setCampaignCredit(campaignCredit);
        allocation.setCampaign(campaign);
        allocation.setAllocatedAmount(BigDecimal.ONE);
        return allocation;
    }

    @Override
    public void persist(CampaignCreditAllocation allocation) {
        campaignCreditAllocationService.create(allocation);
        entityManager.flush();
    }

    @Override
    public CampaignCreditAllocation createPersistent() {
        CampaignCreditAllocation allocation = create();
        persist(allocation);
        return allocation;
    }

    public CampaignCreditAllocation createPersistent(CampaignCredit campaignCredit) {
        CampaignCreditAllocation allocation = create(campaignCredit);
        persist(allocation);
        return allocation;
    }

    public CampaignCreditAllocation createPersistent(CampaignCredit campaignCredit, Campaign campaign) {
        CampaignCreditAllocation allocation = create(campaignCredit, campaign);
        persist(allocation);
        return allocation;
    }

    public CampaignCreditAllocation createPersistent(CampaignCredit campaignCredit, AdvertiserAccount advertiser) {
        CampaignCreditAllocation allocation = create(campaignCredit, advertiser);
        persist(allocation);
        return allocation;
    }

    public void update(CampaignCreditAllocation allocation) {
        campaignCreditAllocationService.update(allocation);
        entityManager.flush();
    }
}