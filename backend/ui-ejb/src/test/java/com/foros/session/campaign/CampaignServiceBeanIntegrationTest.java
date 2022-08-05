package com.foros.session.campaign;

import static com.foros.util.UploadUtils.UPLOAD_CONTEXT;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.DisplayStatus;
import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingFinancialSettings;
import com.foros.model.account.AgencyAccount;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CampaignSchedule;
import com.foros.model.campaign.CcgRate;
import com.foros.model.campaign.RateType;
import com.foros.model.campaign.TGTType;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.creative.Creative;
import com.foros.model.security.AccountType;
import com.foros.model.security.User;
import com.foros.service.mock.AdvertisingFinanceServiceMock;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.OperationType;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.AdvertiserAccountTypeTestFactory;
import com.foros.test.factory.AgencyAccountTestFactory;
import com.foros.test.factory.AgencyAccountTypeTestFactory;
import com.foros.test.factory.BehavioralChannelTestFactory;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.CreativeTemplateTestFactory;
import com.foros.test.factory.DisplayCCGTestFactory;
import com.foros.test.factory.DisplayCampaignTestFactory;
import com.foros.test.factory.DisplayCreativeLinkTestFactory;
import com.foros.test.factory.TextCCGTestFactory;
import com.foros.test.factory.TextCampaignTestFactory;
import com.foros.test.factory.TextCreativeTestFactory;
import com.foros.test.factory.UserTestFactory;
import com.foros.util.DateUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import group.Db;
import group.Validation;

