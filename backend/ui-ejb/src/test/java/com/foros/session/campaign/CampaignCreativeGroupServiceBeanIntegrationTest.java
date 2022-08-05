package com.foros.session.campaign;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.ApproveStatus;
import com.foros.model.Country;
import com.foros.model.FrequencyCap;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.ExternalAccount;
import com.foros.model.action.Action;
import com.foros.model.campaign.BidStrategy;
import com.foros.model.campaign.CCGSchedule;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CampaignType;
import com.foros.model.campaign.CcgRate;
import com.foros.model.campaign.ChannelTarget;
import com.foros.model.campaign.OptInStatusTargeting;
import com.foros.model.campaign.RateType;
import com.foros.model.campaign.TGTType;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.ChannelExpressionLink;
import com.foros.model.isp.Colocation;
import com.foros.model.security.AccountType;
import com.foros.model.security.User;
import com.foros.model.security.UserRole;
import com.foros.model.site.Site;
import com.foros.model.time.TimeSpan;
import com.foros.model.time.TimeUnit;
import com.foros.security.MockPrincipal;
import com.foros.security.principal.SecurityPrincipal;
import com.foros.session.EntityTO;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.UploadStatus;
import com.foros.session.account.AccountService;
import com.foros.session.action.ActionService;
import com.foros.session.channel.service.DeviceChannelService;
import com.foros.test.factory.ActionTestFactory;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.AgencyAccountTestFactory;
import com.foros.test.factory.AgencyAccountTypeTestFactory;
import com.foros.test.factory.AudienceChannelTestFactory;
import com.foros.test.factory.BehavioralChannelTestFactory;
import com.foros.test.factory.ColocationTestFactory;
import com.foros.test.factory.DisplayCCGTestFactory;
import com.foros.test.factory.DisplayCampaignTestFactory;
import com.foros.test.factory.SiteTestFactory;
import com.foros.test.factory.TextCCGTestFactory;
import com.foros.test.factory.TextCampaignTestFactory;
import com.foros.test.factory.TextCreativeLinkTestFactory;
import com.foros.test.factory.UserTestFactory;
import com.foros.util.CollectionUtils;
import com.foros.util.EntityUtils;
import com.foros.util.FlagsUtil;
import com.foros.util.UploadUtils;
import com.foros.util.expression.ExpressionHelper;
import com.foros.util.mapper.Converter;

