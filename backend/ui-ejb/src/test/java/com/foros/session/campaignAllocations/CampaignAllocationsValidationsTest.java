package com.foros.session.campaignAllocations;

import com.foros.AbstractValidationsTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignAllocation;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.opportunity.Opportunity;
import com.foros.model.opportunity.Probability;
import com.foros.session.campaignAllocation.CampaignAllocationService;
import com.foros.session.opportunity.OpportunityService;
import com.foros.test.factory.DisplayCampaignTestFactory;
import com.foros.test.factory.DisplayCreativeLinkTestFactory;
import com.foros.test.factory.OpportunityTestFactory;

import com.foros.test.factory.StatisticTestFactory;
import com.foros.test.factory.TextCreativeLinkTestFactory;
import com.foros.util.EntityUtils;
import group.Db;
import group.Validation;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Category({ Db.class, Validation.class })
public class CampaignAllocationsValidationsTest extends AbstractValidationsTest {
    @Autowired
    private DisplayCampaignTestFactory displayCampaignTF;

    @Autowired
    private DisplayCreativeLinkTestFactory displayCreativeLinkTF;

    @Autowired
    private TextCreativeLinkTestFactory textCreativeLinkTF;

    @Autowired
    private OpportunityTestFactory opportunityTF;

    @Autowired
    private StatisticTestFactory statisticTF;

    @Autowired
    private OpportunityService opportunityService;

    @Autowired
    private CampaignAllocationService campaignAllocationService;

