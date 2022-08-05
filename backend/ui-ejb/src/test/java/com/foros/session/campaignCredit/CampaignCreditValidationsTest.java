package com.foros.session.campaignCredit;

import com.foros.AbstractValidationsTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCredit;
import com.foros.model.campaign.CampaignCreditAllocation;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.CampaignCreditAllocationTestFactory;
import com.foros.test.factory.CampaignCreditTestFactory;

import java.math.BigDecimal;

import com.foros.test.factory.StatisticTestFactory;
import group.Db;
import group.Validation;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class CampaignCreditValidationsTest extends AbstractValidationsTest {
    @Autowired
    private CampaignCreditTestFactory campaignCreditTF;

    @Autowired
    private CampaignCreditAllocationTestFactory campaignCreditAllocationTF;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    private StatisticTestFactory statisticTF;

    @Test
    public void testIllegalDelete() {
        CampaignCredit campaignCredit = campaignCreditTF.createPersistent();
        validate("CampaignCredit.delete", campaignCredit.getId());
        assertViolationsCount(0);

        campaignCreditAllocationTF.createPersistent(campaignCredit);
        validate("CampaignCredit.delete", campaignCredit.getId());
        assertViolationsCount(1);
        assertHasViolation("id");
    }

    @Test
    public void testValidateAdvertiserWhenStandalone() {
        AdvertiserAccount account = advertiserAccountTF.createPersistent();
        CampaignCredit persistedCampaignCredit = campaignCreditTF.createPersistent(account);
        CampaignCredit campaignCredit = createEmptyCampaignCredit(persistedCampaignCredit);

        AdvertiserAccount advertiser = advertiserAccountTF.createPersistent();
        campaignCredit.setAdvertiser(advertiser);
        validate("CampaignCredit.update", campaignCredit);
        assertViolationsCount(1);
        assertHasViolation("advertiser");

        campaignCredit.setAdvertiser(null);
        validate("CampaignCredit.update", campaignCredit);
        assertViolationsCount(0);
    }

    @Test
    public void testValidateAdvertiser() {
        CampaignCredit persistedCampaignCredit = campaignCreditTF.createPersistent();
        CampaignCredit campaignCredit = createEmptyCampaignCredit(persistedCampaignCredit);
        AgencyAccount agencyAccount = (AgencyAccount) persistedCampaignCredit.getAccount();
        AdvertiserAccount advertiser1 = advertiserAccountTF.createPersistentAdvertiserInAgency(agencyAccount);
        AdvertiserAccount advertiser2 = advertiserAccountTF.createPersistentAdvertiserInAgency(agencyAccount);

        // deleted advertiser
        advertiserAccountTF.delete(advertiser1);
        campaignCredit.setAdvertiser(advertiser1);
        validate("CampaignCredit.update", campaignCredit);
        assertViolationsCount(1);
        assertHasViolation("advertiser");

        advertiserAccountTF.undelete(advertiser1);
        validate("CampaignCredit.update", campaignCredit);
        assertViolationsCount(0);

        // doesn't belong to agency
        AdvertiserAccount foreignAdvertiser = advertiserAccountTF.createPersistent();
        campaignCredit.setAdvertiser(foreignAdvertiser);
        validate("CampaignCredit.update", campaignCredit);
        assertViolationsCount(1);
        assertHasViolation("advertiser");

        // allocations are set for one advertiser
        campaignCreditAllocationTF.createPersistent(persistedCampaignCredit, advertiser1);

        campaignCredit.setAdvertiser(null);
        validate("CampaignCredit.update", campaignCredit);
        assertViolationsCount(0);

        campaignCredit.setAdvertiser(advertiser1);
        validate("CampaignCredit.update", campaignCredit);
        assertViolationsCount(0);

        campaignCredit.setAdvertiser(advertiser2);
        validate("CampaignCredit.update", campaignCredit);
        assertViolationsCount(1);
        assertHasViolation("advertiser");

        // allocations are set for more than one advertiser
        campaignCreditAllocationTF.createPersistent(persistedCampaignCredit, advertiser2);

        campaignCredit.setAdvertiser(null);
        validate("CampaignCredit.update", campaignCredit);
        assertViolationsCount(0);

        campaignCredit.setAdvertiser(advertiser1);
        validate("CampaignCredit.update", campaignCredit);
        assertViolationsCount(1);
        assertHasViolation("advertiser");
    }

    @Test
    public void testValidateAmountBounds() {
        CampaignCredit persistedCampaignCredit = campaignCreditTF.createPersistent();
        CampaignCredit campaignCredit = createEmptyCampaignCredit(persistedCampaignCredit);
        campaignCredit.setAccount(persistedCampaignCredit.getAccount());

        // a legal update
        campaignCredit.setAmount(BigDecimal.valueOf(11.11));
        validate("CampaignCredit.update", campaignCredit);
        assertViolationsCount(0);

        // Must be 2 decimal digits, not 3
        campaignCredit.setAmount(BigDecimal.valueOf(11.111));
        validate("CampaignCredit.update", campaignCredit);
        assertViolationsCount(1);
        assertHasViolation("amount");

        // Can not be less than 0.01
        campaignCredit.setAmount(BigDecimal.ZERO);
        validate("CampaignCredit.update", campaignCredit);
        assertViolationsCount(1);
        assertHasViolation("amount");

        // 0.01 is a legal value
        campaignCredit.setAmount(BigDecimal.valueOf(0.01));
        validate("CampaignCredit.update", campaignCredit);
        assertViolationsCount(0);

        // Must be < CampaignCredit.AMOUNT_MAX
        campaignCredit.setAmount(CampaignCredit.AMOUNT_MAX);
        validate("CampaignCredit.update", campaignCredit);
        assertViolationsCount(1);
        assertHasViolation("amount");

        campaignCredit.setAmount(CampaignCredit.AMOUNT_MAX.subtract(BigDecimal.valueOf(0.01)));
        validate("CampaignCredit.update", campaignCredit);
        assertViolationsCount(0);
    }

    @Test
    public void testValidateAmountUsage() {
        CampaignCredit persistedCampaignCredit = campaignCreditTF.createPersistent();

        CampaignCredit campaignCredit = createEmptyCampaignCredit(persistedCampaignCredit);
        campaignCredit.setAccount(persistedCampaignCredit.getAccount());

        CampaignCreditAllocation allocation = campaignCreditAllocationTF.create(campaignCredit);
        allocation.setAllocatedAmount(new BigDecimal(50));
        allocation.setUsedAmount(new BigDecimal(10));
        campaignCreditAllocationTF.persist(allocation);
        commitChangesAndClearContext();

        Set<CampaignCreditAllocation> allocations = new HashSet<>();
        allocations.add(allocation);
        campaignCredit.setAllocations(allocations);

        // Amount > AllocatedAmount = OK
        campaignCredit.setAmount(new BigDecimal(100));
        validate("CampaignCredit.update", campaignCredit);
        assertViolationsCount(0);

        // Amount < AllocatedAmount = Error
        campaignCredit.setAmount(new BigDecimal(1));
        validate("CampaignCredit.update", campaignCredit);
        assertViolationsCount(1);
        assertHasViolation("amount");

        // Amount < AllocatedAmount = Error
        campaignCredit.setAmount(new BigDecimal(25));
        validate("CampaignCredit.update", campaignCredit);
        assertViolationsCount(1);
        assertHasViolation("amount");
    }

    /** OUI-28094 It is possible to make negative balance for campaign credit */
    @Test
    public void testValidateAmountSpent() {
        // Create campaign credit with several campaign allocations
        AdvertiserAccount advertiserAccount = statisticTF.createPermanentAdvertiserAccount(BigDecimal.ZERO);
        CampaignCredit permanentCampaignCredit = statisticTF.createPermanentCampaignCredit(advertiserAccount, new BigDecimal(1000));

        // a first allocation on 500 usd
        CampaignCreative cc1 = statisticTF.createPermanentDisplayCampaignCreative(advertiserAccount);
        Campaign campaign1 = cc1.getCreativeGroup().getCampaign();
        CampaignCreditAllocation allocation1 = statisticTF.createPermanentCreditAllocation(campaign1, permanentCampaignCredit, new BigDecimal(500));

        // a second allocation on 200 usd
        CampaignCreative cc2 = statisticTF.createPermanentDisplayCampaignCreative(advertiserAccount);
        Campaign campaign2 = cc2.getCreativeGroup().getCampaign();
        CampaignCreditAllocation allocation2 = statisticTF.createPermanentCreditAllocation(campaign2, permanentCampaignCredit, new BigDecimal(300));

        // Make "Spent Amount" of the Credit greater than any separate credit allocation amount
        statisticTF.generateRequestStatsHourly(cc1.getId(), 0, 1, 0, 500, 0);
        statisticTF.generateRequestStatsHourly(cc2.getId(), 0, 1, 0, 300, 0);

        // Open Campaign credit for editing and set Amount equal to greatest allocation amount
        // Must be an error, as stated on
        // https://confluence.ocslab.com/display/TDOC/Campaign+Credit+Allocation+Edit+Screen
        //   Amount >= Utilised Amount (500 + 300 = 800)
        CampaignCredit campaignCredit = createEmptyCampaignCredit(permanentCampaignCredit);
        campaignCredit.setAmount(new BigDecimal(500));
        validate("CampaignCredit.update", campaignCredit);
        assertViolationsCount(1);
        assertHasViolation("amount");
    }

    private CampaignCredit createEmptyCampaignCredit(CampaignCredit campaignCredit) {
        CampaignCredit copy = new CampaignCredit();
        copy.setId(campaignCredit.getId());
        return copy;
    }
}
