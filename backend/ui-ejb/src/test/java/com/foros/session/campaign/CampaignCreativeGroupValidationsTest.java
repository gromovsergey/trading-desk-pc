package com.foros.session.campaign;

import com.foros.AbstractValidationsTest;
import com.foros.model.Country;
import com.foros.model.FrequencyCap;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.IspAccount;
import com.foros.model.campaign.CCGSchedule;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CcgRate;
import com.foros.model.campaign.ChannelTarget;
import com.foros.model.campaign.DeliveryPacing;
import com.foros.model.campaign.OptInStatusTargeting;
import com.foros.model.campaign.RateType;
import com.foros.model.campaign.TGTType;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.DeviceChannel;
import com.foros.model.channel.GeoChannel;
import com.foros.model.isp.Colocation;
import com.foros.model.security.AccountType;
import com.foros.model.security.AccountTypeCCGType;
import com.foros.model.time.TimeSpan;
import com.foros.model.time.TimeUnit;
import com.foros.security.AccountRole;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.OperationType;
import com.foros.session.bulk.Operations;
import com.foros.session.channel.geo.GeoChannelService;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.AdvertiserAccountTypeTestFactory;
import com.foros.test.factory.AudienceChannelTestFactory;
import com.foros.test.factory.BehavioralChannelTestFactory;
import com.foros.test.factory.ColocationTestFactory;
import com.foros.test.factory.CountryTestFactory;
import com.foros.test.factory.DeviceChannelTestFactory;
import com.foros.test.factory.DisplayCampaignTestFactory;
import com.foros.test.factory.DisplayCCGTestFactory;
import com.foros.test.factory.IspAccountTestFactory;
import com.foros.test.factory.SiteTestFactory;
import com.foros.test.factory.TextCCGTestFactory;
import com.foros.test.factory.TextCampaignTestFactory;
import com.foros.validation.constraint.violation.ConstraintViolation;