    private Long campaignId;
    private AdvertiserAccount account;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        Campaign campaign = displayCampaignTF.createPersistent();
        campaignId = campaign.getId();
        account = campaign.getAccount();
    }

    @Test
    public void testValidUpdate() {
        Set<CampaignAllocation> allocations = new LinkedHashSet<>();
        allocations.add(createAllocation(createOpportunity(BigDecimal.TEN), 0L, new BigDecimal(1)));
        validate(allocations);
        assertViolationsCount(0);
    }

    @Test
    public void testValidateMaxCount() {
        Opportunity opportunity = createOpportunity(BigDecimal.TEN);
        Set<CampaignAllocation> allocations = new LinkedHashSet<>();
        allocations.add(createAllocation(opportunity, 0L, new BigDecimal(1)));
        allocations.add(createAllocation(opportunity, 1L, new BigDecimal(1)));
        allocations.add(createAllocation(opportunity, 2L, new BigDecimal(1)));
        allocations.add(createAllocation(opportunity, 3L, new BigDecimal(1)));
        allocations.add(createAllocation(opportunity, 4L, new BigDecimal(1)));
        validate(allocations);
        assertViolationsCount(1);
        assertHasViolation("campaignAllocations.size");
    }

    @Test
    public void testValidateBalance() {
        Opportunity opportunity = createOpportunity(BigDecimal.TEN);
        Set<CampaignAllocation> allocations = new LinkedHashSet<>();
        allocations.add(createAllocation(opportunity, 0L, new BigDecimal(1)));
        allocations.add(createAllocation(opportunity, 1L, new BigDecimal(100)));
        validate(allocations);
        assertViolationsCount(1);
        assertHasViolation("campaignAllocations[1].amount");
    }

    @Test
    public void testValidateDuplicateOrders() {
        Opportunity opportunity = createOpportunity(BigDecimal.TEN);
        Set<CampaignAllocation> allocations = new LinkedHashSet<>();
        allocations.add(createAllocation(opportunity, 0L, new BigDecimal(1)));
        allocations.add(createAllocation(opportunity, 1L, new BigDecimal(1)));
        allocations.add(createAllocation(opportunity, 1L, new BigDecimal(1)));

        Campaign campaign = new Campaign(campaignId);
        campaign.setAllocations(allocations);
        validate("CampaignAllocations.createUpdate", campaign);
        assertViolationsCount(1);
        assertHasViolation("campaignAllocations[2].order");
    }

    @Test
    public void testValidateAmount() {
        // required constraint
        Set<CampaignAllocation> allocations = new LinkedHashSet<>();
        allocations.add(createAllocation(createOpportunity(BigDecimal.TEN), 0L, null));
        validate(allocations);
        assertViolationsCount(1);
        assertHasViolation("campaignAllocations[0].amount");

        // fraction digits
        allocations = new LinkedHashSet<>();
        allocations.add(createAllocation(createOpportunity(BigDecimal.TEN), 0L, new BigDecimal(9.123)));
        validate(allocations);
        assertViolationsCount(1);
        assertHasViolation("campaignAllocations[0].amount");

        // greater than 0
        allocations = new LinkedHashSet<>();
        allocations.add(createAllocation(createOpportunity(BigDecimal.TEN), 0L, BigDecimal.ZERO));
        validate(allocations);
        assertViolationsCount(1);
        assertHasViolation("campaignAllocations[0].amount");
    }

    @Test
    public void testValidateOpportunity() {
        // required
        Set<CampaignAllocation> allocations = new LinkedHashSet<>();
        allocations.add(createAllocation(null, 0L, new BigDecimal(200)));
        validate(allocations);
        assertViolationsCount(1);
        assertHasViolation("campaignAllocations[0].opportunity");

        // invalid status
        Opportunity invalidOpp1 = opportunityTF.create(account, new BigDecimal(1000));
        invalidOpp1.setProbability(Probability.BRIEF_RECEIVED);
        opportunityTF.persist(invalidOpp1);

        allocations = new LinkedHashSet<>();
        allocations.add(createAllocation(invalidOpp1, 0L, new BigDecimal(200)));
        validate(allocations);
        assertViolationsCount(1);
        assertHasViolation("campaignAllocations[0].opportunity");

        // invalid account
        Opportunity invalidOpp2 = opportunityTF.create();
        invalidOpp2.setProbability(Probability.LIVE);
        invalidOpp2.setAmount(new BigDecimal(1000));
        opportunityTF.persist(invalidOpp2);

        allocations = new LinkedHashSet<>();
        allocations.add(createAllocation(invalidOpp2, 0L, new BigDecimal(200)));
        validate(allocations);
        assertViolationsCount(1);
        assertHasViolation("campaignAllocations[0].opportunity");
    }

    @Test
    public void testValidateOrder() {
        // required
        Set<CampaignAllocation> allocations = new LinkedHashSet<>();
        allocations.add(createAllocation(createOpportunity(BigDecimal.TEN), null, BigDecimal.ONE));
        validate(allocations);
        assertViolationsCount(1);
        assertHasViolation("campaignAllocations[0].order");

        // range, simple version
        allocations = new LinkedHashSet<>();
        allocations.add(createAllocation(createOpportunity(BigDecimal.TEN), 2L, BigDecimal.ONE));
        validate(allocations);
        assertViolationsCount(1);
        assertHasViolation("campaignAllocations[0].order");
    }

    @Test
    public void testChangingOrder() {
        // changing order of existing allocation
        Opportunity opportunity = createOpportunity(BigDecimal.TEN);
        Campaign campaign = displayCampaignTF.createPersistent(account);
        List<CampaignAllocation> persistedAllocations = createPersistedAllocation(opportunity, campaign, 1L, new BigDecimal(1));
        CampaignAllocation persistedAllocation = persistedAllocations.get(0);
        Set<CampaignAllocation> allocations = new LinkedHashSet<>();
        CampaignAllocation allocation = clonePersistedAllocation(persistedAllocation);
        allocations.add(allocation);
        allocations.add(createAllocation(opportunity, 1L, new BigDecimal(2)));
        validate(allocations);
        assertViolationsCount(0);

        campaign = displayCampaignTF.find(campaign.getId());
        campaign.setAllocations(allocations);
        campaignAllocationService.updateCampaignAllocations(campaign);

        // orders must exchange, i.e. initially first allocation must be second now
        Map<Long, CampaignAllocation> map = EntityUtils.mapEntityIds(campaign.getAllocations());
        assertEquals(2L, map.get(persistedAllocation.getId()).getOrder().longValue());
    }

    @Test
    public void testIllegalDelete() {
        AdvertiserAccount advertiserAccount = statisticTF.createPermanentAdvertiserAccount(BigDecimal.ZERO);
        Opportunity opportunity = statisticTF.createPermanentOpportunity(advertiserAccount, new BigDecimal(200));
        CampaignCreative campaignCreative = statisticTF.createPermanentDisplayCampaignCreative(advertiserAccount);
        Campaign campaign = campaignCreative.getCreativeGroup().getCampaign();
        CampaignAllocation allocation = statisticTF.createPermanentAllocation(campaign, opportunity, 1, new BigDecimal(100));

        // generate 1 click to spend 10 coins from the allocation
        statisticTF.generateRequestStatsHourly(campaignCreative.getId(), 0, 1, 0, 10, 0);

        // trying to delete an allocation with spent money - must be an error
        Set<CampaignAllocation> allocations = new LinkedHashSet<>();
        allocations.add(createAllocation(opportunity, 1L, new BigDecimal(1)));
        campaign.setAllocations(allocations);
        validate("CampaignAllocations.createUpdate", campaign);
        assertViolationsCount(1);
        assertHasViolation("campaignAllocations");
    }

    @Test
    public void testAddDepletedIO() {
        // Create opportunity, link it fully to campaign and spend it fully.
        AdvertiserAccount advertiserAccount = statisticTF.createPermanentAdvertiserAccount(BigDecimal.ZERO);
        Opportunity opportunity = statisticTF.createPermanentOpportunity(advertiserAccount, new BigDecimal(10));
        CampaignCreative campaignCreative = statisticTF.createPermanentDisplayCampaignCreative(advertiserAccount);
        Campaign campaign = campaignCreative.getCreativeGroup().getCampaign();
        CampaignAllocation allocation = statisticTF.createPermanentAllocation(campaign, opportunity, 1, new BigDecimal(10));
        statisticTF.generateRequestStatsHourly(campaignCreative.getId(), 0, 1, 0, 10, 0);

        // Must be impossible to add depleted IO as an allocation
        Set<CampaignAllocation> allocations = new LinkedHashSet<>();
        allocations.add(createAllocation(opportunity, 1L, new BigDecimal(1)));
        campaign.setAllocations(allocations);
        validate("CampaignAllocations.createUpdate", campaign);
        assertViolationsCount(1);
        assertHasViolation("campaignAllocations[0].opportunity");
    }

    private void validate(Set<CampaignAllocation> allocations) {
        Campaign campaign = new Campaign(campaignId);
        campaign.setAllocations(allocations);
        validate("CampaignAllocations.createUpdate", campaign);
    }

    private List<CampaignAllocation> createPersistedAllocation(Opportunity opportunity, Campaign campaign, Long order, BigDecimal amount) {
        Set<CampaignAllocation> allocations = new LinkedHashSet<>();
        allocations.add(createAllocation(opportunity, order, amount));
        campaign.setAllocations(allocations);

        campaignAllocationService.updateCampaignAllocations(campaign);
        commitChangesAndClearContext();

        return campaignAllocationService.getCampaignAllocations(campaign.getId());
    }

    private CampaignAllocation clonePersistedAllocation(CampaignAllocation persistedAllocation) {
        CampaignAllocation allocation = new CampaignAllocation();
        allocation.setId(persistedAllocation.getId());
        Opportunity opportunity = new Opportunity();
        opportunity.setId(persistedAllocation.getOpportunity().getId());
        allocation.setStatus(persistedAllocation.getStatus());
        allocation.setOpportunity(opportunity);
        allocation.setOrder(persistedAllocation.getOrder());
        allocation.setAmount(persistedAllocation.getAmount());
        allocation.setVersion(persistedAllocation.getVersion());
        return allocation;
    }

    private CampaignAllocation createAllocation(Opportunity opportunity, Long order, BigDecimal amount) {
        CampaignAllocation allocation = new CampaignAllocation();
        allocation.setOpportunity(opportunity);
        allocation.setOrder(order);
        allocation.setAmount(amount);
        return allocation;
    }

    public Opportunity createOpportunity(BigDecimal amount) {
        Opportunity opportunity  = opportunityTF.createPersistent(account, amount);
        clearContext();

        return opportunityService.find(opportunity.getId());
    }
}
