package com.foros.session.campaignCredit;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.PaymentOrderType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCredit;
import com.foros.model.campaign.CampaignCreditAllocation;
import com.foros.model.campaign.CampaignCreditAllocationTO;
import com.foros.session.EntityTO;
import com.foros.session.account.AccountService;
import com.foros.session.finance.AdvertisingFinanceService;
import com.foros.session.status.DisplayStatusService;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.AgencyAccountTestFactory;
import com.foros.test.factory.CampaignCreditTestFactory;
import com.foros.test.factory.StatisticTestFactory;
import group.Db;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;

@Category(Db.class)
public class CampaignCreditServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private CampaignCreditService campaignCreditService;

    @Autowired
    private CampaignCreditAllocationService campaignCreditAllocationService;

    @Autowired
    private CampaignCreditTestFactory campaignCreditTF;

    @Autowired
    private AgencyAccountTestFactory agencyAccountTF;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    private DisplayStatusService displayStatusService;

    @Autowired
    private AdvertisingFinanceService advertisingFinanceService;

    @Autowired
    private StatisticTestFactory statisticTF;

    @Test
    public void testFindById() {
        CampaignCredit campaignCredit = campaignCreditTF.createPersistent();

        Long id = campaignCredit.getId();
        campaignCredit = campaignCreditService.find(id);

        assertEquals("Can't find campaign credit <" + id + ">", id, campaignCredit.getId());
    }

    @Test
    public void testCreate() {
        CampaignCredit campaignCredit = campaignCreditTF.create();

        Long id = campaignCreditService.create(campaignCredit);

        assertNotNull("ID wasn't set", campaignCredit.getId());
        assertEquals(id, campaignCredit.getId());
    }

    @Test
    public void testUpdate() {
        CampaignCredit campaignCredit = campaignCreditTF.createPersistent();

        Long id = campaignCredit.getId();
        campaignCredit = campaignCreditService.find(id);
        campaignCredit.setAmount(BigDecimal.valueOf(111));

        campaignCredit = campaignCreditService.update(campaignCredit);

        assertEquals(BigDecimal.valueOf(111), campaignCredit.getAmount());
    }

    @Test
    public void testDelete() {
        CampaignCredit campaignCredit = campaignCreditTF.createPersistent();

        Long id = campaignCredit.getId();
        campaignCreditService.delete(id);

        try {
            campaignCreditService.find(id);
            fail("Campaign credit is not deleted");
        } catch (EntityNotFoundException e) {
            //expected
        }
    }

    @Test
    public void testFindCampaignCreditsByAccount() {
        AgencyAccount account = agencyAccountTF.createPersistent();
        campaignCreditTF.createPersistent(account);
        campaignCreditTF.createPersistent(account);

        List<CampaignCreditTO> campaignCredits = campaignCreditService.findCampaignCredits(account.getId());

        assertEquals(2, campaignCredits.size());

        assertTrue(campaignCreditService.hasCampaignCredits(account.getId()));
    }

    @Test
    public void testChangeCampaignStatusAfterAddCredit() throws Exception {
        // Create new advertiser, the Test Factory set it's Prepaid to 10 so it is LIVE
        AdvertiserAccount advertiser = advertiserAccountTF.createPersistent();
        commitChangesAndClearContext();
        advertiser = accountService.findAdvertiserAccount(advertiser.getId());
        assertEquals(Account.LIVE, advertiser.getDisplayStatus());

        // Set it's Prepaid Amount to 0
        advertiser.getFinancialSettings().setPaymentType(PaymentOrderType.PREPAY);
        advertiser.getFinancialSettings().getData().setPrepaidAmount(BigDecimal.ZERO);
        accountService.updateExternalAccount(advertiser);
        commitChangesAndClearContext();
        advertiser = accountService.findAdvertiserAccount(advertiser.getId());
        assertEquals(Account.NOT_LIVE, advertiser.getDisplayStatus());

        // Add a Campaign Credit with some non zero Amount to the Account
        CampaignCredit campaignCredit = campaignCreditTF.create(advertiser);
        Long campaignCreditId = campaignCreditService.create(campaignCredit);
        commitChangesAndClearContext();
        advertiser = accountService.findAdvertiserAccount(advertiser.getId());
        assertEquals(Account.LIVE, advertiser.getDisplayStatus());

        // Update the Campaign Credit
        campaignCredit = campaignCreditTF.create(advertiser);
        campaignCredit.setId(campaignCreditId);
        campaignCredit.setAmount(BigDecimal.valueOf(100));
        campaignCreditService.update(campaignCredit);
        commitChangesAndClearContext();
        advertiser = accountService.findAdvertiserAccount(advertiser.getId());
        assertEquals(Account.LIVE, advertiser.getDisplayStatus());
        assertEquals(100.0, advertisingFinanceService.getCreditBalance(advertiser.getId()).doubleValue(), 0.001);

        // At last delete it
        campaignCreditService.delete(campaignCreditId);
        commitChangesAndClearContext();
        advertiser = accountService.findAdvertiserAccount(advertiser.getId());
        assertEquals(Account.NOT_LIVE, advertiser.getDisplayStatus());
        assertEquals(0, advertisingFinanceService.getCreditBalance(advertiser.getId()).doubleValue(), 0.001);
    }

    @Test
    public void testGetCampaignsForCreditAllocation() {
        // Create two Campaign Credits and spend the first one fully
        AdvertiserAccount advertiserAccount = statisticTF.createPermanentAdvertiserAccount(BigDecimal.ZERO);
        CampaignCredit campaignCredit1 = statisticTF.createPermanentCampaignCredit(advertiserAccount, new BigDecimal(20));

        // Create two campaigns and allocate amounts which sum more then campaign credit
        CampaignCreative cc1 = statisticTF.createPermanentDisplayCampaignCreative(advertiserAccount);
        Campaign campaign1 = cc1.getCreativeGroup().getCampaign();
        CampaignCreditAllocation allocation1 = statisticTF.createPermanentCreditAllocation(campaign1, campaignCredit1, new BigDecimal(15));

        CampaignCreative cc2 = statisticTF.createPermanentDisplayCampaignCreative(advertiserAccount);
        Campaign campaign2 = cc2.getCreativeGroup().getCampaign();
        CampaignCreditAllocation allocation2 = statisticTF.createPermanentCreditAllocation(campaign2, campaignCredit1, new BigDecimal(15));

        // Fully spend the first credit
        // spend 25 usd and create (25-15)=10 usd as overrun credit for Campaign 1
        statisticTF.generateRequestStatsHourly(cc1.getId(), 0, 1, 0, 25, 0);

        // spend (25-20)=5 usd and create (15-5)=10 usd as overrun credit for Campaign 2
        statisticTF.generateRequestStatsHourly(cc2.getId(), 0, 1, 0, 25, 0);

        // There must be 3 credits now, an original one and two overdeliveries
        List<CampaignCreditTO> credits = campaignCreditService.findCampaignCredits(advertiserAccount.getId());
        assertEquals(3, credits.size());

        // Original credit is fully spent
        CampaignCreditStatsTO stats1 = campaignCreditService.getStats(campaignCredit1.getId());
        assertEquals(0, stats1.getAvailableAmount().intValue());

        // Campaign 1 doesn't have active allocations now, so there must be it
        List<EntityTO> campaigns = campaignCreditService.getCampaignsForCreditAllocation(advertiserAccount.getId());
        assertEquals(1, campaigns.size());
        assertEquals(campaign1.getId(), campaigns.get(0).getId());

        // Create a second Campaign credit
        CampaignCredit campaignCredit2 = statisticTF.createPermanentCampaignCredit(advertiserAccount, new BigDecimal(100));

        // Again, only Campaign 1 doesn't have active allocations, so there must be it
        campaigns = campaignCreditService.getCampaignsForCreditAllocation(advertiserAccount.getId());
        assertEquals(1, campaigns.size());
        assertEquals(campaign1.getId(), campaigns.get(0).getId());

        // Campaign 2 has active credit allocation, so the result must be not NULL
        // But, from the other side, the Campaign credit is fully spent, so UI doesn't show the campaign's credit allocation
        // This contradiction (between the specification and an user's expectation) is filed in TDOC-1880
        CampaignCreditAllocationTO to = campaignCreditAllocationService.findCreditAllocationForCampaign(campaign2.getId());
        assertNull(to);
    }
}
