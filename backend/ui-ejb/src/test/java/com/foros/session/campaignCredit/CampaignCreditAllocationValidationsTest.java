package com.foros.session.campaignCredit;

import com.foros.AbstractValidationsTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCredit;
import com.foros.model.campaign.CampaignCreditAllocation;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.CampaignCreditAllocationTestFactory;
import com.foros.test.factory.CampaignCreditTestFactory;
import com.foros.test.factory.DisplayCampaignTestFactory;

import group.Db;
import group.Validation;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@Category({ Db.class, Validation.class })
public class CampaignCreditAllocationValidationsTest extends AbstractValidationsTest {
    @Autowired
    private CampaignCreditAllocationTestFactory campaignCreditAllocationTF;

    @Autowired
    private CampaignCreditTestFactory campaignCreditTF;

    @Autowired
    private DisplayCampaignTestFactory displayCampaignTF;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Test
    public void testValidateCampaignDeleted() {
        CampaignCredit campaignCredit = campaignCreditTF.createPersistent();
        CampaignCreditAllocation allocation = campaignCreditAllocationTF.create(campaignCredit);

        validate("CampaignCreditAllocation.create", allocation);
        assertViolationsCount(0);

        displayCampaignTF.delete(allocation.getCampaign().getId());
        validate("CampaignCreditAllocation.create", allocation);
        assertViolationsCount(1);
        assertHasViolation("campaign");
    }

    @Test
    public void testValidateCampaign() {
        CampaignCredit campaignCredit = campaignCreditTF.createPersistent();
        CampaignCreditAllocation allocation = campaignCreditAllocationTF.create(campaignCredit);

        AgencyAccount agencyAccount = (AgencyAccount) campaignCredit.getAccount();
        AdvertiserAccount advertiser1 = advertiserAccountTF.createPersistentAdvertiserInAgency(agencyAccount);
        AdvertiserAccount advertiser2 = advertiserAccountTF.createPersistentAdvertiserInAgency(agencyAccount);
        AdvertiserAccount foreignAdvertiser = advertiserAccountTF.createPersistent();

        Campaign campaign1 = displayCampaignTF.createPersistent(advertiser1);
        Campaign campaign2 = displayCampaignTF.createPersistent(advertiser2);
        Campaign foreignCampaign = displayCampaignTF.createPersistent(foreignAdvertiser);

        allocation.setCampaign(campaign1);
        validate("CampaignCreditAllocation.create", allocation);
        assertViolationsCount(0);

        allocation.setCampaign(foreignCampaign);
        validate("CampaignCreditAllocation.create", allocation);
        assertViolationsCount(1);
        assertHasViolation("campaign");

        campaignCredit.setAdvertiser(advertiser1);
        campaignCreditTF.update(campaignCredit);

        allocation.setCampaign(campaign1);
        validate("CampaignCreditAllocation.create", allocation);
        assertViolationsCount(0);

        allocation.setCampaign(campaign2);
        validate("CampaignCreditAllocation.create", allocation);
        assertViolationsCount(1);
        assertHasViolation("campaign");

        // there can be only one campaign credit allocation per campaign with an allocation balance > 0
        CampaignCreditAllocation allocation1 = campaignCreditAllocationTF.createPersistent(campaignCredit, campaign1);
        entityManager.refresh(campaign1);
        CampaignCreditAllocation allocation2 = campaignCreditAllocationTF.create(campaignCredit, campaign1);
        validate("CampaignCreditAllocation.create", allocation2);
        assertViolationsCount(1);
        assertHasViolation("campaign");
    }

    //@Test
    public void testValidateAmount() {
        CampaignCredit campaignCredit = campaignCreditTF.create();
        campaignCredit.setAmount(BigDecimal.valueOf(100));
        campaignCreditTF.persist(campaignCredit);

        CampaignCreditAllocation allocation = campaignCreditAllocationTF.create(campaignCredit);

        allocation.setAllocatedAmount(BigDecimal.valueOf(11.11));
        validate("CampaignCreditAllocation.create", allocation);
        assertViolationsCount(0);

        allocation.setAllocatedAmount(BigDecimal.valueOf(11.111));
        validate("CampaignCreditAllocation.create", allocation);
        assertViolationsCount(1);
        assertHasViolation("allocatedAmount");

        allocation.setAllocatedAmount(BigDecimal.valueOf(0));
        validate("CampaignCreditAllocation.create", allocation);
        assertViolationsCount(1);
        assertHasViolation("allocatedAmount");

        CampaignCreditAllocation allocation1 = campaignCreditAllocationTF.create(campaignCredit);
        allocation1.setAllocatedAmount(BigDecimal.valueOf(50));
        campaignCreditAllocationTF.persist(allocation1);

        allocation.setAllocatedAmount(BigDecimal.valueOf(60));
        validate("CampaignCreditAllocation.create", allocation);
        assertViolationsCount(1);
        assertHasViolation("allocatedAmount");

        allocation.setAllocatedAmount(BigDecimal.valueOf(50));
        validate("CampaignCreditAllocation.create", allocation);
        assertViolationsCount(0);

        // can't be less than used amount
        allocation.setAllocatedAmount(BigDecimal.valueOf(10));
        campaignCreditAllocationTF.persist(allocation);
        allocation.setUsedAmount(BigDecimal.valueOf(5));
        entityManager.merge(allocation);

        allocation.setAllocatedAmount(BigDecimal.valueOf(4));
        validate("CampaignCreditAllocation.update", allocation);
        assertViolationsCount(1);
        assertHasViolation("allocatedAmount");

        allocation.setAllocatedAmount(BigDecimal.valueOf(5));
        validate("CampaignCreditAllocation.update", allocation);
        assertViolationsCount(0);

        // can't be less than used amount
        allocation.setAllocatedAmount(BigDecimal.valueOf(10));
        campaignCreditAllocationTF.persist(allocation);
        allocation.setUsedAmount(BigDecimal.valueOf(5));
        entityManager.merge(allocation);

        allocation.setAllocatedAmount(BigDecimal.valueOf(4));
        validate("CampaignCreditAllocation.update", allocation);
        assertViolationsCount(1);
        assertHasViolation("allocatedAmount");

        allocation.setAllocatedAmount(BigDecimal.valueOf(5));
        validate("CampaignCreditAllocation.update", allocation);
        assertViolationsCount(0);

        // there can be only one campaign credit allocation per campaign with an allocation balance > 0
        allocation.setAllocatedAmount(BigDecimal.valueOf(5));
        campaignCreditAllocationTF.update(allocation);

        Campaign campaign = allocation.getCampaign();
        CampaignCreditAllocation allocation2 = campaignCreditAllocationTF.createPersistent(campaignCredit, campaign);
        entityManager.refresh(campaign);

        allocation.setAllocatedAmount(BigDecimal.valueOf(6));
        validate("CampaignCreditAllocation.update", allocation);
        assertViolationsCount(1);
        assertHasViolation("allocatedAmount");
    }
}