import group.Db;
import group.Validation;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class CampaignCreativeGroupValidationsTest extends AbstractValidationsTest {
    @Autowired
    private TextCCGTestFactory textCCGTF;

    @Autowired
    private DisplayCCGTestFactory displayCCGTF;

    @Autowired
    private CountryTestFactory countryTF;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private TextCampaignTestFactory textCampaignTF;

    @Autowired
    private DisplayCampaignTestFactory displayCampaignTF;

    @Autowired
    private DeviceChannelTestFactory deviceChannelTF;

    @Autowired
    private AdvertiserAccountTypeTestFactory advertiserTF;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    private BehavioralChannelTestFactory behavioralChannelTestFactory;

    @Autowired
    private AudienceChannelTestFactory audienceChannelTestFactory;

    @Autowired
    public ColocationTestFactory colocationTF;

    @Autowired
    public IspAccountTestFactory ispAccountTF;

    @Autowired
    public CampaignCreativeGroupService ccgService;

    @Autowired
    public SiteTestFactory siteTF;

    @Autowired
    public GeoChannelService geoChannelService;

    @Test
    public void testDefaultCreate() {
        CampaignCreativeGroup ccg = textCCGTF.create();
        testDefaultInternal("CampaignCreativeGroup.create", ccg);
    }

    @Test
    public void testExceedingDailyBudget() {
        CampaignCreativeGroup ccg = textCCGTF.create();
        ccg.setBudget(null);
        ccg.setDailyBudget(new BigDecimal(1000000000));
        validate("CampaignCreativeGroup.create", ccg);
        assertHasViolation("dailyBudget");

        ccg.setBudget(new BigDecimal(100));
        ccg.setDailyBudget(new BigDecimal(100));
        validate("CampaignCreativeGroup.create", ccg);
        assertViolationsCount(0);
    }

    @Test
    public void testDefaultUpdate() {
        CampaignCreativeGroup ccg = textCCGTF.createPersistent();
        clearContext();
        testDefaultInternal("CampaignCreativeGroup.update", ccg);
    }

    @Test
    public void testSetAudienceChannelTarget() {
        AdvertiserAccount advertiserAccount = advertiserAccountTF.createPersistent();
        Campaign campaign = textCampaignTF.createPersistent(advertiserAccount);
        CampaignCreativeGroup group = textCCGTF.create(campaign);
        group.setTgtType(TGTType.CHANNEL);
        textCCGTF.persist(group);

        AudienceChannel channel = audienceChannelTestFactory.create(group.getAccount());
        channel.setCountry(group.getCountry());
        audienceChannelTestFactory.persist(channel);

        clearContext();

        group.setChannelTarget(ChannelTarget.TARGETED);
        group.setChannel(channel);
        validate("CampaignCreativeGroup.update", group);
        assertViolationsCount(0);
    }

    @Test
    public void testUpdateCountry() {

        AdvertiserAccount advertiserAccount = advertiserAccountTF.create();
        advertiserAccount.setInternational(true);
        advertiserAccountTF.persist(advertiserAccount);
        Campaign campaign = textCampaignTF.createPersistent(advertiserAccount);
        CampaignCreativeGroup group = textCCGTF.create(campaign);
        group.setTgtType(TGTType.CHANNEL);
        textCCGTF.persist(group);

        validate("CampaignCreativeGroup.update", group);
        assertViolationsCount(0);

        group.setCountry(countryTF.findOrCreatePersistent("RU"));
        validate("CampaignCreativeGroup.update", group);
        assertViolationsCount(0);

        group.setChannelTarget(ChannelTarget.TARGETED);
        BehavioralChannel channel = behavioralChannelTestFactory.create(group.getAccount());
        channel.setCountry(group.getCountry());
        group.setChannel(channel);
        behavioralChannelTestFactory.persist(channel);
        getEntityManager().merge(group);
        getEntityManager().flush();
        getEntityManager().clear();
        group.setCountry(countryTF.findOrCreatePersistent("UA"));
        validate("CampaignCreativeGroup.update", group);
        assertViolationsCount(1);

        group.setChannelTarget(ChannelTarget.UNTARGETED);
        group.setChannel(null);
        getEntityManager().merge(group);
        getEntityManager().flush();
        getEntityManager().clear();
        group.setCountry(countryTF.findOrCreatePersistent("RU"));
        validate("CampaignCreativeGroup.update", group);
        assertViolationsCount(0);

        group = getEntityManager().find(CampaignCreativeGroup.class, group.getId());
        group.getCcgSchedules().size();
        Country gbCountry = countryTF.findOrCreatePersistent("GB");
        GeoChannel gbState = geoChannelService.getStates(gbCountry).iterator().next();
        group.setGeoChannels(Collections.singleton(gbState));
        getEntityManager().flush();
        getEntityManager().clear();
        group.setCountry(countryTF.findOrCreatePersistent("US"));
        validate("CampaignCreativeGroup.update", group);
        assertViolationsCount(1);
    }

    @Test
    public void testInvalidCountry() {
        AdvertiserAccount advertiserAccount = advertiserAccountTF.create();
        advertiserAccount.setInternational(true);
        advertiserAccountTF.persist(advertiserAccount);
        Campaign campaign = textCampaignTF.createPersistent(advertiserAccount);
        CampaignCreativeGroup group = textCCGTF.create(campaign);
        group.setTgtType(TGTType.CHANNEL);

        group.setCountry(new Country("GBBBBBB"));
        validate("CampaignCreativeGroup.create", group);

        assertViolationsCount(1);

        ConstraintViolation violation = violations.iterator().next();
        assertTrue(violation.getPropertyPath().toString().equals("country.countryCode"));
        assertTrue(violation.getMessage().equals("Entity not found"));
    }

    @Test
    public void testDeliverySchedule() {
        CampaignCreativeGroup ccg = textCCGTF.create();
        testDeliveryScheduleForInvalidTimeSlot("CampaignCreativeGroup.create", ccg);

        ccg = textCCGTF.create();
        testDeliveryScheduleForIntersection("CampaignCreativeGroup.create", ccg);

        ccg = textCCGTF.createPersistent();
        testDeliveryScheduleForConflict("CampaignCreativeGroup.update", ccg);
    }

    @Test
    public void testDeliveryFlag() {
        CampaignCreativeGroup ccg = textCCGTF.create();

        Set<CCGSchedule> schedules = new LinkedHashSet<>();
        CCGSchedule schedule = new CCGSchedule();
        schedule.setTimeFrom(60L);
        schedule.setTimeTo(89L);
        schedules.add(schedule);
        ccg.setCcgSchedules(schedules);

        validate("CampaignCreativeGroup.create", ccg);
        assertHasViolation("deliverySchedule");
    }

    @Test
    public void testMobileDeviceTargeting() throws Exception {
        AccountType accountType = advertiserTF.create();
        accountType.getDeviceChannels().clear();
        accountType.getDeviceChannels().add(deviceChannelTF.getMobileDeviceChannel());
        advertiserTF.persist(accountType);

        AdvertiserAccount acc = advertiserAccountTF.createPersistent(accountType);
        Campaign campaign = textCampaignTF.createPersistent(acc);
        CampaignCreativeGroup ccg = textCCGTF.createPersistent(campaign);
        ccg.getDeviceChannels().clear();
        // save with one mobile not-root channel
        ccg.getDeviceChannels().add(deviceChannelTF.createPersistent(deviceChannelTF.getMobileDeviceChannel()));

        textCCGTF.update(ccg);
        commitChanges();
        clearContext();

        testValidateDeviceTargeting(ccg, false, true, 1);
        testValidateDeviceTargeting(ccg, true, false, 0);
        testValidateDeviceTargeting(ccg, false, false, 1);
    }

    @Test
    public void testDeviceTargetingUncheck() throws Exception {
        AccountType accountType = advertiserTF.create();
        accountType.getDeviceChannels().clear();
        accountType.getDeviceChannels().add(deviceChannelTF.getMobileDeviceChannel());
        accountType.getDeviceChannels().add(deviceChannelTF.getNonMobileDeviceChannel());
        advertiserTF.persist(accountType);

        AdvertiserAccount acc = advertiserAccountTF.createPersistent(accountType);
        Campaign campaign = textCampaignTF.createPersistent(acc);
        CampaignCreativeGroup ccg = textCCGTF.createPersistent(campaign);
        commitChanges();

        // uncheck non-mobile in account type
        clearContext();
        accountType.getDeviceChannels().clear();
        accountType.getDeviceChannels().add(deviceChannelTF.getMobileDeviceChannel());
        advertiserTF.update(accountType);

        // it's not ok to save non-mobile
        testValidateDeviceTargeting(ccg, true, true, 1);
        // it's ok to save mobile
        testValidateDeviceTargeting(ccg, true, false, 0);
    }

    @Test
    public void testNonMobileDeviceTargeting() throws Exception {
        AccountType accountType = advertiserTF.create();
        accountType.getDeviceChannels().clear();
        accountType.getDeviceChannels().add(deviceChannelTF.getNonMobileDeviceChannel());
        advertiserTF.persist(accountType);

        AdvertiserAccount acc = advertiserAccountTF.createPersistent(accountType);
        Campaign campaign = textCampaignTF.createPersistent(acc);
        CampaignCreativeGroup ccg = textCCGTF.createPersistent(campaign);

        commitChanges();
        clearContext();
        testValidateDeviceTargeting(ccg, false, true, 0);
        testValidateDeviceTargeting(ccg, true, false, 1);
        testValidateDeviceTargeting(ccg, false, false, 1);
    }

    @Test
    public void testChildMobileChannels() throws Exception {
        AccountType accountType = advertiserTF.createPersistent();
        AdvertiserAccount acc = advertiserAccountTF.createPersistent(accountType);
        Campaign campaign = textCampaignTF.createPersistent(acc);
        CampaignCreativeGroup ccg = textCCGTF.createPersistent(campaign);
        DeviceChannel child = deviceChannelTF.createPersistent(deviceChannelTF.getMobileDeviceChannel());
        commitChanges();

        ccg.getDeviceChannels().clear();
        ccg.getDeviceChannels().add(child);
        clearContext();
        validate("CampaignCreativeGroup.updateDeviceTargeting", ccg);
        // not allowed by account type
        assertHasViolation("deviceTargetingOptions");
        commitChanges();

        clearContext();
        accountType.getDeviceChannels().add(child);
        advertiserTF.update(accountType);
        commitChanges();
        validate("CampaignCreativeGroup.updateDeviceTargeting", ccg);
        // it's ok. allowed by account type
        assertHasNoViolation("deviceTargetingOptions");

        ccg.getDeviceChannels().clear();
        ccg.getDeviceChannels().add(deviceChannelTF.getMobileDeviceChannel());
        ccg.getDeviceChannels().add(child);
        validate("CampaignCreativeGroup.updateDeviceTargeting", ccg);
        // child channel can be selected
        assertHasNoViolation("deviceTargetingOptions");

        DeviceChannel childOfChild = deviceChannelTF.createPersistent(child);
        deviceChannelTF.delete(child);
        ccg.getDeviceChannels().clear();
        ccg.getDeviceChannels().add(deviceChannelTF.getMobileDeviceChannel());
        ccg.getDeviceChannels().add(childOfChild);
        validate("CampaignCreativeGroup.updateDeviceTargeting", ccg);
        // child of not live channel cannot be selected
        assertHasViolation("deviceTargetingOptions");
    }

    @Test
    public void testUserSampleGroupUpdate() {
        AccountType accountType = advertiserTF.createPersistent();
        AdvertiserAccount acc = advertiserAccountTF.createPersistent(accountType);
        Campaign campaign = textCampaignTF.createPersistent(acc);
        CampaignCreativeGroup ccg = textCCGTF.createPersistent(campaign);
        ccg.setUserSampleGroupEnd(30L);
        ccg.setUserSampleGroupStart(10L);

        validate("CampaignCreativeGroup.updateUserSampleGroups", ccg);
        assertHasNoViolation("userSampleGroups");

        ccg.setUserSampleGroupEnd(9L);
        validate("CampaignCreativeGroup.updateUserSampleGroups", ccg);
        assertHasViolation("userSampleGroups");

        ccg.setUserSampleGroupEnd(null);
        validate("CampaignCreativeGroup.updateUserSampleGroups", ccg);
        assertHasViolation("userSampleGroups");
    }

    @Test
    public void testOptInStatusTageting() {
        CampaignCreativeGroup ccg = displayCCGTF.createPersistent();

        ccg.setOptInStatusTargeting(new OptInStatusTargeting(false, false, false));
        validate("CampaignCreativeGroup.update", ccg);
        assertHasViolation("optInStatusTargeting");

        ccg.setOptInStatusTargeting(new OptInStatusTargeting(true, false, false));
        validate("CampaignCreativeGroup.update", ccg);
        assertHasNoViolation("optInStatusTargeting");

        ccg.setOptInStatusTargeting(null);
        validate("CampaignCreativeGroup.update", ccg);
        assertHasNoViolation("optInStatusTargeting");
    }

    @Test
    public void testMinUidAge() {
        CampaignCreativeGroup ccg = displayCCGTF.createPersistent();

        ccg.setMinUidAge(null);
        validate("CampaignCreativeGroup.update", ccg);
        assertHasViolation("minUidAge");

        ccg.setOptInStatusTargeting(new OptInStatusTargeting(false, true, true));
        ccg.setMinUidAge(0L);
        validate("CampaignCreativeGroup.update", ccg);
        assertHasNoViolation("minUidAge");

        ccg.setMinUidAge(1L);
        validate("CampaignCreativeGroup.update", ccg);
        assertHasViolation("minUidAge");

        ccg.setOptInStatusTargeting(null);
        ccg.setMinUidAge(1L);
        validate("CampaignCreativeGroup.update", ccg);
        assertHasViolation("minUidAge");

        ccg.setOptInStatusTargeting(new OptInStatusTargeting(true, false, false));
        ccg.setMinUidAge(0L);
        validate("CampaignCreativeGroup.update", ccg);
        assertHasNoViolation("minUidAge");

        ccg.setMinUidAge(1L);
        validate("CampaignCreativeGroup.update", ccg);
        assertHasNoViolation("minUidAge");

        ccg.setMinUidAge(10000L);
        validate("CampaignCreativeGroup.update", ccg);
        assertHasNoViolation("minUidAge");

        ccg.setMinUidAge(-1L);
        validate("CampaignCreativeGroup.update", ccg);
        assertHasViolation("minUidAge");

        ccg.setMinUidAge(10001L);
        validate("CampaignCreativeGroup.update", ccg);
        assertHasViolation("minUidAge");
    }

    @Test
    public void testColocations() {
        CampaignCreativeGroup ccg = displayCCGTF.createPersistent();
        commitChanges();
        clearContext();

        // right colocations
        IspAccount ispAccount = ispAccountTF.create();
        ispAccount.setCountry(ccg.getCountry());
        ispAccountTF.persist(ispAccount);
        Set<Colocation> colocations = new HashSet<>();
        colocations.add(colocationTF.createPersistent(ispAccount));
        colocations.add(colocationTF.createPersistent(ispAccount));
        ccg.setColocations(colocations);
        validate("CampaignCreativeGroup.update", ccg);
        assertHasNoViolation("colocations[0]");

        // wrong colocation account country
        IspAccount ispAccount2 = ispAccountTF.create();
        ispAccount2.setCountry(countryTF.findOrCreatePersistent("RU"));
        ispAccountTF.persist(ispAccount2);
        Set<Colocation> colocations2 = new HashSet<>();
        colocations2.add(colocationTF.createPersistent(ispAccount2));

        ccg.setColocations(colocations2);
        validate("CampaignCreativeGroup.update", ccg);
        assertHasViolation("colocations[0]");

        // test account colocation
        IspAccount ispAccount3 = ispAccountTF.create();
        ispAccount3.setCountry(ccg.getCountry());
        ispAccount3.setFlags(ispAccount3.getFlags() | Account.TEST_FLAG);
        ispAccountTF.persist(ispAccount3);
        Set<Colocation> colocations3 = new HashSet<>();
        colocations3.add(colocationTF.createPersistent(ispAccount3));

        ccg.setColocations(colocations3);
        validate("CampaignCreativeGroup.update", ccg);
        assertHasViolation("colocations[0]");
    }

    @Test
    public void testExternalFCUpdate() {
        CampaignCreativeGroup ccg = ccgForFCUpdateTests();

        currentUserRule.setPrincipal(ADVERTISER_PRINCIPAL);

        CcgRate ccgRate = ccgRateForFCUpdateTests();
        ccg.setCcgRate(ccgRate);
        validate("CampaignCreativeGroup.update", ccg);
        assertHasNoViolation("ccgRate");

        FrequencyCap cap = frequencyCapForFCUpdateTests();
        ccg.setFrequencyCap(cap);
        validate("CampaignCreativeGroup.update", ccg);
        assertHasNoViolation("frequencyCap");

        ccgRate.setRateType(RateType.CPM);
        ccg.setCcgRate(ccgRate);
        validate("CampaignCreativeGroup.update", ccg);
        assertHasNoViolation("ccgRate");
        assertHasNoViolation("frequencyCap");
    }

    @Test
    public void testInternalFCUpdate() {
        CampaignCreativeGroup ccg = ccgForFCUpdateTests();

        CcgRate ccgRate = ccgRateForFCUpdateTests();
        ccg.setCcgRate(ccgRate);
        validate("CampaignCreativeGroup.update", ccg);
        assertHasNoViolation("ccgRate");

        FrequencyCap cap = frequencyCapForFCUpdateTests();
        ccg.setFrequencyCap(cap);
        validate("CampaignCreativeGroup.update", ccg);
        assertHasNoViolation("ccgRate");
        assertHasNoViolation("frequencyCap");

        ccgRate.setRateType(RateType.CPM);
        ccgRate.setCpc(null);
        ccgRate.setCpm(BigDecimal.TEN);
        ccg.setCcgRate(ccgRate);
        validate("CampaignCreativeGroup.update", ccg);
        assertHasNoViolation("ccgRate");
        assertHasNoViolation("frequencyCap");

        ccgRate.setRateType(RateType.CPA);
        ccgRate.setCpm(null);
        ccgRate.setCpa(BigDecimal.TEN);
        ccg.setCcgRate(ccgRate);
        validate("CampaignCreativeGroup.update", ccg);
        assertHasNoViolation("ccgRate");
        assertHasNoViolation("frequencyCap");
    }

    private CampaignCreativeGroup ccgForFCUpdateTests() {
        //Account Type
        AccountType accountType = advertiserTF.create();
        accountType.setAccountRole(AccountRole.ADVERTISER);

        Set<AccountTypeCCGType> ccgTypes = new HashSet<>();
        ccgTypes.add(new AccountTypeCCGType(accountType, CCGType.TEXT, TGTType.CHANNEL, RateType.CPA));
        ccgTypes.add(new AccountTypeCCGType(accountType, CCGType.TEXT, TGTType.CHANNEL, RateType.CPC));
        ccgTypes.add(new AccountTypeCCGType(accountType, CCGType.TEXT, TGTType.CHANNEL, RateType.CPM));
        accountType.setCcgTypes(ccgTypes);

        advertiserTF.persist(accountType);

        //Other
        AdvertiserAccount advertiserAccount = advertiserAccountTF.createPersistent(accountType);
        Campaign campaign = textCampaignTF.createPersistent(advertiserAccount);
        CampaignCreativeGroup ccg = textCCGTF.create(campaign);
        ccg.setTgtType(TGTType.CHANNEL);
        textCCGTF.persist(ccg);

        return ccg;
    }

    private CcgRate ccgRateForFCUpdateTests() {
        CcgRate ccgRate = new CcgRate();
        ccgRate.setEffectiveDate(new Date());
        ccgRate.setRateType(RateType.CPC);
        ccgRate.setCpa(BigDecimal.TEN);
        ccgRate.setCpc(BigDecimal.TEN);
        ccgRate.setCpm(BigDecimal.TEN);

        return ccgRate;
    }

    private FrequencyCap frequencyCapForFCUpdateTests() {
        FrequencyCap cap = new FrequencyCap();
        cap.setLifeCount(1);
        cap.setPeriod(2);
        cap.setPeriodSpan(new TimeSpan(3l, TimeUnit.MINUTE));
        cap.setWindowCount(4);
        cap.setWindowLength(5);
        cap.setWindowLengthSpan(new TimeSpan(3l, TimeUnit.MINUTE));

        return cap;
    }

    private void testDefaultInternal(String validationName, CampaignCreativeGroup ccg) {
        validate(validationName, ccg);
        assertViolationsCount(0);

        CcgRate ccgRate = new CcgRate();
        ccgRate.setEffectiveDate(new Date());
        ccgRate.setCpc(null);
        ccg.setCcgRate(ccgRate);
        validate(validationName, ccg);
        assertHasViolation("ccgRate");

        ccgRate.setCpc(new BigDecimal(-1));
        validate(validationName, ccg);
        assertHasViolation("ccgRate");

        ccgRate.setCpc(new BigDecimal(999999999.99));
        validate(validationName, ccg);
        assertHasViolation("ccgRate");
        ccgRate.setCpc(BigDecimal.ONE);

        ccg.setBudget(new BigDecimal(999999999.99));
        validate(validationName, ccg);
        assertHasViolation("budget");

        ccg.setBudget(new BigDecimal(99.99999999));
        validate(validationName, ccg);
        assertHasViolation("budget");
        ccg.setBudget(BigDecimal.ONE);

        ccg.setDailyBudget(null);
        validate(validationName, ccg);
        assertHasViolation("dailyBudget");

        ccg.setDailyBudget(BigDecimal.ZERO);
        validate(validationName, ccg);
        assertHasViolation("dailyBudget");

        ccg.setDailyBudget(new BigDecimal(99.99999999));
        validate(validationName, ccg);
        assertHasViolation("dailyBudget");
        ccg.setDailyBudget(BigDecimal.ONE);

        Date date = new Date();
        ccg.setDateEnd(date);
        ccg.setDateStart(new Date(date.getTime() + 100));
        ccg.setLinkedToCampaignEndDateFlag(false);
        validate(validationName, ccg);
        assertHasViolation("dateEnd");

        ccg.getCampaign().setDateStart(new Date(ccg.getDateStart().getTime() + 1000));
        validate(validationName, ccg);
        assertHasViolation("dateStart");

        ccg.getCampaign().setDateEnd(new Date(ccg.getDateEnd().getTime() - 1000));
        validate(validationName, ccg);
        assertHasViolation("dateStart");

        ccg.setCountry(null);
        validate(validationName, ccg);
        assertHasViolation("country");

        ccg.setCountry(countryTF.findOrCreatePersistent("RU"));
        validate(validationName, ccg);
        assertHasViolation("country");
        ccg.setDeliveryScheduleFlag(true);
        ccg.setCcgSchedules(new LinkedHashSet<CCGSchedule>());
        validate(validationName, ccg);
        assertHasViolation("deliverySchedule");

        textCCGTF.addSchedule(ccg, 0L, 30L);
        validate(validationName, ccg);
        assertHasViolation("deliverySchedule");

        if (ccg.getCcgType() == CCGType.TEXT) {
            ccg.setDeliveryPacing(DeliveryPacing.UNRESTRICTED);
            validate(validationName, ccg);
            assertHasViolation("deliveryPacing");
        }
    }

    private void testDeliveryScheduleForInvalidTimeSlot(String validationName, CampaignCreativeGroup ccg) {
        // timeTo exceeds maximum limit
        textCCGTF.addSchedule(ccg, 0L, 10080L);
        validate(validationName, ccg);
        assertHasViolation("deliverySchedule");

        // timeFrom > timeTo
        textCCGTF.addSchedule(ccg, 59L, 0L);
        validate(validationName, ccg);
        assertHasViolation("deliverySchedule");

        // timeFrom not a multiple of 30.
        textCCGTF.addSchedule(ccg, 1L, 20L);
        validate(validationName, ccg);
        assertHasViolation("deliverySchedule");
    }

    private void testDeliveryScheduleForIntersection(String validationName, CampaignCreativeGroup ccg) {
        textCCGTF.addSchedule(ccg, 0L, 59L);
        textCCGTF.addSchedule(ccg, 0L, 29L);
        validate(validationName, ccg);
        assertHasViolation("deliverySchedule");
    }

    private void testDeliveryScheduleForConflict(String validationName, CampaignCreativeGroup ccg) {
        Campaign campaign = textCampaignTF.create();
        textCampaignTF.addSchedule(campaign, 0L, 59L);
        campaignService.create(campaign);
        commitChanges();

        ccg.setCampaign(campaign);
        textCCGTF.addSchedule(ccg, 60L, 89L);
        ccg.setDeliveryScheduleFlag(true);

        validate(validationName, ccg);
        assertHasViolation("conflictedDeliverySchedule");
    }

    private void testValidateDeviceTargeting(CampaignCreativeGroup ccg, boolean addMobile, boolean addNonMobile, int violationsSize) {
        clearContext();
        ccg.getDeviceChannels().clear();
        if (addMobile) {
            ccg.getDeviceChannels().add(deviceChannelTF.getMobileDeviceChannel());
        }
        if (addNonMobile) {
            ccg.getDeviceChannels().add(deviceChannelTF.getNonMobileDeviceChannel());
        }
        validate("CampaignCreativeGroup.updateDeviceTargeting", ccg);

        assertEquals(violationsSize, violations.size());
    }

    @Test
    public void testUpdateChannelTarget() {
        CampaignCreativeGroup group = textCCGTF.create();
        group.setTgtType(TGTType.CHANNEL);
        textCCGTF.persist(group);

        BehavioralChannel channel = behavioralChannelTestFactory.createPersistent(group.getAccount());

        // don't update fields
        validate("CampaignCreativeGroup.updateTarget", group);
        assertViolationsCount(0);

        // don't set channel
        group.setChannel(null);
        group.setChannelTarget(ChannelTarget.NOT_SET);
        validate("CampaignCreativeGroup.updateTarget", group);
        assertViolationsCount(0);

        group.setChannel(null);
        group.setChannelTarget(ChannelTarget.UNTARGETED);
        validate("CampaignCreativeGroup.updateTarget", group);
        assertViolationsCount(0);

        group.setChannel(null);
        group.setChannelTarget(ChannelTarget.TARGETED);
        validate("CampaignCreativeGroup.updateTarget", group);
        assertViolationsCount(1);

        //set channel
        group.setChannel(channel);
        group.setChannelTarget(ChannelTarget.TARGETED);
        validate("CampaignCreativeGroup.updateTarget", group);
        assertViolationsCount(0);

        group.setChannel(channel);
        group.setChannelTarget(ChannelTarget.UNTARGETED);
        validate("CampaignCreativeGroup.updateTarget", group);
        assertViolationsCount(1);

        group.setChannel(channel);
        group.setChannelTarget(ChannelTarget.NOT_SET);
        validate("CampaignCreativeGroup.updateTarget", group);
        assertViolationsCount(1);

        //validate channel
        group.setChannel(new BehavioralChannel());
        group.setChannelTarget(ChannelTarget.TARGETED);
        validate("CampaignCreativeGroup.updateTarget", group);
        assertViolationsCount(1);

        Channel channel2 = new BehavioralChannel();
        channel2.setId(channel.getId());
        group.setChannel(channel2);
        group.setChannelTarget(ChannelTarget.TARGETED);
        validate("CampaignCreativeGroup.updateTarget", group);
        assertViolationsCount(0);

        channel2.setId(123L);
        group.setChannel(channel2);
        group.setChannelTarget(ChannelTarget.TARGETED);
        validate("CampaignCreativeGroup.updateTarget", group);
        assertViolationsCount(1);
    }

    @Test
    public void testUpdateChannelTargetKeywordGroup() {
        CampaignCreativeGroup group = textCCGTF.create();
        group.setTgtType(TGTType.KEYWORD);
        textCCGTF.persist(group);

        BehavioralChannel channel = behavioralChannelTestFactory.createPersistent(group.getAccount());

        group.setChannel(channel);
        group.setChannelTarget(ChannelTarget.TARGETED);
        validate("CampaignCreativeGroup.updateTarget", group);
        assertViolationsCount(1);
    }

    @Test
    public void testSetChannelTargetWhenCreate() {
        AdvertiserAccount advertiserAccount = advertiserAccountTF.createPersistent();
        Campaign campaign = textCampaignTF.createPersistent(advertiserAccount);
        BehavioralChannel channel = behavioralChannelTestFactory.createPersistent(advertiserAccount);

        Operations<CampaignCreativeGroup> ops = new Operations<>();

        Operation<CampaignCreativeGroup> op1 = new Operation<>();
        CampaignCreativeGroup channelGroup = textCCGTF.create(campaign);
        channelGroup.setTgtType(TGTType.CHANNEL);
        op1.setEntity(channelGroup);
        op1.setOperationType(OperationType.CREATE);
        ops.getOperations().add(op1);

        Operation<CampaignCreativeGroup> op2 = new Operation<>();
        CampaignCreativeGroup keywordGroup = textCCGTF.create(campaign);
        keywordGroup.setTgtType(TGTType.KEYWORD);
        op2.setEntity(keywordGroup);
        op2.setOperationType(OperationType.CREATE);
        ops.getOperations().add(op2);

        // don't set channel target
        validate("CampaignCreativeGroup.merge", ops);
        assertViolationsCount(0);

        // set channel target
        channelGroup.setChannel(channel);
        channelGroup.setChannelTarget(ChannelTarget.TARGETED);
        keywordGroup.setChannel(channel);
        keywordGroup.setChannelTarget(ChannelTarget.TARGETED);
        validate("CampaignCreativeGroup.merge", ops);
        assertViolationsCount(1);
        assertHasViolation("operations[1].campaignCreativeGroup.ccgType");
    }

    @Test
    public void testValidateUpdateList() {
        AdvertiserAccount advertiserAccount = advertiserAccountTF.createPersistent();
        Campaign campaign = textCampaignTF.createPersistent(advertiserAccount);
        CampaignCreativeGroup ccg = textCCGTF.createPersistent(campaign);

        CampaignCreativeGroup toUpdate = new CampaignCreativeGroup(ccg.getId());
        CcgRate ccgRate = new CcgRate();
        ccgRate.setCpc(BigDecimal.TEN);
        ccgRate.setRateType(RateType.CPC);
        toUpdate.setCcgRate(ccgRate);

        validate("CampaignCreativeGroup.update", Collections.singletonList(toUpdate));
        assertViolationsCount(0);

        toUpdate = new CampaignCreativeGroup(ccg.getId());
        ccgRate = new CcgRate();
        ccgRate.setCpc(null);
        ccgRate.setRateType(RateType.CPC);
        toUpdate.setCcgRate(ccgRate);

        validate("CampaignCreativeGroup.update", Collections.singletonList(toUpdate));
        assertViolationsCount(1);
        assertHasViolation("groups[0].ccgRate");
    }

    @Test
    public void testChangesDeliveryPacingRelatedFields() {
        AdvertiserAccount advertiserAccount = advertiserAccountTF.createPersistent();
        Campaign campaign = displayCampaignTF.create(advertiserAccount);
        campaign.setDateEnd(null);
        textCampaignTF.persist(campaign);

        CampaignCreativeGroup ccg = displayCCGTF.create(campaign);
        ccg.setLinkedToCampaignEndDateFlag(false);
        ccg.setDateEnd(ccg.getDateStart());
        ccg.setDeliveryPacing(DeliveryPacing.DYNAMIC);
        displayCCGTF.persist(ccg);

        
        // Test validation of Delivery Pacing on LinkedToCampaignEndDateFlag changed
        CampaignCreativeGroup toUpdate = new CampaignCreativeGroup(ccg.getId());
        ccg.setLinkedToCampaignEndDateFlag(true);

        validate("CampaignCreativeGroup.update", Collections.singletonList(toUpdate));
        assertViolationsCount(1);
        assertHasViolation("groups[0].deliveryPacing");


        // Test validation of Delivery Pacing on CampaignDateEnd changed
        campaign.setDateEnd(campaign.getDateStart());
        displayCampaignTF.update(campaign);
        ccg.setLinkedToCampaignEndDateFlag(true);
        displayCCGTF.update(ccg);

        commitChanges();
        clearContext();

        Campaign campaignToUpdate = new Campaign(campaign.getId());
        campaignToUpdate.setDateEnd(null);
        displayCampaignTF.update(campaignToUpdate);

        ccg = displayCCGTF.refresh(ccg);
        clearContext();

        validate("CampaignCreativeGroup.update", Collections.singletonList(ccg));
        assertViolationsCount(1);
        assertHasViolation("groups[0].deliveryPacing");
    }
}