@Category({ Db.class, Validation.class })
public class CampaignServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private CampaignService campaignService;

    @Autowired
    private DisplayCampaignTestFactory displayCampaignTF;

    @Autowired
    private TextCampaignTestFactory textCampaignTF;

    @Autowired
    private DisplayCCGTestFactory displayCCGTF;

    @Autowired
    private TextCCGTestFactory textCCGTF;

    @Autowired
    private CreativeSizeTestFactory creativeSizeTF;

    @Autowired
    private CreativeTemplateTestFactory creativeTemplateTF;

    @Autowired
    private AgencyAccountTestFactory agencyAccountTF;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    private AdvertiserAccountTypeTestFactory advertiserAccountTypeTF;

    @Autowired
    private UserTestFactory userTF;

    @Autowired
    private TextCreativeTestFactory textCreativeTF;

    @Autowired
    private DisplayCreativeLinkTestFactory creativeLinkTF;

    @Autowired
    private AgencyAccountTypeTestFactory accountTypeTF;

    @Autowired
    private CampaignCreativeGroupService campaignCreativeGroupService;

    @Autowired
    private AdvertisingFinanceServiceMock advertisingFinanceService;

    @Autowired
    private BehavioralChannelTestFactory behavioralChannelTF;

    @Test
    public void testFindById() {
        Campaign campaign = displayCampaignTF.createPersistent();

        Long id = campaign.getId();
        campaign = campaignService.find(id);

        assertEquals("Can't find campaign <" + id + ">", id, campaign.getId());
    }

    @Test
    public void testCreate() {
        Campaign campaign = displayCampaignTF.createPersistent();

        assertNotNull("ID wasn't set", campaign.getId());
        assertEquals("Status wasn't set", Status.ACTIVE, campaign.getStatus());
    }

    @Test
    public void testCreateCopy() throws IOException {
        AgencyAccount agencyAccount = agencyAccountTF.createPersistent();
        AdvertiserAccount advertiserAccount = advertiserAccountTF.createPersistentAdvertiserInAgency(agencyAccount);
        Campaign campaign = displayCampaignTF.create(advertiserAccount);
        campaignService.create(campaign);
        commitChanges();

        AccountType accountType = agencyAccount.getAccountType();
        accountType.setIoManagement(false);
        accountTypeTF.update(accountType);
        commitChanges();

        AdvertisingFinancialSettings financialSettings = advertisingFinanceService.getFinancialSettings(agencyAccount.getId());
        BigDecimal commission = BigDecimal.valueOf(0.23);
        financialSettings.setCommission(commission);
        advertisingFinanceService.updateFinance(financialSettings);
        commitChanges();

        campaign = campaignService.find(campaign.getId());
        createCampaignCreativeGroup(campaign);

        commitChanges();
        clearContext();

        Long copyId = campaignService.createCopy(campaign.getId());
        assertFalse("ID of the copy should be different", campaign.getId().equals(copyId));

        BigDecimal newCommission = campaignService.find(copyId).getCommission();
        assertTrue(commission.compareTo(newCommission) == 0);
        assertNotSame(campaign.getCommission(), newCommission);
    }

    @Test
    public void testCreateCopyBudget() throws IOException {
        AgencyAccount agencyAccount = agencyAccountTF.createPersistent();
        AdvertiserAccount advertiserAccount = advertiserAccountTF.createPersistentAdvertiserInAgency(agencyAccount);
        Campaign campaign = displayCampaignTF.create(advertiserAccount);
        BigDecimal budget = BigDecimal.TEN;
        campaign.setBudget(budget);
        campaignService.create(campaign);
        commitChanges();

        AccountType accountType = agencyAccount.getAccountType();
        accountType.setIoManagement(false);
        accountTypeTF.update(accountType);
        commitChanges();

        Long copyId = campaignService.createCopy(campaign.getId());
        BigDecimal newBudget = campaignService.find(copyId).getBudget();
        assertEquals(budget, newBudget);

        accountType.setIoManagement(true);
        accountTypeTF.update(accountType);
        commitChanges();

        copyId = campaignService.createCopy(campaign.getId());
        newBudget = campaignService.find(copyId).getBudget();
        assertEquals(BigDecimal.ZERO, newBudget);
    }

    @Test
    public void testPerform() {
        AccountType accountType = advertiserAccountTypeTF.create();
        accountType.setIoManagement(false);
        advertiserAccountTypeTF.persist(accountType);
        Long forUpdateId = textCampaignTF.createPersistent(accountType).getId();
        commitChanges();

        Operations<Campaign> operations = new Operations<Campaign>();

        Campaign campaignUpdated = campaignService.find(forUpdateId);
        Campaign campaignCreated = textCampaignTF.create(accountType);

        operations.getOperations().add(createOperation(campaignUpdated, OperationType.UPDATE));
        operations.getOperations().add(createOperation(campaignCreated, OperationType.CREATE));

        commitChanges();

        OperationsResult result = campaignService.perform(operations);

        assertEquals(2, result.getIds().size());
        assertEquals(campaignUpdated.getId(), result.getIds().get(0));
        assertNotNull(result.getIds().get(1));
    }

    @Test
    public void testSearchCampaigns() {
        setDeletedObjectsVisible(true);
        AgencyAccount agencyAccount = agencyAccountTF.createPersistent();
        AdvertiserAccount advertiserAccount = advertiserAccountTF.createPersistentAdvertiserInAgency(agencyAccount);
        Campaign campaign = displayCampaignTF.create(advertiserAccount);
        campaignService.create(campaign);
        commitChanges();

        campaign = campaignService.find(campaign.getId());
        clearContext();
        campaignService.delete(campaign.getId());
        commitChanges();

        List<TreeFilterElementTO> campaigns = campaignService.searchCampaigns(advertiserAccount.getId(), true, true);
        assertTrue(campaigns.size() == 1);

        setDeletedObjectsVisible(false);
        campaigns = campaignService.searchCampaigns(advertiserAccount.getId(), true, true);
        assertTrue(campaigns.size() == 0);
    }

    @Test
    public void testScenario() {
        Campaign campaign = displayCampaignTF.createPersistent();

        clearContext();
        Long id = campaign.getId();
        campaign = campaignService.find(id);

        BigDecimal budget = new BigDecimal(1000);
        String name = displayCampaignTF.getTestEntityRandomName();
        campaign.setName(name);
        campaign.setBudget(budget);
        campaignService.update(campaign);
        commitChanges();

        clearContext();
        campaignService.delete(id);
        commitChanges();

        clearContext();
        BigDecimal fetchedBudget = jdbcTemplate.queryForObject("select BUDGET_MANUAL from CAMPAIGN where campaign_id = ?", BigDecimal.class, id);
        assertEquals("Budget is not updated", budget.compareTo(fetchedBudget), 0);
        assertEquals("Name is not updated", name,
                jdbcTemplate.queryForObject("select NAME from CAMPAIGN where campaign_id = ?", String.class, id));
        assertEquals("Campaign is not deleted", "D",
                jdbcTemplate.queryForObject("select STATUS from CAMPAIGN where campaign_id = ?", String.class, id));

        clearContext();
        campaignService.undelete(id);
        commitChanges();

        clearContext();
        assertEquals("Campaign is not undeleted", "I",
                jdbcTemplate.queryForObject("select STATUS from CAMPAIGN where campaign_id = ?", String.class, id));

        campaignService.activate(id);
        commitChanges();

        clearContext();
        assertEquals("Campaign is not deactivated", "A",
               jdbcTemplate.queryForObject("select STATUS from CAMPAIGN where campaign_id = ?", String.class, id));
    }

    @Test
    public void testGetCCGStatsInternalUser() {
        Campaign campaign = displayCampaignTF.createPersistent();
        getGetCCGStatsScenario(campaign.getId());
    }

    @Test
    public void testGetCCGStatsExternalUser() throws Exception {
        Campaign campaign = displayCampaignTF.createPersistent();
        getGetCCGStatsScenario(campaign.getId());
    }

    @Test
    public void testGetCCGStatsZeroImpressions() throws Exception {
        Campaign campaign = displayCampaignTF.createPersistent();
        getGetCCGStatsScenario(campaign.getId(), 0, 0, 0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Test
    public void testLiveDisplayStatus() throws Exception {
        AccountType accountType = advertiserAccountTypeTF.create();
        accountType.setIoManagement(false);
        advertiserAccountTypeTF.persist(accountType);
        Campaign campaign = textCampaignTF.createPersistent(accountType);
        textCCGTF.createGreenCCG(campaign);
        textCCGTF.createGreyCCG(campaign);

        verifyCampaignDisplayStatus(campaign.getId(), Campaign.LIVE);
    }

    @Test
    public void testLiveNeedAttDisplayStatus() throws Exception {
        AccountType accountType = advertiserAccountTypeTF.create();
        accountType.setIoManagement(false);
        advertiserAccountTypeTF.persist(accountType);
        Campaign campaign = textCampaignTF.createPersistent(accountType);
        textCCGTF.createGreenCCG(campaign);
        textCCGTF.createRedCCG(campaign);

        verifyCampaignDisplayStatus(campaign.getId(), Campaign.LIVE_NEED_ATT);
    }

    @Test
    public void testNotLiveNeedAttDisplayStatus() throws Exception {
        AccountType accountType = advertiserAccountTypeTF.create();
        accountType.setIoManagement(false);
        advertiserAccountTypeTF.persist(accountType);
        Campaign campaign = textCampaignTF.createPersistent(accountType);
        textCCGTF.createGreyCCG(campaign);
        textCCGTF.createRedCCG(campaign);

        verifyCampaignDisplayStatus(campaign.getId(), Campaign.NOT_LIVE_NEED_ATT);
    }

    @Test
    public void testNoActiveGroupsDisplayStatus() throws Exception {
        Campaign campaign = textCampaignTF.createPersistent();
        textCCGTF.createGreyCCG(campaign);

        verifyCampaignDisplayStatus(campaign.getId(), Campaign.NO_ACTIVE_GROUPS);
    }

    @Test
    public void testDateEnd() {
        Campaign campaign = displayCampaignTF.create();
        Calendar dateEnd = Calendar.getInstance();
        dateEnd.setTime(campaign.getDateStart());
        dateEnd.add(Calendar.MINUTE, 10);
        campaign.setDateEnd(dateEnd.getTime());
        displayCampaignTF.persist(campaign);

        CampaignCreativeGroup ccgNotLinked = createCampaignCreativeGroup(campaign);
        Calendar dateEndCCG = Calendar.getInstance();
        dateEndCCG.setTime(campaign.getDateStart());
        dateEndCCG.add(Calendar.MINUTE, 5);
        ccgNotLinked.setDateEnd(dateEndCCG.getTime());
        displayCCGTF.update(ccgNotLinked);

        CampaignCreativeGroup ccgLinked = createCampaignCreativeGroup(campaign);
        ccgLinked.setLinkedToCampaignEndDateFlag(true);
        displayCCGTF.update(ccgLinked);

        commitChanges();
        clearContext();
        ccgNotLinked = displayCCGTF.refresh(ccgNotLinked);
        ccgLinked = displayCCGTF.refresh(ccgLinked);

        Calendar dateEndEarlier = Calendar.getInstance();
        dateEndEarlier.setTime(campaign.getDateStart());
        dateEndEarlier.add(Calendar.MINUTE, 2);
        campaign.setDateEnd(dateEndEarlier.getTime());
        displayCampaignTF.update(campaign);
        campaign = displayCampaignTF.refresh(campaign);

        assertEquals(true, ccgNotLinked.getCalculatedEndDate().equals(campaign.getDateEnd()));
        assertEquals(true, ccgLinked.getCalculatedEndDate().equals(campaign.getDateEnd()));

        Calendar dateEndLater = Calendar.getInstance();
        dateEndLater.setTime(campaign.getDateStart());
        dateEndLater.add(Calendar.MINUTE, 6);
        campaign.setDateEnd(dateEndLater.getTime());
        displayCampaignTF.update(campaign);

        assertEquals(false, ccgNotLinked.getCalculatedEndDate().equals(campaign.getDateEnd()));
        assertEquals(true, ccgLinked.getCalculatedEndDate().equals(campaign.getDateEnd()));

        campaign.setDateEnd(null);
        displayCampaignTF.update(campaign);

        assertEquals(false, ccgNotLinked.getCalculatedEndDate() == null);
        assertEquals(true, ccgLinked.getCalculatedEndDate() == null);
    }

    @Test
    public void testCreateOrUpdateAll() {
        AdvertiserAccount account = advertiserAccountTF.createPersistent();
        User user = userTF.createPersistent(account);
        Calendar now = Calendar.getInstance();
        DateUtil.resetFields(now, Calendar.SECOND, Calendar.MILLISECOND);
        List<Campaign> campaigns = new ArrayList<Campaign>();
        int count = 2;
        String campaignNames = "";

        for (int i = 0; i < 2; i++) {
            Campaign campaign = textCampaignTF.create(account.getId(), user.getId(), null);
            campaignNames = campaignNames + "'" + campaign.getName() + "'";
            if (i != count - 1) {
                campaignNames = campaignNames + ",";
            }
            campaign.setStatus(Status.INACTIVE);
            campaign.setProperty(UPLOAD_CONTEXT, new UploadContext(UploadStatus.NEW));
            Set<CampaignCreativeGroup> creativeGroups = new HashSet<CampaignCreativeGroup>();
            for (int j = 0; j < 2; j++) {
                CampaignCreativeGroup ccg = textCCGTF.create(campaign);
                creativeGroups.add(ccg);
                ccg.setProperty(UPLOAD_CONTEXT, new UploadContext(UploadStatus.NEW));
            }
            campaign.setCreativeGroups(creativeGroups);

            campaigns.add(campaign);
        }

        campaignService.createOrUpdateAll(account.getId(), campaigns);
        entityManager.flush();

        entityManager.clear();
        @SuppressWarnings("unchecked")
        List<Campaign> foundCampaigns =
                entityManager.createQuery("SELECT c FROM Campaign c WHERE c.name in (" + campaignNames + ")").getResultList();
        assertEquals(2, foundCampaigns.size());
        assertEquals(2, foundCampaigns.get(0).getCreativeGroups().size());
        assertEquals(2, foundCampaigns.get(1).getCreativeGroups().size());
    }

    @Test
    public void testValidateAll() {
        AccountType accountType = advertiserAccountTypeTF.create();
        accountType.setIoManagement(false);
        advertiserAccountTypeTF.persist(accountType);

        AdvertiserAccount account = advertiserAccountTF.createPersistent(accountType);
        List<Campaign> campaigns = new ArrayList<Campaign>();
        commitChanges();

        // update Campaign
        Campaign campaignUpdated = textCampaignTF.create(account);
        campaignUpdated.setName("campaignUpdated");
        textCampaignTF.persist(campaignUpdated);
        campaigns.add(campaignUpdated);

        // new Campaign
        Campaign campaignNew = textCampaignTF.create(account);
        campaignNew.setName("campaignNew");
        campaigns.add(campaignNew);

        // new Campaign without CCGs
        Campaign campaignNewWithoutCCGs = textCampaignTF.create(account);
        campaignNewWithoutCCGs.setName("campaignNewWithoutCCGs");
        campaigns.add(campaignNewWithoutCCGs);

        // Campaign doesn't exist
        Campaign campaignNotExistLink = textCampaignTF.create(account);
        campaignNotExistLink.setName("campaignNotExistLink");
        campaignNotExistLink.setProperty(UPLOAD_CONTEXT, new UploadContext(UploadStatus.LINK));
        campaigns.add(campaignNotExistLink);

        // update Campaign Creative Group
        CampaignCreativeGroup groupUpdated = textCCGTF.createPersistent(campaignUpdated);
        campaignUpdated.getCreativeGroups().add(groupUpdated);

        // new Campaign Creative Group
        CampaignCreativeGroup groupNew = textCCGTF.create(campaignNew);
        campaignNew.getCreativeGroups().add(groupNew);

        // Campaign Creative Group doesn't exist
        CampaignCreativeGroup groupNotExist = textCCGTF.create(campaignUpdated);
        groupNotExist.setProperty(UPLOAD_CONTEXT, new UploadContext(UploadStatus.LINK));
        campaignUpdated.getCreativeGroups().add(groupNotExist);

        clearContext();
        campaignService.validateAll(account.getId(), TGTType.KEYWORD, campaigns);

        assertEquals(UploadStatus.UPDATE, campaignUpdated.getProperty(UPLOAD_CONTEXT).getStatus());
        assertEquals(UploadStatus.NEW, campaignNew.getProperty(UPLOAD_CONTEXT).getStatus());
        assertEquals(UploadStatus.REJECTED, campaignNewWithoutCCGs.getProperty(UPLOAD_CONTEXT).getStatus());
        assertEquals(UploadStatus.LINK, campaignNotExistLink.getProperty(UPLOAD_CONTEXT).getStatus());

        assertEquals(UploadStatus.UPDATE, groupUpdated.getProperty(UPLOAD_CONTEXT).getStatus());
        assertEquals(UploadStatus.NEW, groupNew.getProperty(UPLOAD_CONTEXT).getStatus());
        assertEquals(UploadStatus.LINK, groupNotExist.getProperty(UPLOAD_CONTEXT).getStatus());
    }

    @Test
    public void testCreateCampaignForDeliverySchedule() {
        Campaign campaign = displayCampaignTF.create();

        // campaign running 24/7
        Long campaignId = campaignService.create(campaign);
        campaign = campaignService.find(campaignId);
        assertEquals("Schedule list should be empty", true, campaign.getCampaignSchedules().isEmpty());

        // campaign with schedules
        campaign = displayCampaignTF.create();
        displayCampaignTF.addSchedule(campaign, 0L, 59L);
        displayCampaignTF.addSchedule(campaign, 180L, 209L);

        campaignId = campaignService.create(campaign);
        campaign = campaignService.find(campaignId);
        assertEquals("Schedule list must have two schedules", 2, campaign.getCampaignSchedules().size());
    }

    @Test
    public void testUpdateCampaignForDeliverSchedule() {
        // campaign running 24/7
        Campaign campaign = displayCampaignTF.createPersistent();
        clearContext();
        assertEquals("Schedule list should be empty", true, campaign.getCampaignSchedules().isEmpty());

        // update to set schedules
        displayCampaignTF.addSchedule(campaign, 0L, 59L);
        displayCampaignTF.addSchedule(campaign, 210L, 239L);
        displayCampaignTF.update(campaign);
        clearContext();

        campaign = displayCampaignTF.find(campaign.getId());
        assertEquals("Schedule list must have two schedules", 2, campaign.getCampaignSchedules().size());

        // update back to 24/7 schedule
        clearContext();
        campaign.setCampaignSchedules(new LinkedHashSet<CampaignSchedule>());
        displayCampaignTF.update(campaign);
        clearContext();
        campaign = displayCampaignTF.find(campaign.getId());
        assertEquals("Schedule list should be empty", true, campaign.getCampaignSchedules().isEmpty());
    }

    @Test
    public void testAffectedCCGDelivery() {
        Campaign campaign = displayCampaignTF.create();
        displayCampaignTF.addSchedule(campaign, 0L, 59L);
        Long campaignId = campaignService.create(campaign);
        commitChanges();

        CampaignCreativeGroup notAffectedCCG = displayCCGTF.create(campaign);
        displayCCGTF.addSchedule(notAffectedCCG, 0L, 59L);
        notAffectedCCG.setDeliveryScheduleFlag(true);
        campaignCreativeGroupService.create(notAffectedCCG);
        commitChanges();

        CampaignCreativeGroup linkedToCampaignSchedule = displayCCGTF.create(campaign);
        linkedToCampaignSchedule.setDeliveryScheduleFlag(false);
        campaignCreativeGroupService.create(linkedToCampaignSchedule);
        commitChanges();

        CampaignCreativeGroup affectedCCG = displayCCGTF.create(campaign);
        displayCCGTF.addSchedule(affectedCCG, 360L, 419L);
        affectedCCG.setDeliveryScheduleFlag(true);
        campaignCreativeGroupService.create(affectedCCG);
        commitChanges();

        CampaignCreativeGroup affectedCCG1 = displayCCGTF.create(campaign);
        displayCCGTF.addSchedule(affectedCCG1, 0L, 59L);
        displayCCGTF.addSchedule(affectedCCG1, 90L, 119L);
        affectedCCG1.setDeliveryScheduleFlag(true);
        campaignCreativeGroupService.create(affectedCCG1);
        commitChanges();

        clearContext();

        Collection<CCGStatsTO> ccgList = campaignService.getCCGStats(campaignId, null, null);
        assertEquals("Invalid ccgList", 4, ccgList.size());

        Set<String> affectedCCGSchedule = campaignService.getAffectedCCGForCampaignDelivery(campaignId, campaign.getCampaignSchedules());
        assertTrue("CCgSchedule is out of campaign schedule", affectedCCGSchedule.contains(affectedCCG.getName()));
        assertTrue("CCgSchedule is out of campaign schedule", affectedCCGSchedule.contains(affectedCCG1.getName()));
    }

    @Test
    public void testPendingCCGForAccount() throws Exception {
        AccountType accountType = advertiserAccountTypeTF.create();
        accountType.setCPCFlag(CCGType.DISPLAY, true);
        advertiserAccountTypeTF.persist(accountType);
        AdvertiserAccount account = advertiserAccountTF.createPersistent(accountType);

        assertNotNull(campaignService.getPendingCCGTreeRawDataForAccount(account));
    }

    private Operation<Campaign> createOperation(Campaign campaign, OperationType type) {
        Operation<Campaign> operation = new Operation<Campaign>();
        operation.setEntity(campaign);
        operation.setOperationType(type);
        return operation;
    }

    private CampaignCreative createCampaignCreative(CampaignCreativeGroup group) {
        Creative creative = textCreativeTF.createPersistent(group.getAccount(), creativeTemplateTF.findText(), creativeSizeTF.findText());
        return creativeLinkTF.create(group, creative);
    }

    private CampaignCreativeGroup createCampaignCreativeGroup(Campaign campaign) {
        CampaignCreativeGroup group = displayCCGTF.createPersistent(campaign);
        entityManager.flush();
        group.setStatus(Status.INACTIVE);
        displayCCGTF.update(group);
        group = entityManager.find(CampaignCreativeGroup.class, group.getId());
        BigDecimal cpc = new BigDecimal(123);
        CcgRate ccgRate = new CcgRate();
        ccgRate.setCcg(group);
        ccgRate.setCpc(cpc);
        ccgRate.setRateType(RateType.CPC);
        ccgRate.setEffectiveDate(new LocalDate().plusYears(100).toDate());
        persist(ccgRate);
        group.setCcgRate(ccgRate);
        displayCCGTF.update(group);

        return group;
    }

    private void getGetCCGStatsScenario(Long campaignId, long impressions, long clicks, long uniqueUsers,
                                        BigDecimal cost, BigDecimal targetingCost, BigDecimal creditUsed) {
        Campaign campaign = campaignService.find(campaignId);

        List<CCGStatsTO> stats = campaignService.getCCGStats(campaignId, null, null);
        stats.addAll(campaignService.getCCGStats(campaignId, null, null));
        assertEquals("Stats found not same number of CCGs than is linked to the Campaign", campaign.getCreativeGroups().size(), stats.size());

        stats = campaignService.getCCGStats(campaignId, null, null);
        stats.addAll(campaignService.getCCGStats(campaignId, null, null));
        assertEquals("Stats found not same number of CCGs than is linked to the Campaign", campaign.getCreativeGroups().size(), stats.size());

        for (CCGStatsTO stat : stats) {
            CampaignCreativeGroup group = entityManager.find(CampaignCreativeGroup.class, stat.getId());
            assertEquals(group.getName(), stat.getName());
            assertEquals(group.getDisplayStatus(), stat.getDisplayStatus());
            assertEquals(group.getCcgType(), stat.getCcgType());
            assertEquals(group.getTgtType(), stat.getTgtType());
            assertEquals(impressions, stat.getImps());
            assertEquals(clicks, stat.getClicks());
            assertEquals(0, stat.getPostClickConv());
            assertEquals(0, stat.getPostImpConv());
            assertEquals(uniqueUsers, stat.getUniqueUsers());
            assertEquals(cost, stat.getInventoryCost());
            assertEquals(targetingCost, stat.getTargetingCost());
            assertEquals(creditUsed, stat.getCreditUsed());
            assertEquals(group.getChannelTarget(), stat.getTarget());
            assertNull(group.getTargetingChannelId());
            assertNull(stat.getChannelTarget());
        }
    }

    private void getGetCCGStatsScenario(Long campaignId) {
        getGetCCGStatsScenario(campaignId, 0, 0, 0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    private void verifyCampaignDisplayStatus(Long campaignId, DisplayStatus expected) {
        commitChanges();

        clearContext();
        Campaign campaign = campaignService.find(campaignId);

        assertEquals(expected, campaign.getDisplayStatus());

        verifyBulkUpdateDisplayStatus(campaign);
    }

    @Test
    public void testExcludedChannels() {
        AdvertiserAccount advertiser = advertiserAccountTF.createPersistent();
        BehavioralChannel channel = behavioralChannelTF.createPersistent(advertiser);
        commitChanges();
        clearContext();

        Campaign campaign = displayCampaignTF.create(advertiser);
        campaign.setExcludedChannels(Collections.singleton(new BehavioralChannel(channel.getId())));
        campaignService.create(campaign);
        commitChanges();
        clearContext();

        campaign = displayCampaignTF.refresh(campaign);
        assertEquals(1, campaign.getExcludedChannels().size());
    }
}
