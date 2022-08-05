package com.foros.session.campaignCredit;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCredit;
import com.foros.model.campaign.CampaignCreditAllocation;
import com.foros.model.campaign.CampaignCreditAllocationTO;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.AgencyAccountTestFactory;
import com.foros.test.factory.AgencyAccountTypeTestFactory;
import com.foros.test.factory.BehavioralChannelTestFactory;
import com.foros.test.factory.CampaignCreditAllocationTestFactory;
import com.foros.test.factory.CampaignCreditTestFactory;
import com.foros.test.factory.DisplayCCGTestFactory;
import com.foros.test.factory.DisplayCampaignTestFactory;
import com.foros.test.factory.DisplayCreativeLinkTestFactory;

import com.foros.test.factory.StatisticTestFactory;
import group.Db;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class CampaignCreditAllocationServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private CampaignCreditAllocationService campaignCreditAllocationService;

    @Autowired
    private CampaignCreditAllocationTestFactory campaignCreditAllocationTF;

    @Autowired
    private CampaignCreditTestFactory campaignCreditTF;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    private AgencyAccountTestFactory agencyAccountTF;

    @Autowired
    private DisplayCampaignTestFactory displayCampaignTF;

    @Autowired
    private DisplayCCGTestFactory displayCCGTestFactory;

    @Autowired
    private DisplayCreativeLinkTestFactory creativeLinkTF;

    @Autowired
    private AgencyAccountTypeTestFactory  accountTypeTF;

    @Autowired
    private CampaignCreativeGroupService campaignCreativeGroupService;

    @Autowired
    private BehavioralChannelTestFactory behavioralChannelTF;

    @Autowired
    private StatisticTestFactory statisticTF;

    @Test
    public void testFindById() {
        CampaignCreditAllocation allocation = campaignCreditAllocationTF.createPersistent();

        Long id = allocation.getId();
        allocation = campaignCreditAllocationService.find(id);

        assertEquals("Can't find campaign credit allocation <" + id + ">", id, allocation.getId());
    }

    @Test
    public void testCreate() {
        CampaignCreditAllocation allocation = campaignCreditAllocationTF.create();

        Long id = campaignCreditAllocationService.create(allocation);

        assertNotNull("ID wasn't set", allocation.getId());
        assertEquals(id, allocation.getId());
        Campaign campaign = allocation.getCampaign();
        assertTrue("CreditAllocation should be added to Campaign", campaign.getCreditAllocations().contains(allocation));
    }

    @Test
    public void testUpdate() {
        CampaignCreditAllocation allocation = campaignCreditAllocationTF.createPersistent();

        Long id = allocation.getId();
        allocation = campaignCreditAllocationService.find(id);
        allocation.setAllocatedAmount(BigDecimal.valueOf(111));

        allocation = campaignCreditAllocationService.update(allocation);

        assertEquals(BigDecimal.valueOf(111), allocation.getAllocatedAmount());
    }

    @Test
    public void testFindAllocationsByCampaignCredit() {
        CampaignCredit campaignCredit = campaignCreditTF.createPersistent();
        campaignCreditAllocationTF.createPersistent(campaignCredit);
        campaignCreditAllocationTF.createPersistent(campaignCredit);

        List<CampaignCreditAllocationTO> allocations = campaignCreditAllocationService.findCreditAllocations(campaignCredit.getId());
        assertEquals(2, allocations.size());
    }

    @Test
    public void testFindAllocationByCampaign() {
        AgencyAccount agencyAccount = agencyAccountTF.createPersistent();
        CampaignCredit campaignCredit = campaignCreditTF.createPersistent(agencyAccount);
        AdvertiserAccount advertiser = advertiserAccountTF.createPersistentAdvertiserInAgency(agencyAccount);
        Campaign campaign1 = displayCampaignTF.createPersistent(advertiser);
        Campaign campaign2 = displayCampaignTF.createPersistent(advertiser);

        CampaignCreditAllocation allocation1 = campaignCreditAllocationTF.createPersistent(campaignCredit, campaign1);
        CampaignCreditAllocation allocation2 = campaignCreditAllocationTF.createPersistent(campaignCredit, campaign2);
        Long id = allocation1.getId();

        CampaignCreditAllocationTO allocation = campaignCreditAllocationService.findCreditAllocationForCampaign(campaign1.getId());

        assertEquals(id, allocation.getId());
    }

    @Test
    public void testCampaignCreditPartialUtilization() {
        AdvertiserAccount advertiserAccount = statisticTF.createPermanentAdvertiserAccount(BigDecimal.ZERO);
        CampaignCredit campaignCredit = statisticTF.createPermanentCampaignCredit(advertiserAccount, new BigDecimal(100));

        // Share the credit among two companies
        CampaignCreative cc1 = statisticTF.createPermanentDisplayCampaignCreative(advertiserAccount);
        Campaign campaign1 = cc1.getCreativeGroup().getCampaign();
        CampaignCreditAllocation allocation1 = statisticTF.createPermanentCreditAllocation(campaign1, campaignCredit, new BigDecimal(100));

        CampaignCreative cc2 = statisticTF.createPermanentDisplayCampaignCreative(advertiserAccount);
        Campaign campaign2 = cc2.getCreativeGroup().getCampaign();
        CampaignCreditAllocation allocation2 = statisticTF.createPermanentCreditAllocation(campaign2, campaignCredit, new BigDecimal(100));

        // Utilize amount 50 by 2nd campaign
        statisticTF.generateRequestStatsHourly(cc2.getId(), 0, 1, 0, 50, 0);

        // Open 1st campaign and look at its allocation
        // Expected Result: Available amount is 50 as on campaign credit view page
        CampaignCreditAllocationTO to1 = campaignCreditAllocationService.findCreditAllocationForCampaign(campaign1.getId());
        assertEquals(50, to1.getAvailableAmount().intValue());
    }

    @Test
    public void testCampaignCreditFullUtilization() {
        AdvertiserAccount advertiserAccount = statisticTF.createPermanentAdvertiserAccount(BigDecimal.ZERO);
        CampaignCredit campaignCredit = statisticTF.createPermanentCampaignCredit(advertiserAccount, new BigDecimal(1));

        // Share the credit among two companies
        CampaignCreative cc1 = statisticTF.createPermanentDisplayCampaignCreative(advertiserAccount);
        Campaign campaign1 = cc1.getCreativeGroup().getCampaign();
        CampaignCreditAllocation allocation1 = statisticTF.createPermanentCreditAllocation(campaign1, campaignCredit, new BigDecimal(1));

        CampaignCreative cc2 = statisticTF.createPermanentDisplayCampaignCreative(advertiserAccount);
        Campaign campaign2 = cc2.getCreativeGroup().getCampaign();
        CampaignCreditAllocation allocation2 = statisticTF.createPermanentCreditAllocation(campaign2, campaignCredit, new BigDecimal(1));

        // Fully spend amount 1 in 2nd campaign and open 1st campaign view page
        statisticTF.generateRequestStatsHourly(cc2.getId(), 0, 1, 0, 1, 0);

        // Expected Result: No credit allocation on campaign view page
        CampaignCreditAllocationTO to1 = campaignCreditAllocationService.findCreditAllocationForCampaign(campaign1.getId());
        assertEquals(null, to1);
    }
}