import group.Db;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class CampaignCreativeGroupServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private CampaignCreativeGroupService campaignCreativeGroupService;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private DisplayCCGTestFactory displayCCGTF;

    @Autowired
    private TextCCGTestFactory textCCGTF;

    @Autowired
    public ActionTestFactory actionTestTF;

    @Autowired
    private TextCreativeLinkTestFactory creativeLinkTF;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    private BehavioralChannelTestFactory behavioralChannelTF;

    @Autowired
    private AudienceChannelTestFactory audienceChannelTF;

    @Autowired
    private TextCampaignTestFactory textCampaignTF;

    @Autowired
    private DisplayCampaignTestFactory displayCampaignTF;

    @Autowired
    public SiteTestFactory siteTF;

    @Autowired
    public ColocationTestFactory colocationTF;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private UserTestFactory userTestFactory;

    @Autowired
    private AgencyAccountTestFactory agencyAccountTestFactory;

    @Autowired
    private AgencyAccountTypeTestFactory agencyAccountTypeTestFactory;

    @Autowired
    private DeviceChannelService deviceChannelService;

    @Test
    public void testCreate() throws Exception {
        Campaign campaign = textCampaignTF.createPersistent();
        AdvertiserAccount account = campaign.getAccount();
        account.setFlags(account.getFlags() | Account.INTERNATIONAL);
        commitChanges();

        campaign = displayCampaignTF.find(campaign.getId());
        CampaignCreativeGroup ccg = textCCGTF.create();
        ccg.setCampaign(displayCampaignTF.find(campaign.getId()));
        ccg.setDateStart(campaign.getDateStart());
        campaignCreativeGroupService.create(ccg);
        commitChanges();
        assertTargetingChannelExists(ccg);

        CampaignCreativeGroup found = campaignCreativeGroupService.find(ccg.getId());
        assertEquals(found.getName(), ccg.getName());
        checkDeviceChannels(found, null);
    }

    @Test
    public void testDefaultDeviceChannels() throws Exception {
        CampaignCreativeGroup ccg = textCCGTF.createPersistent();
        AdvertiserAccount advertiserAccount = ccg.getAccount();
        AccountType accountType = advertiserAccount.getAccountType();
        accountType.getDeviceChannels().clear();
        accountType.getDeviceChannels().add(deviceChannelService.getApplicationsChannel());
        accountType.getDeviceChannels().add(deviceChannelService.getBrowsersChannel());
        accountType.getDeviceChannels().add(deviceChannelService.getMobileDevicesChannel());
        accountType.getDeviceChannels().add(deviceChannelService.getNonMobileDevicesChannel());

        // All
        advertiserAccountTF.update(advertiserAccount);
        commitChangesAndClearContext();

        CampaignCreativeGroup created1 = textCCGTF.createPersistent(ccg.getCampaign());
        commitChangesAndClearContext();

        CampaignCreativeGroup found = campaignCreativeGroupService.find(created1.getId());
        checkDeviceChannels(found, null);

        // Not All
        advertiserAccount = found.getAccount();
        accountType = advertiserAccount.getAccountType();
        accountType.getDeviceChannels().remove(deviceChannelService.getNonMobileDevicesChannel());

        advertiserAccountTF.update(advertiserAccount);
        commitChangesAndClearContext();

        CampaignCreativeGroup created2 = textCCGTF.createPersistent(ccg.getCampaign());
        commitChangesAndClearContext();

        found = campaignCreativeGroupService.find(created2.getId());
        checkDeviceChannels(found, null);
    }

    @Test
    public void testUpdateFrequencyCaps() throws Exception {
        CampaignCreativeGroup ccg = displayCCGTF.create();
        ccg.setStatus(Status.ACTIVE);
        persistGroup(ccg, advertiserAccountTF.createPersistent());

        // check that fcaps is null
        clearContext();
        ccg = displayCCGTF.refresh(ccg);
        assertNull(ccg.getFrequencyCap());

        // set fcaps
        clearContext();
        FrequencyCap fcap = new FrequencyCap();
        fcap.setLifeCount(10);
        fcap.setPeriod(12);
        ccg.setFrequencyCap(fcap);
        campaignCreativeGroupService.update(ccg);
        commitChanges();

        // check that fcaps were correctly set
        ccg = displayCCGTF.refresh(ccg);
        fcap = ccg.getFrequencyCap();
        assertEquals(fcap.getLifeCount(), new Integer(10));
        assertEquals(fcap.getPeriod(), new Integer(12));

        // modify fcaps
        clearContext();
        ccg.getFrequencyCap().setPeriod(15);
        ccg.registerChange("frequencyCap");
        campaignCreativeGroupService.update(ccg);
        commitChanges();

        // check that fcaps were correctly modified
        fcap = entityManager.find(FrequencyCap.class, fcap.getId());
        assertEquals(fcap.getPeriod(), new Integer(15));

        // clear fcaps
        ccg = displayCCGTF.refresh(ccg);
        entityManager.clear();
        ccg.setFrequencyCap(null);
        campaignCreativeGroupService.update(ccg);
        commitChanges();

        // check that fcaps were deleted persistently
        ccg = displayCCGTF.refresh(ccg);
        assertNull(ccg.getFrequencyCap());
        assertNull(entityManager.find(FrequencyCap.class, fcap.getId()));
    }

    @Test
    public void testUpdateCcgRateAndFrequencyCapDisplayKeywordInternal() throws Exception {
        CampaignCreativeGroup ccg = generateCcgPersistent(CCGType.DISPLAY, TGTType.CHANNEL);

        ccg = testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPA, false);
        ccg = testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPC, false);
        testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPM, false);
    }

    @Test
    public void testSearchGroups() {
        setDeletedObjectsVisible(true);
        Campaign campaign = displayCampaignTF.create();
        campaignService.create(campaign);

        CampaignCreativeGroup ccg = displayCCGTF.create(campaign);
        campaignCreativeGroupService.create(ccg);
        entityManager.flush();

        getEntityManager().clear();
        campaignCreativeGroupService.delete(ccg.getId());
        commitChanges();

        List<TreeFilterElementTO> ccgs = campaignCreativeGroupService.searchGroups(campaign.getId());
        assertTrue(ccgs.size() == 1);

        setDeletedObjectsVisible(false);
        ccgs = campaignCreativeGroupService.searchGroups(campaign.getId());
        assertTrue(ccgs.size() == 0);
    }

    @Test
    public void testUpdateCcgRateAndFrequencyCapDisplayChannelInternal() throws Exception {
        CampaignCreativeGroup ccg = generateCcgPersistent(CCGType.DISPLAY, TGTType.CHANNEL);

        ccg = testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPA, false);
        ccg = testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPC, false);
        testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPM, false);
    }

    @Test
    public void testUpdateCcgRateAndFrequencyCapTextKeywordInternal() throws Exception {
        CampaignCreativeGroup ccg = generateCcgPersistent(CCGType.TEXT, TGTType.KEYWORD);

        ccg = testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPA, false);
        ccg = testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPC, false);
        testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPM, false);
    }

    @Test
    public void testUpdateCcgRateAndFrequencyCapTextChannelInternal() throws Exception {
        CampaignCreativeGroup ccg = generateCcgPersistent(CCGType.TEXT, TGTType.CHANNEL);

        ccg = testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPA, false);
        ccg = testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPC, false);
        testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPM, false);
    }

    @Test
    public void testUpdateCcgRateAndFrequencyCapDisplayKeywordExternal() throws Exception {
        currentUserRule.setPrincipal(getRealAdvertisingPrincipal());

        CampaignCreativeGroup ccg = generateCcgPersistent(CCGType.DISPLAY, TGTType.CHANNEL);

        ccg = testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPA, false);
        ccg = testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPC, false);
        testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPM, false);
    }

    @Test
    public void testUpdateCcgRateAndFrequencyCapDisplayChannelExternal() throws Exception {
        currentUserRule.setPrincipal(getRealAdvertisingPrincipal());

        CampaignCreativeGroup ccg = generateCcgPersistent(CCGType.DISPLAY, TGTType.CHANNEL);

        ccg = testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPA, false);
        ccg = testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPC, false);
        testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPM, false);
    }

    @Test
    public void testUpdateCcgRateAndFrequencyCapTextKeywordExternal() throws Exception {
        currentUserRule.setPrincipal(getRealAdvertisingPrincipal());

        CampaignCreativeGroup ccg = generateCcgPersistent(CCGType.TEXT, TGTType.KEYWORD);

        ccg = testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPA, false);
        ccg = testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPC, false);
        testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPM, false);
    }

    @Test
    public void testUpdateCcgRateAndFrequencyCapTextChannelExternal() throws Exception {
        currentUserRule.setPrincipal(getRealAdvertisingPrincipal());

        CampaignCreativeGroup ccg = generateCcgPersistent(CCGType.TEXT, TGTType.CHANNEL);

        ccg = testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPA, false);
        ccg = testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPC, false);
        testUpdateCcgRateAndFrequencyCap(ccg, RateType.CPM, false);
    }

    private CampaignCreativeGroup testUpdateCcgRateAndFrequencyCap(CampaignCreativeGroup ccg,
            RateType rateType, boolean shouldBeNull) throws Exception {

        //Saving "old" CcgRate and frequency cap
        CcgRate ccgRate = generateCcgRate(rateType);
        FrequencyCap fCap = generateFrequencyCap();
        clearContext();
        ccg.setFrequencyCap(fCap);
        ccg.setCcgRate(ccgRate);
        campaignCreativeGroupService.update(ccg);
        commitChanges();
        ccg = campaignCreativeGroupService.find(ccg.getId());

        //Checking changes
        assertTrue(ccgRatesEqual(ccgRate, ccg.getCcgRate()));
        if (shouldBeNull) {
            assertNull(ccg.getFrequencyCap());
        } else {
            assertTrue(frequencyCapsEqual(ccg.getFrequencyCap(), fCap));
        }

        //Deleting fcaps
        ccg.setFrequencyCap(null);
        campaignCreativeGroupService.update(ccg);
        commitChanges();
        ccg = campaignCreativeGroupService.find(ccg.getId());
        assertNull(ccg.getFrequencyCap());

        return ccg;
    }

    @Test
    public void testUpdateFlags() throws Exception {
        CampaignCreativeGroup ccg = textCCGTF.createPersistent();
        commitChanges();

        clearContext();
        ccg = campaignCreativeGroupService.find(ccg.getId());
        assertTrue(ccg.isLinkedToCampaignEndDateFlag());

        ccg.setLinkedToCampaignEndDateFlag(false);
        campaignCreativeGroupService.update(ccg);
        commitChanges();

        clearContext();
        ccg = campaignCreativeGroupService.find(ccg.getId());
        assertFalse(ccg.isLinkedToCampaignEndDateFlag());
    }

    @Test
    public void testCCGCreateCopy() throws Exception {
        CampaignCreativeGroup campaignCreativeGroup = displayCCGTF.create();
        AdvertiserAccount account = advertiserAccountTF.createPersistent();
        persistGroup(campaignCreativeGroup, account);

        long newCcgId = campaignCreativeGroupService.createCopy(campaignCreativeGroup.getId());
        CampaignCreativeGroup foundCopy = campaignCreativeGroupService.find(newCcgId);

        assertNotSame(campaignCreativeGroup.getId(), newCcgId);
        assertEquals(foundCopy.getName(), "Copy of " + campaignCreativeGroup.getName());
        checkDeviceChannels(campaignCreativeGroup, foundCopy);
    }

    @Test
    public void testActivate() throws Exception {
        CampaignCreativeGroup campaignCreativeGroup =  testActivate(false, Status.INACTIVE);
        assertEquals(Status.ACTIVE, campaignCreativeGroup.getStatus());
    }

    @Test
    public void testActivateInactive() throws Exception {
        CampaignCreativeGroup campaignCreativeGroup = testActivate(false, Status.INACTIVE);
        assertEquals(Status.ACTIVE, campaignCreativeGroup.getStatus());
    }

    @Test
    public void testActivatePending() throws Exception {
        CampaignCreativeGroup campaignCreativeGroup = testActivate(false, Status.PENDING);
        assertEquals(Status.ACTIVE, campaignCreativeGroup.getStatus());
    }

    @Test
    public void testActivateInternalInactive() throws Exception{
        CampaignCreativeGroup campaignCreativeGroup = testActivate(true, Status.INACTIVE);
        // Commented for now. See OUI-25720
//        assertEquals(Status.PENDING, campaignCreativeGroup.getStatus());
        assertEquals(Status.ACTIVE, campaignCreativeGroup.getStatus());
    }

    @Test
    public void testActivateInternalPending() throws Exception {
        CampaignCreativeGroup campaignCreativeGroup = testActivate(true, Status.PENDING);
        assertEquals(Status.ACTIVE, campaignCreativeGroup.getStatus());
    }

    @Test
    public void testApprove() throws Exception {
        CampaignCreativeGroup campaignCreativeGroup = displayCCGTF.create();
        AdvertiserAccount account = advertiserAccountTF.createPersistent();

        persistGroup(campaignCreativeGroup, account);
        // by default, ccg is approved. decline it and then try to approve
        campaignCreativeGroupService.decline(campaignCreativeGroup.getId(), "Testing");
        getEntityManager().flush();

        campaignCreativeGroupService.approve(campaignCreativeGroup.getId());
        assertEquals(ApproveStatus.APPROVED, campaignCreativeGroup.getQaStatus());
    }

    @Test
    public void testDecline() throws Exception {
        CampaignCreativeGroup campaignCreativeGroup = displayCCGTF.create();
        AdvertiserAccount account = advertiserAccountTF.createPersistent();
        persistGroup(campaignCreativeGroup, account);

        campaignCreativeGroupService.decline(campaignCreativeGroup.getId(), "Decline reason");

        assertEquals(ApproveStatus.DECLINED, campaignCreativeGroup.getQaStatus());
    }

    @Test
    public void testChangesInFrequencyCapsApproved() throws Exception {
        testChangesInFrequencyCaps(ApproveStatus.APPROVED);
    }

    @Test
    public void testChangesInFrequencyCapsDeclined() throws Exception {
        testChangesInFrequencyCaps(ApproveStatus.DECLINED);
    }

    @Test
    public void testChangesInChannelTargetApproved() throws Exception {
        testChangesInChannelTarget(ApproveStatus.APPROVED, ApproveStatus.APPROVED);
    }

    @Test
    public void testChangesInChannelTargetDeclined() throws Exception {
        testChangesInChannelTarget(ApproveStatus.DECLINED, ApproveStatus.DECLINED);
    }

    @Test
    public void testAudienceChannelTarget() {
            CampaignCreativeGroup campaignCreativeGroup = displayCCGTF.create();
            AdvertiserAccount account = advertiserAccountTF.createPersistent();
            persistGroup(campaignCreativeGroup, account);

            entityManager.clear();

            AudienceChannel audienceChannel = audienceChannelTF.createPersistent(campaignCreativeGroup.getAccount());
            campaignCreativeGroup.setChannel(audienceChannel);
            campaignCreativeGroup.setChannelTarget(ChannelTarget.TARGETED);
            campaignCreativeGroupService.updateTarget(campaignCreativeGroup);
            entityManager.flush();

            assertEquals(audienceChannel.getId(), campaignCreativeGroup.getChannel().getId());
            assertTargetingChannelExists(campaignCreativeGroup);
            assertEquals(Status.ACTIVE, campaignCreativeGroup.getStatus());
    }

    @Test
    public void testChangesInGeneralPropertiesApproved() throws Exception {
        testChangesInGeneralProperties(ApproveStatus.APPROVED);
    }

    @Test
    public void testChangesInGeneralPropertiesDeclined() throws Exception {
        testChangesInGeneralProperties(ApproveStatus.DECLINED);
    }

    @Test
    public void testLinkedKeywords() {
        CampaignCreativeGroup ccg = textCCGTF.createPersistent();
        campaignCreativeGroupService.getLinkedKeywords(ccg.getId(), null, null);
    }

    @Test
    public void testCreateCCGForDeliverySchedule() {
        CampaignCreativeGroup campaignCreativeGroup = displayCCGTF.create();

        // ccg linked to Campaign Schedule
        Long ccgId = campaignCreativeGroupService.create(campaignCreativeGroup);
        campaignCreativeGroup = campaignCreativeGroupService.find(ccgId);
        assertEquals("Schedule list should be empty", true, campaignCreativeGroup.getCcgSchedules().isEmpty());

        // ccg with schedules
        campaignCreativeGroup = displayCCGTF.create();
        displayCCGTF.addSchedule(campaignCreativeGroup, 0L, 59L);
        displayCCGTF.addSchedule(campaignCreativeGroup, 190L, 219L);
        campaignCreativeGroup.setDeliveryScheduleFlag(true);

        ccgId = campaignCreativeGroupService.create(campaignCreativeGroup);
        campaignCreativeGroup = campaignCreativeGroupService.find(ccgId);
        assertEquals("Schedule list must have two schedules", 2, campaignCreativeGroup.getCcgSchedules().size());
    }

    @Test
    public void testUpdateCCGForDeliverSchedule() {
        CampaignCreativeGroup campaignCreativeGroup = displayCCGTF.create();
        Long ccgId = campaignCreativeGroupService.create(campaignCreativeGroup);
        commitChanges();

        clearContext();
        campaignCreativeGroup = campaignCreativeGroupService.find(ccgId);
        assertEquals("Schedule list should be empty", true, campaignCreativeGroup.getCcgSchedules().isEmpty());

        CampaignCreativeGroup toUpdate = displayCCGTF.create();
        toUpdate.setId(campaignCreativeGroup.getId());

        // update to set schedules
        displayCCGTF.addSchedule(toUpdate, 0L, 59L);
        displayCCGTF.addSchedule(toUpdate, 180L, 209L);
        toUpdate.setDeliveryScheduleFlag(true);
        campaignCreativeGroupService.update(toUpdate);
        commitChanges();

        clearContext();
        campaignCreativeGroup = campaignCreativeGroupService.find(ccgId);
        assertEquals("Schedule list must have two schedules", 2, campaignCreativeGroup.getCcgSchedules().size());

        // update back to linked to Campaign Schedule
        toUpdate = new CampaignCreativeGroup();
        toUpdate.setId(campaignCreativeGroup.getId());
        toUpdate.setDeliveryScheduleFlag(false);
        toUpdate.setCcgSchedules(new LinkedHashSet<CCGSchedule>());
        campaignCreativeGroupService.update(toUpdate);
        commitChanges();

        clearContext();
        campaignCreativeGroup = campaignCreativeGroupService.find(ccgId);
        assertEquals("Schedule list should be empty", true, campaignCreativeGroup.getCcgSchedules().isEmpty());
    }

    @Test
    public void testCcgScheduleFallingOutOfCampaignSchedule() {
        Campaign campaign = displayCampaignTF.create();

        displayCampaignTF.addSchedule(campaign, 0L, 59L);
        campaignService.create(campaign);

        CampaignCreativeGroup notAffectedCCG = displayCCGTF.create(campaign);
        notAffectedCCG.setDeliveryScheduleFlag(true);
        displayCCGTF.addSchedule(notAffectedCCG, 0L, 59L);

        campaignCreativeGroupService.create(notAffectedCCG);
        entityManager.flush();
        assertEquals(false, campaignCreativeGroupService.isFallingOutOfCampaignSchedule(notAffectedCCG.getId()));

        CampaignCreativeGroup affectedCCG = displayCCGTF.create(campaign);
        affectedCCG.setDeliveryScheduleFlag(true);
        displayCCGTF.addSchedule(affectedCCG, 360L, 419L);
        campaignCreativeGroupService.create(affectedCCG);
        entityManager.flush();
        assertEquals(true, campaignCreativeGroupService.isFallingOutOfCampaignSchedule(affectedCCG.getId()));

        CampaignCreativeGroup affectedCCG1 = displayCCGTF.create(campaign);
        affectedCCG1.setDeliveryScheduleFlag(true);
        displayCCGTF.addSchedule(affectedCCG1, 0L, 59L);
        displayCCGTF.addSchedule(affectedCCG1, 90L, 119L);
        campaignCreativeGroupService.create(affectedCCG1);
        commitChanges();

        clearContext();
        assertEquals(true, campaignCreativeGroupService.isFallingOutOfCampaignSchedule(affectedCCG1.getId()));
    }

    @Test
    public void testChangesInFreqCapsCpmDisplay() throws Exception {
        CampaignCreativeGroup campaignCreativeGroup = displayCCGTF.create();
        setCcgRate(campaignCreativeGroup, RateType.CPM);
        campaignCreativeGroup = prepareTestChangesInSitesOrCaps(campaignCreativeGroup);

        CampaignCreativeGroup toUpdate = getGroupWithCapsToUpdate(campaignCreativeGroup);
        toUpdate = campaignCreativeGroupService.update(toUpdate);

        // Commented for now. See OUI-25720
//        assertEquals(Status.PENDING, toUpdate.getStatus());
        assertEquals(ApproveStatus.APPROVED, toUpdate.getQaStatus());
    }

    @Test
    public void testChangesInFreqCapsCpaDisplay() throws Exception {
        CampaignCreativeGroup campaignCreativeGroup = displayCCGTF.create();
        setCcgRate(campaignCreativeGroup, RateType.CPA);
        campaignCreativeGroup = prepareTestChangesInSitesOrCaps(campaignCreativeGroup);

        CampaignCreativeGroup toUpdate = getGroupWithCapsToUpdate(campaignCreativeGroup);
        toUpdate = campaignCreativeGroupService.update(toUpdate);

        // Commented for now. See OUI-25720
//        assertEquals(Status.PENDING, toUpdate.getStatus());
        assertEquals(ApproveStatus.APPROVED, toUpdate.getQaStatus());
    }

    @Test
    public void testChangesInFreqCapsCpcDisplay() throws Exception {
        CampaignCreativeGroup campaignCreativeGroup = displayCCGTF.create();
        setCcgRate(campaignCreativeGroup, RateType.CPC);
        campaignCreativeGroup = prepareTestChangesInSitesOrCaps(campaignCreativeGroup);

        CampaignCreativeGroup toUpdate = getGroupWithCapsToUpdate(campaignCreativeGroup);
        toUpdate = campaignCreativeGroupService.update(toUpdate);

        // Commented for now. See OUI-25720
//        assertEquals(Status.PENDING, toUpdate.getStatus());
        assertEquals(ApproveStatus.APPROVED, toUpdate.getQaStatus());
    }

    @Test
    public void testChangesInFreqCapsCpmText() throws Exception {
        CampaignCreativeGroup campaignCreativeGroup = textCCGTF.create();
        campaignCreativeGroup.setLinkedToCampaignEndDateFlag(false);
        setCcgRate(campaignCreativeGroup, RateType.CPM);
        campaignCreativeGroup = prepareTestChangesInSitesOrCaps(campaignCreativeGroup);

        CampaignCreativeGroup toUpdate = getGroupWithCapsToUpdate(campaignCreativeGroup);
        toUpdate = campaignCreativeGroupService.update(toUpdate);

        // Commented for now. See OUI-25720
//        assertEquals(Status.PENDING, toUpdate.getStatus());
        assertEquals(ApproveStatus.APPROVED, toUpdate.getQaStatus());
    }

    @Test
    public void testChangesInFreqCapsCpaText() throws Exception {
        CampaignCreativeGroup campaignCreativeGroup = textCCGTF.create();
        campaignCreativeGroup.setLinkedToCampaignEndDateFlag(false);
        setCcgRate(campaignCreativeGroup, RateType.CPA);
        campaignCreativeGroup = prepareTestChangesInSitesOrCaps(campaignCreativeGroup);

        CampaignCreativeGroup toUpdate = getGroupWithCapsToUpdate(campaignCreativeGroup);
        toUpdate = campaignCreativeGroupService.update(toUpdate);

        assertEquals(Status.ACTIVE, toUpdate.getStatus());
        assertEquals(ApproveStatus.APPROVED, toUpdate.getQaStatus());
    }

    @Test
    public void testChangesInFreqCapsCpcText() throws Exception {
        CampaignCreativeGroup campaignCreativeGroup = textCCGTF.create();
        campaignCreativeGroup.setLinkedToCampaignEndDateFlag(false);
        setCcgRate(campaignCreativeGroup, RateType.CPC);
        campaignCreativeGroup = prepareTestChangesInSitesOrCaps(campaignCreativeGroup);

        CampaignCreativeGroup toUpdate = getGroupWithCapsToUpdate(campaignCreativeGroup);
        toUpdate = campaignCreativeGroupService.update(toUpdate);

        assertEquals(Status.ACTIVE, toUpdate.getStatus());
        assertEquals(ApproveStatus.APPROVED, toUpdate.getQaStatus());
    }

    @Test
    public void testChangesInSitesDisplay() throws Exception {
        CampaignCreativeGroup campaignCreativeGroup = displayCCGTF.create();
        campaignCreativeGroup = prepareTestChangesInSitesOrCaps(campaignCreativeGroup);

        CampaignCreativeGroup toUpdate = getGroupWithSitesToUpdate(campaignCreativeGroup);
        toUpdate = campaignCreativeGroupService.update(toUpdate);

        assertEquals(Status.ACTIVE, toUpdate.getStatus());
        assertEquals(ApproveStatus.APPROVED, toUpdate.getQaStatus());
    }

    @Test
    public void testChangesInSitesText() throws Exception {
        CampaignCreativeGroup campaignCreativeGroup = textCCGTF.create();
        campaignCreativeGroup.setLinkedToCampaignEndDateFlag(false);
        campaignCreativeGroup = prepareTestChangesInSitesOrCaps(campaignCreativeGroup);

        CampaignCreativeGroup toUpdate = getGroupWithSitesToUpdate(campaignCreativeGroup);
        toUpdate = campaignCreativeGroupService.update(toUpdate);

        assertEquals(Status.ACTIVE, toUpdate.getStatus());
        assertEquals(ApproveStatus.APPROVED, toUpdate.getQaStatus());
    }

    @Test
    public void testChangesInColocationsDisplay() throws Exception {
        testChangesInColocations(displayCCGTF.create());
    }

    @Test
    public void testChangesInColocationsText() throws Exception {
        testChangesInColocations(textCCGTF.create());
    }

    private void testChangesInColocations(CampaignCreativeGroup campaignCreativeGroup) {
        campaignCreativeGroup.setLinkedToCampaignEndDateFlag(false);
        campaignCreativeGroup = prepareTestChangesInSitesOrCaps(campaignCreativeGroup);

        CampaignCreativeGroup toUpdate = getGroupWithColocationsToUpdate(campaignCreativeGroup);
        toUpdate = campaignCreativeGroupService.update(toUpdate);

        assertEquals(Status.ACTIVE, toUpdate.getStatus());
        assertEquals(ApproveStatus.APPROVED, toUpdate.getQaStatus());
        assertTrue(!toUpdate.getColocations().isEmpty());
    }

    @Test
    public void testLinkConversions() {
        CampaignCreativeGroup campaignCreativeGroup = displayCCGTF.create();
        Long ccgId = campaignCreativeGroupService.create(campaignCreativeGroup);
        commitChanges();
        clearContext();
        campaignCreativeGroup = campaignCreativeGroupService.find(ccgId);

        // link conversion
        Action action = actionTestTF.createPersistent();
        int conversionSize = campaignCreativeGroup.getActions().size();
        campaignCreativeGroupService.linkConversions(Arrays.asList(ccgId), Arrays.asList(action.getId()));
        campaignCreativeGroup = campaignCreativeGroupService.find(campaignCreativeGroup.getId());
        int newConversionSize = campaignCreativeGroup.getActions().size();
        assertTrue("Conversion was not linked", (newConversionSize - conversionSize) == 1);

        // link deleted conversion
        Action action2 = actionTestTF.createPersistent();
        actionService.delete(action2.getId());
        action2 = actionService.findById(action.getId());
        conversionSize = campaignCreativeGroup.getActions().size();
        campaignCreativeGroupService.linkConversions(Arrays.asList(ccgId), Arrays.asList(action2.getId()));
        campaignCreativeGroup = campaignCreativeGroupService.find(campaignCreativeGroup.getId());
        newConversionSize = campaignCreativeGroup.getActions().size();
        assertTrue("Link deleted conversion", newConversionSize == conversionSize);

        // link conversion to deleted group
        campaignCreativeGroupService.delete(ccgId);
        commitChanges();
        clearContext();
        campaignCreativeGroup = campaignCreativeGroupService.find(ccgId);
        Action action3 = actionTestTF.createPersistent();
        conversionSize = campaignCreativeGroup.getActions().size();
        campaignCreativeGroupService.linkConversions(Arrays.asList(ccgId), Arrays.asList(action3.getId()));
        campaignCreativeGroup = campaignCreativeGroupService.find(campaignCreativeGroup.getId());
        newConversionSize = campaignCreativeGroup.getActions().size();
        assertTrue("Link deleted group", newConversionSize == conversionSize);
    }

    @Test
    public void testGetLinkedCreatives() {
        CampaignCreative campaignCreative = creativeLinkTF.createPersistent();
        CampaignCreativeGroup creativeGroup = campaignCreative.getCreativeGroup();

        assertFalse(creativeGroup.isSequentialAdservingFlag());
        campaignCreativeGroupService.getLinkedCreatives(creativeGroup.getId(), LocalDate.now(), LocalDate.now(), 0, 1000);

        creativeGroup.setSequentialAdservingFlag(true);
        getEntityManager().merge(creativeGroup);
        commitChanges();

        creativeGroup = campaignCreativeGroupService.find(creativeGroup.getId());
        assertTrue(creativeGroup.isSequentialAdservingFlag());
        campaignCreativeGroupService.getLinkedCreatives(creativeGroup.getId(), LocalDate.now(), LocalDate.now(), 0, 1000);
    }

    @Test
    public void testBidStrategy() {
        Long ccgId = displayCCGTF.createPersistent().getId();
        commitChangesAndClearContext();

        CampaignCreativeGroup ccg = new CampaignCreativeGroup();
        ccg.setId(ccgId);
        CcgRate cpaRate = generateCcgRate(RateType.CPA);
        ccg.setCcgRate(cpaRate);
        ccg.setBidStrategy(BidStrategy.MINIMUM_CTR_GOAL);
        ccg.setMinCtrGoal(BigDecimal.TEN);
        campaignCreativeGroupService.update(ccg);
        commitChangesAndClearContext();

        ccg = campaignCreativeGroupService.find(ccg.getId());
        assertEquals(BidStrategy.MINIMUM_CTR_GOAL, ccg.getBidStrategy());
        assertEquals(10, ccg.getMinCtrGoal().doubleValue(), 0.0001);
    }

    private void persistGroup(CampaignCreativeGroup campaignCreativeGroup, AdvertiserAccount account) {
        boolean isText = campaignCreativeGroup.getCcgType().equals(CCGType.TEXT);
        Campaign campaign = isText ? textCampaignTF.createPersistent(account) : displayCampaignTF.createPersistent(account);
        campaign.setCampaignType(isText ? CampaignType.TEXT : CampaignType.DISPLAY);

        if (campaignCreativeGroup.getStatus() == null) {
            campaignCreativeGroup.setStatus(Status.ACTIVE);
        }
        campaignCreativeGroup.setCampaign(campaign);
        campaignCreativeGroup.setDateStart(campaign.getDateStart());
        campaignCreativeGroup.setChannelTarget(ChannelTarget.UNTARGETED);

        if (campaignCreativeGroup.getCcgRate() == null) {
            setCcgRate(campaignCreativeGroup, RateType.CPC);
        }

        campaignCreativeGroupService.create(campaignCreativeGroup);
        getEntityManager().flush();
    }

    private CampaignCreativeGroup testActivate(boolean isInternal, Status previousStatus) throws Exception {
        CampaignCreativeGroup campaignCreativeGroup = displayCCGTF.create();
        campaignCreativeGroup.setStatus(previousStatus);
        AdvertiserAccount account = advertiserAccountTF.createPersistent();
        persistGroup(campaignCreativeGroup, account);

        if (isInternal) {
            currentUserRule.setPrincipal(DEFAULT_ADMIN_PRINCIPAL);
        } else {
            currentUserRule.setPrincipal(ADVERTISER_PRINCIPAL);
        }

        campaignCreativeGroupService.activate(campaignCreativeGroup.getId());
        return campaignCreativeGroup;
    }

    private void testChangesInFrequencyCaps(ApproveStatus qaStatus) throws Exception {
        CampaignCreativeGroup campaignCreativeGroup = displayCCGTF.create();
        AdvertiserAccount account = advertiserAccountTF.createPersistent();
        persistGroup(campaignCreativeGroup, account);

        if (qaStatus == ApproveStatus.DECLINED) {
            campaignCreativeGroupService.decline(campaignCreativeGroup.getId(), "Testing");
        }

        getEntityManager().flush();

        FrequencyCap fc = new FrequencyCap();
        fc.setLifeCount(1);
        fc.setPeriod(1);
        fc.setWindowCount(1);
        fc.setWindowLength(1);
        campaignCreativeGroup.setFrequencyCap(fc);

        campaignCreativeGroupService.update(campaignCreativeGroup);

        getEntityManager().clear();
        campaignCreativeGroup.getFrequencyCap().setLifeCount(2);
        campaignCreativeGroup.registerChange("frequencyCap");

        campaignCreativeGroupService.update(campaignCreativeGroup);

        campaignCreativeGroup = campaignCreativeGroupService.find(campaignCreativeGroup.getId());

        // Commented for now. See OUI-25720
//        assertEquals(Status.PENDING, campaignCreativeGroup.getStatus());
        assertEquals(qaStatus == ApproveStatus.APPROVED ? ApproveStatus.APPROVED : ApproveStatus.HOLD,
                campaignCreativeGroup.getQaStatus());
    }

    private void testChangesInChannelTarget(ApproveStatus qaStatus, ApproveStatus expected) throws Exception {
        CampaignCreativeGroup campaignCreativeGroup = displayCCGTF.create();
        AdvertiserAccount account = advertiserAccountTF.createPersistent();
        persistGroup(campaignCreativeGroup, account);

        if (qaStatus == ApproveStatus.DECLINED) {
            campaignCreativeGroupService.decline(campaignCreativeGroup.getId(), "Testing");
        }

        // Commented for now. See OUI-25720
//        campaignCreativeGroupService.activate(campaignCreativeGroup.getId());

        entityManager.clear();
        BehavioralChannel channel = behavioralChannelTF.createPersistent(campaignCreativeGroup.getAccount());
        campaignCreativeGroup.setChannel(channel);
        campaignCreativeGroup.setChannelTarget(ChannelTarget.TARGETED);
        campaignCreativeGroupService.updateTarget(campaignCreativeGroup);
        entityManager.flush();

        assertTargetingChannelExists(campaignCreativeGroup);

        assertEquals(Status.ACTIVE, campaignCreativeGroup.getStatus());
        assertEquals(expected, campaignCreativeGroup.getQaStatus());
    }

    private void testChangesInGeneralProperties(ApproveStatus qaStatus) throws Exception {
        CampaignCreativeGroup campaignCreativeGroup = displayCCGTF.create();
        AdvertiserAccount account = advertiserAccountTF.createPersistent();

        persistGroup(campaignCreativeGroup, account);

        if (qaStatus == ApproveStatus.DECLINED) {
            campaignCreativeGroupService.decline(campaignCreativeGroup.getId(), "Testing");
        }

        commitChanges();
        campaignCreativeGroup.setName(displayCCGTF.getTestEntityRandomName());
        campaignCreativeGroup = campaignCreativeGroupService.update(campaignCreativeGroup);

        // Commented for now. See OUI-25720
//        assertEquals(Status.PENDING, campaignCreativeGroup.getStatus());
        assertEquals(qaStatus == ApproveStatus.APPROVED? ApproveStatus.APPROVED: ApproveStatus.HOLD,
                campaignCreativeGroup.getQaStatus());
    }

    private CcgRate generateCcgRate(RateType rateType) {
        CcgRate ccgRate = new CcgRate();
        ccgRate.setEffectiveDate(new Date());
        ccgRate.setRateType(rateType);
        ccgRate.setCpa(BigDecimal.TEN);
        ccgRate.setCpc(BigDecimal.TEN);
        ccgRate.setCpm(BigDecimal.TEN);

        return ccgRate;
    }

    private boolean ccgRatesEqual(CcgRate first, CcgRate second) {
        return first != null && second != null &&
            first.getEffectiveDate().equals(second.getEffectiveDate()) &&
            first.getRateType().equals(second.getRateType()) &&
            first.getCpa().equals(second.getCpa()) &&
            first.getCpc().equals(second.getCpc()) &&
            first.getCpm().equals(second.getCpm());
    }

    private FrequencyCap generateFrequencyCap() {
        FrequencyCap freqCap = new FrequencyCap();
        freqCap.setLifeCount(11);
        freqCap.setPeriod(3);
        freqCap.setPeriodSpan(new TimeSpan(44l, TimeUnit.MINUTE));
        freqCap.setWindowCount(5);
        freqCap.setWindowLength(6);
        freqCap.setWindowLengthSpan(new TimeSpan(44l, TimeUnit.SECOND));
        return freqCap;
    }

    private boolean frequencyCapsEqual(FrequencyCap first, FrequencyCap second) {
        return first != null && second != null &&
            first.getLifeCount().equals(second.getLifeCount()) &&
            first.getPeriod().equals(second.getPeriod()) &&
            first.getPeriodSpan().equals(second.getPeriodSpan()) &&
            first.getWindowCount().equals(second.getWindowCount()) &&
            first.getWindowLength().equals(second.getWindowLength()) &&
            first.getWindowLengthSpan().equals(second.getWindowLengthSpan());
    }

    private CampaignCreativeGroup generateCcgPersistent(CCGType ccgType, TGTType tgtType) {
        CampaignCreativeGroup ccg = ccgType == CCGType.DISPLAY ? displayCCGTF.create() : textCCGTF.create();
        ccg.setTgtType(tgtType);
        ccg.setStatus(Status.ACTIVE);

        persistGroup(ccg, advertiserAccountTF.createPersistent());

        return ccg;
    }

    private CampaignCreativeGroup prepareTestChangesInSitesOrCaps(CampaignCreativeGroup campaignCreativeGroup) {
        AdvertiserAccount account = advertiserAccountTF.createPersistent();

        persistGroup(campaignCreativeGroup, account);
        getEntityManager().flush();
        getEntityManager().clear();

        // Commented for now. See OUI-25720
//        campaignCreativeGroupService.activate(campaignCreativeGroup.getId());
        return campaignCreativeGroupService.find(campaignCreativeGroup.getId());
    }

    private CampaignCreativeGroup getGroupWithCapsToUpdate(CampaignCreativeGroup campaignCreativeGroup) {
        CampaignCreativeGroup toUpdate = new CampaignCreativeGroup();
        toUpdate.setId(campaignCreativeGroup.getId());
        toUpdate.setCcgType(campaignCreativeGroup.getCcgType());
        toUpdate.setCcgRate(campaignCreativeGroup.getCcgRate());
        toUpdate.setFrequencyCap(generateFrequencyCap());
        toUpdate.setOptimizeCreativeWeightFlag(campaignCreativeGroup.isOptimizeCreativeWeightFlag());
        return toUpdate;
    }

    private CampaignCreativeGroup getGroupWithSitesToUpdate(CampaignCreativeGroup campaignCreativeGroup) {
        CampaignCreativeGroup toUpdate = new CampaignCreativeGroup();
        toUpdate.setId(campaignCreativeGroup.getId());
        Set<Site> sites = new HashSet<Site>();
        sites.add(siteTF.createPersistent());
        toUpdate.setCcgType(campaignCreativeGroup.getCcgType());
        toUpdate.setCcgRate(campaignCreativeGroup.getCcgRate());
        toUpdate.setSites(sites);
        toUpdate.setIncludeSpecificSitesFlag(true);
        return toUpdate;
    }

    private CampaignCreativeGroup getGroupWithColocationsToUpdate(CampaignCreativeGroup campaignCreativeGroup) {
        CampaignCreativeGroup toUpdate = new CampaignCreativeGroup();
        toUpdate.setId(campaignCreativeGroup.getId());
        Set<Colocation> colocations = new HashSet<Colocation>();
        colocations.add(colocationTF.createPersistent());
        toUpdate.setCcgType(campaignCreativeGroup.getCcgType());
        toUpdate.setCcgRate(campaignCreativeGroup.getCcgRate());
        toUpdate.setColocations(colocations);
        toUpdate.setIspColocationTargetingFlag(true);
        return toUpdate;
    }

    private void setCcgRate(CampaignCreativeGroup campaignCreativeGroup, RateType type) {
        BigDecimal cpc = new BigDecimal(123);
        CcgRate ccgRate = new CcgRate();
        ccgRate.setCcg(campaignCreativeGroup);
        ccgRate.setCpc(cpc);
        ccgRate.setRateType(type);
        ccgRate.setEffectiveDate(new LocalDate().plusYears(100).toDate());

        campaignCreativeGroup.setCcgRate(ccgRate);
    }

    @Test
    public void testBulk() {
        Campaign campaign = textCampaignTF.createPersistent();
        campaign.getAccount().setInternational(true);
        campaign.getAccount().setCountry(entityManager.find(Country.class, "US"));

        commitChanges();
        clearContext();

        CampaignCreativeGroup ccg = newCcg(campaign);
        ccg.setCountry(new Country("RU"));

        campaignCreativeGroupService.validateAll(campaign, TGTType.CHANNEL, Arrays.asList(ccg));
        campaignCreativeGroupService.createOrUpdateAll(campaign.getId(), Arrays.asList(ccg));

        commitChanges();
        clearContext();

        CampaignCreativeGroup ccg2 = newCcg(campaign);
        ccg2.setCountry(new Country("GB"));

        campaignCreativeGroupService.validateAll(campaign, TGTType.CHANNEL, Arrays.asList(ccg2));
        assertEquals(ccg.getId(), ccg2.getId());
        campaignCreativeGroupService.createOrUpdateAll(campaign.getId(), Arrays.asList(ccg2));

        commitChanges();
        clearContext();

        CampaignCreativeGroup ccg3 = entityManager.find(CampaignCreativeGroup.class, ccg.getId());
        assertNotNull(ccg3);
        assertEquals("GB", ccg3.getCountry().getCountryCode());

        BehavioralChannel channel = behavioralChannelTF.createPersistent(campaign.getAccount());
        commitChanges();
        clearContext();

        CampaignCreativeGroup ccg4 = newCcg(campaign);
        ccg4.setCountry(null);
        ccg4.setChannelTarget(ChannelTarget.TARGETED);
        ccg4.setChannel(new ChannelExpressionLink(ExpressionHelper.formatChannelName(channel)));
        campaignCreativeGroupService.validateAll(campaign, TGTType.CHANNEL, Arrays.asList(ccg4));
        assertEquals(UploadStatus.REJECTED, UploadUtils.getUploadContext(ccg4).getStatus());
    }

    private CampaignCreativeGroup newCcg(Campaign campaign) {
        CampaignCreativeGroup ccg = new CampaignCreativeGroup();
        Campaign ccgCampaign = new Campaign(campaign.getId());
        ccgCampaign.setName(campaign.getName());
        ccg.setCampaign(ccgCampaign);
        ccg.setBudget(BigDecimal.TEN);
        ccg.setDateStart(campaign.getDateStart());
        ccg.setDateEnd(campaign.getDateEnd());
        ccg.setDailyBudget(BigDecimal.TEN);
        ccg.setTgtType(TGTType.CHANNEL);
        ccg.setChannelTarget(ChannelTarget.UNTARGETED);
        ccg.setCcgType(CCGType.TEXT);
        ccg.setName("Test");
        ccg.setCountry(new Country(campaign.getAccount().getCountry().getCountryCode()));
        CcgRate rate = new CcgRate();
        rate.setCpc(BigDecimal.ONE);
        rate.setRateType(RateType.CPC);
        ccg.setCcgRate(rate);
        ccg.setMinUidAge(0L);
        return ccg;
    }

    @Test
    public void testOptInStatusTargeting() throws Exception {
        CampaignCreativeGroup ccg = displayCCGTF.create();
        OptInStatusTargeting targeting1 = new OptInStatusTargeting(true, false, true);
        ccg.setOptInStatusTargeting(targeting1);

        campaignCreativeGroupService.create(ccg);
        entityManager.flush();
        entityManager.clear();

        CampaignCreativeGroup found = campaignCreativeGroupService.find(ccg.getId());
        assertEquals(targeting1, found.getOptInStatusTargeting());

        ccg = new CampaignCreativeGroup(ccg.getId());
        OptInStatusTargeting targeting2 = new OptInStatusTargeting(false, true, true);
        ccg.setOptInStatusTargeting(targeting2);

        campaignCreativeGroupService.update(ccg);
        entityManager.flush();
        entityManager.clear();

        found = campaignCreativeGroupService.find(ccg.getId());
        assertEquals(targeting2, found.getOptInStatusTargeting());
    }

    @Test
    public void testFetchTargetableSites() throws Exception {
        Site site = siteTF.createPersistent();
        // scenario non test site account, non test advertiser/agency account
        assertTrue("fetchTargetableSites() can't find newly created site", checkForSiteExistance(site, false));
        getEntityManager().clear();

        // scenario non test site account, test advertiser/agency account
        assertTrue("fetchTargetableSites() can't find newly created site", checkForSiteExistance(site, true));

        // set site account test flag
        ExternalAccount account = accountService.findPublisherAccount(site.getAccount().getId());

        getEntityManager().clear();
        account.setFlags(FlagsUtil.set(account.getFlags(), Account.TEST_FLAG, true));
        accountService.updateExternalAccount(account);

        getEntityManager().flush();
        getEntityManager().clear();
        // scenario test site account, non test advertiser/agency account
        assertFalse("fetchTargetableSites() fetched unexpected site", checkForSiteExistance(site, false));

        getEntityManager().clear();
        // scenario test site account, test advertiser/agency account
        assertTrue("fetchTargetableSites() can't find newly created site", checkForSiteExistance(site, true));
    }

    private boolean checkForSiteExistance(Site site, boolean testAdvAccount) {
        Long id = site.getId();

        for (EntityTO to : campaignCreativeGroupService.fetchTargetableSites(testAdvAccount, site.getAccount().getCountry().getCountryCode())) {
            if (to.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    private void assertTargetingChannelExists(CampaignCreativeGroup campaignCreativeGroup) {
        Long targetingChannelId = jdbcTemplate.queryForObject(
                "select targeting_channel_id  from campaigncreativegroup where ccg_id= ?",
                Long.class,
                campaignCreativeGroup.getId()
        );
        assertTrue(targetingChannelId != null && targetingChannelId > 0);
    }

    @Test
    public void testFetchTargetingStats() {
        CampaignCreativeGroup ccg = textCCGTF.createPersistent();
        assertNotNull(campaignCreativeGroupService.fetchTargetingStats(ccg.getId(), true));
    }

    @Test
    public void testFindColocations() {
        CampaignCreativeGroup ccg = displayCCGTF.createPersistent();
        ccg = getGroupWithColocationsToUpdate(ccg);
        displayCCGTF.update(ccg);
        commitChanges();

        Collection<ISPColocationTO> colocations = campaignCreativeGroupService.findLinkedColocations(ccg.getId());
        assertNotNull(colocations);
        assertEquals(ccg.getColocations().size(), colocations.size());

        List<Long> ids = CollectionUtils.convert(colocations, new Converter<ISPColocationTO, Long>() {
            @Override
            public Long item(ISPColocationTO value) {
                return value.getId();
            }
        });

        colocations = campaignCreativeGroupService.findColocationsByIds(new HashSet<Long>(ids));
        assertNotNull(colocations);
        assertEquals(ccg.getColocations().size(), colocations.size());

        CollectionUtils.convert(colocations, new Converter<ISPColocationTO, Long>() {
            @Override
            public Long item(ISPColocationTO value) {
                return value.getId();
            }
        });

        Colocation colocation = ccg.getColocations().iterator().next();
        colocations = campaignCreativeGroupService.findColocationsByName(
                colocation.getName(),
                colocation.getAccount().getCountry().getCountryCode(),
                false,
                99
        );
        assertNotNull(colocations);
        assertFalse(colocations.isEmpty());
    }

    @Test
    public void testFindCCGNamesByCampaign() {
        CampaignCreativeGroup ccg = displayCCGTF.createPersistent();
        List<String> res = campaignCreativeGroupService.findCCGNamesByCampaign(ccg.getCampaign().getId());
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(ccg.getName(), res.get(0));
    }

    @Test
    public void testGetLinkedConversions() {
        CampaignCreativeGroup ccg = textCCGTF.createPersistent();
        Action action = actionTestTF.createPersistent(ccg.getAccount());
        campaignCreativeGroupService.linkConversions(Arrays.asList(ccg.getId()), Arrays.asList(action.getId()));
        commitChanges();
        clearContext();

        List<LinkedConversionTO> conversions = campaignCreativeGroupService.getLinkedConversions(ccg.getId(), new LocalDate(), new LocalDate());
        assertTrue(!conversions.isEmpty());

    }

    @Test
    public void testUpdateLinkedEndDate() {
        Campaign campaign = displayCampaignTF.create();
        campaign.setDateEnd(campaign.getDateStart());
        displayCampaignTF.persist(campaign);

        CampaignCreativeGroup ccg = displayCCGTF.create(campaign);
        ccg.setDateEnd(new Date());
        ccg.setLinkedToCampaignEndDateFlag(true);
        ccg.setId(campaignCreativeGroupService.create(ccg));
        commitChanges();

        ccg = entityManager.find(CampaignCreativeGroup.class, ccg.getId());
        assertNull(ccg.getDateEnd());
        clearContext();

        CampaignCreativeGroup toUpdate = new CampaignCreativeGroup();
        toUpdate.setId(ccg.getId());
        toUpdate.setLinkedToCampaignEndDateFlag(false);
        toUpdate.setDateEnd(campaign.getDateEnd());
        displayCCGTF.update(toUpdate);

        campaign = entityManager.find(Campaign.class, campaign.getId());
        ccg = entityManager.find(CampaignCreativeGroup.class, ccg.getId());
        assertEquals(campaign.getDateEnd(), ccg.getDateEnd());
    }

    private SecurityPrincipal getRealAdvertisingPrincipal() {
        AccountType agencyAccountType = agencyAccountTypeTestFactory.createPersistent();
        AgencyAccount account = agencyAccountTestFactory.createPersistent(agencyAccountType);
        UserRole userRole = new UserRole(ADVERTISER_PRINCIPAL.getUserRoleId());
        User user = userTestFactory.createPersistent(account, userRole);
        commitChangesAndClearContext();

        return new MockPrincipal(
                "testAdvertiser@ocslab.com",
                user.getId(),
                account.getId(),
                userRole.getId(),
                (long)account.getRole().getId()
        );
    }

    private void checkDeviceChannels(CampaignCreativeGroup targetGroup, CampaignCreativeGroup expectedGroup) {
        Set<Long> allowedDeviceIds = EntityUtils.getEntityIds(targetGroup.getAccount().getAccountType().getDeviceChannels());
        Set<Long> realDeviceIds = toAllIfEmpty(EntityUtils.getEntityIds(targetGroup.getDeviceChannels()));

        Set<Long> expectedDeviceIds;
        if (expectedGroup != null) {
            assertEquals(expectedGroup.getAccount(), targetGroup.getAccount());
            expectedDeviceIds = toAllIfEmpty(EntityUtils.getEntityIds(expectedGroup.getDeviceChannels()));
        } else {
            // By default, group devices channels collection = account type devices channels collection
            expectedDeviceIds = allowedDeviceIds;
        }

        assertEquals(deviceChannelService.getNormalizedDeviceChannelsCollection(expectedDeviceIds, allowedDeviceIds),
                deviceChannelService.getNormalizedDeviceChannelsCollection(realDeviceIds, allowedDeviceIds));
    }

    private Set<Long> toAllIfEmpty(Set<Long> devices) {
        if (devices == null || devices.isEmpty()) {
            Set<Long> result = new HashSet<>();
            result.add(deviceChannelService.getBrowsersChannel().getId());
            result.add(deviceChannelService.getApplicationsChannel().getId());
            return result;
        }
        return devices;
    }
}
