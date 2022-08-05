package com.foros.session.campaignAllocations;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignAllocation;
import com.foros.model.campaign.CampaignAllocationStatus;
import com.foros.model.campaign.CampaignAllocationsTotalTO;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.opportunity.Opportunity;
import com.foros.session.campaignAllocation.CampaignAllocationService;
import com.foros.session.campaignAllocation.OpportunityTO;
import com.foros.test.factory.DisplayCampaignTestFactory;
import com.foros.test.factory.StatisticTestFactory;
import com.foros.util.EntityUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CampaignAllocationServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private DisplayCampaignTestFactory displayCampaignTF;

    @Autowired
    private CampaignAllocationService campaignAllocationService;

    @Autowired
    private StatisticTestFactory statisticTF;

    @Test
    public void testCampaignAllocationsTotal() {
        AdvertiserAccount advertiserAccount = statisticTF.createPermanentAdvertiserAccount(BigDecimal.ZERO);

        Opportunity opportunity1 = statisticTF.createPermanentOpportunity(advertiserAccount, new BigDecimal(1000));
        Opportunity opportunity2 = statisticTF.createPermanentOpportunity(advertiserAccount, new BigDecimal(5000));

        CampaignCreative displayCampaignCreative = statisticTF.createPermanentDisplayCampaignCreative(advertiserAccount);
        Campaign campaign = displayCampaignCreative.getCreativeGroup().getCampaign();

        CampaignAllocation allocation = statisticTF.createPermanentAllocation(campaign, opportunity1, 1, new BigDecimal(100));
        statisticTF.createPermanentAllocation(campaign, opportunity1, 2, new BigDecimal(200));
        statisticTF.createPermanentAllocation(campaign, opportunity2, 3, new BigDecimal(300));
        statisticTF.createPermanentAllocation(campaign, opportunity2, 4, new BigDecimal(400));

        // spend 100 coins from the first allocation
        statisticTF.generateRequestStatsHourly(displayCampaignCreative.getId(), 0, 1, 0, 50, 0);

        campaign = displayCampaignTF.find(campaign.getId());
        assertEquals(4, campaign.getAllocations().size());

        Map<Long, CampaignAllocation> map = EntityUtils.mapEntityIds(campaign.getAllocations());
        assertEquals(50, map.get(allocation.getId()).getUtilizedAmount().intValue());
        assertEquals(CampaignAllocationStatus.ACTIVE, map.get(allocation.getId()).getStatus());

        CampaignAllocationsTotalTO totals = campaignAllocationService.getCampaignAllocationsTotal(campaign.getId());
        assertEquals(4, totals.getAllocations().size());
        assertEquals(100+200+300+400, totals.getAmount().intValue());
        assertEquals(50, totals.getUtilisedAmount().intValue());
        assertEquals((100-50)+200+300+400, totals.getAvailableAmount().intValue());
    }

    @Test
    public void getAvailableOpportunities() {
        // Create opportunity, link it fully to campaign and spend it fully.
        AdvertiserAccount advertiserAccount = statisticTF.createPermanentAdvertiserAccount(BigDecimal.ZERO);
        Opportunity opportunity = statisticTF.createPermanentOpportunity(advertiserAccount, new BigDecimal(10));
        CampaignCreative displayCampaignCreative = statisticTF.createPermanentDisplayCampaignCreative(advertiserAccount);
        Campaign campaign = displayCampaignCreative.getCreativeGroup().getCampaign();
        CampaignAllocation allocation = statisticTF.createPermanentAllocation(campaign, opportunity, 1, new BigDecimal(10));
        statisticTF.generateRequestStatsHourly(displayCampaignCreative.getId(), 0, 1, 0, 10, 0);

        // Open allocation edit page of campaign and check if IO from your opportunity is presented in I/O list
        // Expected Result:
        //   IO from your opportunity is NOT presented in I/O list
        //   or at least it is impossible to add this IO as allocation
        List<OpportunityTO> availableOpportunities = campaignAllocationService.getAvailableOpportunities(advertiserAccount.getId());
        assertTrue(availableOpportunities.isEmpty());
    }
}
