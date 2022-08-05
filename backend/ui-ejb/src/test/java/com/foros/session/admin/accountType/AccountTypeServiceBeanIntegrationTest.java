package com.foros.session.admin.accountType;

import static com.foros.model.security.AccountType.FINANCIAL_FIELDS_FLAG;
import static com.foros.model.security.AccountType.FREQ_CAPS_FLAG;
import static com.foros.model.security.AccountType.INPUT_RATES_AND_AMOUNTS_FLAG;
import static com.foros.model.security.AccountType.INVOICE_COMMISSION_FLAG;
import static com.foros.model.security.AccountType.INVOICING_FLAG;
import static com.foros.model.security.AccountType.OWNERSHIP;
import static com.foros.model.security.AccountType.PUBLISHER_INVENTORY_ESTIMATION_FLAG;
import static com.foros.model.security.AccountType.SITE_TARGETING_FLAG;
import static com.foros.model.security.AccountType.WDTAGS_FLAG;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.ApproveStatus;
import com.foros.model.FrequencyCap;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.model.admin.WalledGarden;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CampaignType;
import com.foros.model.campaign.RateType;
import com.foros.model.campaign.TGTType;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.AccountType;
import com.foros.model.security.AccountTypeCCGType;
import com.foros.model.security.AdvExclusionsApprovalType;
import com.foros.model.security.AdvExclusionsType;
import com.foros.model.site.CategoryExclusionApproval;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.model.template.CreativeTemplate;
import com.foros.security.AccountRole;
import com.foros.session.EntityTO;
import com.foros.session.campaign.CCGEntityTO;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.AdvertiserAccountTypeTestFactory;
import com.foros.test.factory.AgencyAccountTestFactory;
import com.foros.test.factory.AgencyAccountTypeTestFactory;
import com.foros.test.factory.CreativeCategoryTestFactory;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.CreativeTemplateTestFactory;
import com.foros.test.factory.PublisherAccountTestFactory;
import com.foros.test.factory.PublisherAccountTypeTestFactory;
import com.foros.test.factory.SiteTestFactory;
import com.foros.test.factory.TagsTestFactory;
import com.foros.test.factory.TextCCGTestFactory;
import com.foros.test.factory.TextCampaignTestFactory;
import com.foros.test.factory.WalledGardenTestFactory;

import group.Db;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class AccountTypeServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private AccountTypeService accountTypeService;

    @Autowired
    private AdvertiserAccountTypeTestFactory advertiserAccountTypeTF;

    @Autowired
    private PublisherAccountTypeTestFactory publisherAccountTypeTF;

    @Autowired
    private AgencyAccountTypeTestFactory agencyAccountTypeTF;

    @Autowired
    private AgencyAccountTestFactory agencyAccountTF;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    private TextCampaignTestFactory textCampaignTF;

    @Autowired
    private TextCCGTestFactory textCCGTF;

    @Autowired
    private WalledGardenTestFactory walledGardenTF;

    @Autowired
    private CreativeTemplateTestFactory creativeTemplateTF;

    @Autowired
    private CreativeSizeTestFactory creativeSizeTF;

    @Autowired
    private PublisherAccountTestFactory publisherAccountTF;

    @Autowired
    private SiteTestFactory siteTF;

    @Autowired
    private TagsTestFactory tagsTF;

    @Autowired
    private CreativeCategoryTestFactory creativeCategoryTF;

    @Test
    public void testCreateAndFindByRole() {
        AccountType at = advertiserAccountTypeTF.createPersistent();

        List<AccountType> accTypes = accountTypeService.findByRole(AccountRole.ADVERTISER.getName());
        assertTrue(accTypes.contains(at));
    }

    @Test
    public void testCreateWithFinancialFieldsFlag(){
        AccountType at = agencyAccountTypeTF.create();
        at.setFinancialFieldsFlag(true);
        accountTypeService.create(at);

        assertNotNull(at.getId());
        AccountType accountType = accountTypeService.findById(at.getId());
        assertEquals(FINANCIAL_FIELDS_FLAG, accountType.getFlags());
    }

    @Test
    public void testCreateWithInvoicingFlag(){
        AccountType at = advertiserAccountTypeTF.create();
        at.setInvoicingFlag(true);
        accountTypeService.create(at);

        assertNotNull(at.getId());
        AccountType accountType = accountTypeService.findById(at.getId());
        assertEquals(INVOICING_FLAG + SITE_TARGETING_FLAG, accountType.getFlags());
    }

    @Test
    public void testCreateWithInputRatesAndAmountsFlag(){
        AccountType at = agencyAccountTypeTF.create();
        at.setInputRatesAndAmountsFlag(true);
        accountTypeService.create(at);

        assertNotNull(at.getId());
        AccountType accountType = accountTypeService.findById(at.getId());
        assertEquals(INPUT_RATES_AND_AMOUNTS_FLAG, accountType.getFlags());
    }

    @Test
    public void testCreateWithInvoicingCommisionFlag(){
        AccountType at = agencyAccountTypeTF.create();
        at.setInvoiceCommissionFlag(true);
        accountTypeService.create(at);

        assertNotNull(at.getId());
        AccountType accountType = accountTypeService.findById(at.getId());
        assertEquals(INVOICE_COMMISSION_FLAG , accountType.getFlags());
    }

    @Test
    public void testCreateWithAllFlags(){
        AccountType at = agencyAccountTypeTF.create();
        at.setFinancialFieldsFlag(true);
        at.setInvoicingFlag(true);
        at.setInputRatesAndAmountsFlag(true);
        at.setInvoiceCommissionFlag(true);
        at.setOwnershipFlag(true);
        at.setSiteTargetingFlag(true);
        accountTypeService.create(at);

        assertNotNull(at.getId());
        AccountType accountType = accountTypeService.findById(at.getId());
        assertEquals(
            FINANCIAL_FIELDS_FLAG |
            INVOICING_FLAG |
            INPUT_RATES_AND_AMOUNTS_FLAG |
            SITE_TARGETING_FLAG |
            OWNERSHIP |
            INVOICE_COMMISSION_FLAG, accountType.getFlags());
    }

    @Test
    public void testCreateWithAllFlagsForAgency(){
        AccountType at = agencyAccountTypeTF.create();
        at.setFinancialFieldsFlag(true);
        at.setInvoicingFlag(true);
        at.setInputRatesAndAmountsFlag(true);
        at.setInvoiceCommissionFlag(true);
        at.setOwnershipFlag(true);
        at.setSiteTargetingFlag(true);
        accountTypeService.create(at);

        assertNotNull(at.getId());
        AccountType accountType = accountTypeService.findById(at.getId());
        assertEquals(
            FINANCIAL_FIELDS_FLAG |
            INVOICING_FLAG |
            INPUT_RATES_AND_AMOUNTS_FLAG |
            SITE_TARGETING_FLAG |
            OWNERSHIP |
            INVOICE_COMMISSION_FLAG, accountType.getFlags());
    }

    @Test
    public void testCreateCCGTypes() {
        AccountType at = advertiserAccountTypeTF.createPersistent();
        assertNotNull(at.getId());
        assertFalse(at.isCPMFlag(CCGType.DISPLAY));
        assertFalse(at.isCPAFlag(CCGType.DISPLAY));
        assertTrue(at.isCPCFlag(CCGType.DISPLAY));
        assertTrue(at.isAllowTextAdvertisingFlag());

        at = accountTypeService.findById(at.getId());
        entityManager.clear();
        assertNotNull(at);

        at.setCPMFlag(CCGType.DISPLAY, true);
        at.setCPCFlag(CCGType.DISPLAY, true);
        at.setAllowTextKeywordAdvertisingFlag(true);
        accountTypeService.update(at);

        at = accountTypeService.findById(at.getId());
        entityManager.clear();

        assertNotNull(at);
        assertTrue(at.isCPMFlag(CCGType.DISPLAY));
        assertFalse(at.isCPAFlag(CCGType.DISPLAY));
        assertTrue(at.isCPCFlag(CCGType.DISPLAY));
        assertTrue(at.isAllowTextAdvertisingFlag());
        assertEquals(4, at.getCcgTypes().size());

        at.setCPCFlag(CCGType.DISPLAY, false);
        accountTypeService.update(at);

        at = accountTypeService.findById(at.getId());
        entityManager.clear();

        assertNotNull(at);
        assertTrue(at.isCPMFlag(CCGType.DISPLAY));
        assertFalse(at.isCPAFlag(CCGType.DISPLAY));
        assertFalse(at.isCPCFlag(CCGType.DISPLAY));
        assertTrue(at.isAllowTextAdvertisingFlag());
        assertEquals(3, at.getCcgTypes().size());
    }

    /* @Test todo: OUI-21610 */
    public void update() {
        AccountType at = advertiserAccountTypeTF.createPersistent(new CreativeSize[] { new CreativeSize(1L), new CreativeSize(2L) },
                new CreativeTemplate[] { new CreativeTemplate(1L), new CreativeTemplate(2L) });

        AccountType atFound = accountTypeService.findById(at.getId());
        assertEquals(at, atFound);

        CreativeSize sz = new CreativeSize(3L);
        Set<CreativeSize> sizes = atFound.getCreativeSizes();
        int len = sizes.size();
        atFound.getCreativeSizes().add(sz);
        atFound.setName(advertiserAccountTypeTF.getTestEntityRandomName());

        accountTypeService.update(atFound);
        AccountType atUpdated = accountTypeService.findById(at.getId());
        assertEquals(atFound, atUpdated);
        assertEquals(atUpdated.getName(), "NewName");
        assertEquals(atUpdated.getCreativeSizes().size(), len + 1);
        assertTrue(atUpdated.getCreativeSizes().contains(sz));
    }

    @Test
    public void testAccountListForSitesLinkedByFrequencyCapsFlag() {
        Site site = siteTF.createPersistent();
        AccountType accountType = site.getAccount().getAccountType();

        site.setNoAdsTimeout(2L);
        entityManager.merge(site);

        Site site2 = siteTF.createPersistent(accountType);
        FrequencyCap frequencyCap = new FrequencyCap();
        frequencyCap.setLifeCount(1000);
        site2.setFrequencyCap(frequencyCap);
        entityManager.merge(site2);

        accountType.setFreqCapsFlag(true);
        entityManager.merge(accountType);

        List<EntityTO> result = accountTypeService.getSitesLinkedByFrequencyCapsFlag(accountType.getId());

        assertEquals(2, result.size());
    }

    @Test
    public void testHasSitesLinkedByFrequencyCapsFlag() {
        Site site = siteTF.createPersistent();
        site.setNoAdsTimeout(2L);
        entityManager.merge(site);

        AccountType accountType = site.getAccount().getAccountType();
        accountType.setFreqCapsFlag(true);
        entityManager.merge(accountType);

        AccountTypeDisabledFields result = accountTypeService.getAccountTypeChangesCheck(accountType);

        assertTrue(result.isFreqCapsFlagDisabled());
    }

    @Test
    public void testHasAccountLinkedByAccountType() {
        AccountType accountType = getEntityManager().find(AccountType.class, getAccountTypeIdForLinkedAccount());
        boolean result = accountTypeService.hasAccountLinkedByAccountType(accountType);
        assertTrue(result);
    }

    @Test
    public void testGetAccountLinkedByAccountType() {
       AccountType accountType = getEntityManager().find(AccountType.class, getAccountTypeIdForLinkedAccount());
       List<EntityTO> result = accountTypeService.getAccountLinkedByAccountType(accountType);
       assertTrue(result.size() > 0);
    }

    @Test
    public void testCheckPublisherAccountCanMoved() {
        AccountType publisherATFrom = publisherAccountTypeTF.createPersistent();
        AccountType publisherATTo = publisherAccountTypeTF.createPersistent();

        // make all publisher queries run
        publisherATFrom.setFlags(WDTAGS_FLAG
            | PUBLISHER_INVENTORY_ESTIMATION_FLAG
            | FREQ_CAPS_FLAG
        );

        publisherATFrom.setAdvExclusions(AdvExclusionsType.DISABLED);
        getEntityManager().merge(publisherATFrom);

        publisherATTo.setFlags(0x00000000L);
        getEntityManager().merge(publisherATTo);

        // It should work with new account types and account that has nothing
        assertTrue(accountTypeService.checkAccountCanMoved(new PublisherAccount(-1L), publisherATFrom, publisherATTo));
    }

    @Test
    public void testCheckAgencyAccountCanMoved() {
        AccountType agencyATFrom = agencyAccountTypeTF.createPersistent();
        AccountType agencyATTo = agencyAccountTypeTF.createPersistent();
        AgencyAccount agencyAccount = agencyAccountTF.createPersistent(agencyATFrom);

        agencyATFrom.setFlags(0x00000000L);
        getEntityManager().merge(agencyATFrom);

        agencyATTo.setFlags(FINANCIAL_FIELDS_FLAG);
        getEntityManager().merge(agencyATTo);

        assertFalse(accountTypeService.checkAccountCanMoved(agencyAccount, agencyATFrom, agencyATTo));
    }

    @Test
    public void testCheckWalledGardenAgencyAccountCanMoved() {
        WalledGarden wg1 = walledGardenTF.createPersistent();
        AgencyAccount agencyAccount = wg1.getAgency();
        AccountType agencyATFrom = agencyAccount.getAccountType();

        AccountType agencyATTo = agencyAccountTypeTF.createPersistent();
        assertFalse(agencyATTo.isCPMFlag(CCGType.DISPLAY));
        assertFalse(agencyATTo.isCPCFlag(CCGType.DISPLAY));
        assertTrue(agencyATTo.isCPAFlag(CCGType.DISPLAY));
        // accountType with only Display CPA flag set
        assertFalse(accountTypeService.checkAccountCanMoved(agencyAccount, agencyATFrom, agencyATTo));

        agencyATTo = accountTypeService.findById(agencyATTo.getId());
        entityManager.clear();

        agencyATTo.setCPMFlag(CCGType.DISPLAY, true);
        agencyATTo = updateAccountType(agencyATTo);

        assertTrue(agencyATTo.isCPMFlag(CCGType.DISPLAY));
        assertFalse(agencyATTo.isCPCFlag(CCGType.DISPLAY));
        assertTrue(agencyATTo.isCPAFlag(CCGType.DISPLAY));

        // accountType with Display CPA and CPM flag set
        assertFalse(accountTypeService.checkAccountCanMoved(agencyAccount, agencyATFrom, agencyATTo));

        agencyATTo.setAllowTextKeywordAdvertisingFlag(true);
        agencyATTo = updateAccountType(agencyATTo);

        assertTrue(agencyATTo.isAllowTextAdvertisingFlag());
        // accountType with Text ccg selected
        assertFalse(accountTypeService.checkAccountCanMoved(agencyAccount, agencyATFrom, agencyATTo));

        agencyATTo.setCPCFlag(CCGType.DISPLAY, false);
        agencyATTo.setCPAFlag(CCGType.DISPLAY, false);
        agencyATTo.setCPMFlag(CCGType.DISPLAY, true);
        agencyATTo.setAllowTextKeywordAdvertisingFlag(false);
        agencyATTo = updateAccountType(agencyATTo);

        assertTrue(agencyATTo.isCPMFlag(CCGType.DISPLAY));
        assertFalse(agencyATTo.isCPAFlag(CCGType.DISPLAY));
        assertFalse(agencyATTo.isCPCFlag(CCGType.DISPLAY));
        assertFalse(agencyATTo.isAllowTextAdvertisingFlag());

        // accountType with only Display CCG with CPM rate type selected
        assertTrue(accountTypeService.checkAccountCanMoved(agencyAccount, agencyATFrom, agencyATTo));

        agencyATTo.setCPCFlag(CCGType.DISPLAY, true);
        agencyATTo = updateAccountType(agencyATTo);

        assertTrue(agencyATTo.isCPMFlag(CCGType.DISPLAY));
        assertTrue(agencyATTo.isCPCFlag(CCGType.DISPLAY));
        assertFalse(agencyATTo.isCPAFlag(CCGType.DISPLAY));
        assertFalse(agencyATTo.isAllowTextAdvertisingFlag());

        // accountType with only Display CCG with CPM & CPC rate types selected
        assertTrue(accountTypeService.checkAccountCanMoved(agencyAccount, agencyATFrom, agencyATTo));

        agencyATTo.setCPMFlag(CCGType.DISPLAY, false);
        agencyATTo = updateAccountType(agencyATTo);

        assertTrue(agencyATTo.isCPCFlag(CCGType.DISPLAY));
        assertFalse(agencyATTo.isCPMFlag(CCGType.DISPLAY));
        assertFalse(agencyATTo.isCPAFlag(CCGType.DISPLAY));
        assertFalse(agencyATTo.isAllowTextAdvertisingFlag());

        // accountType with only Display CCG with CPC rate type selected
        assertTrue(accountTypeService.checkAccountCanMoved(agencyAccount, agencyATFrom, agencyATTo));
    }

    @Test
    public void testGetCCGListWithRateTypeForAccountType() {
        AccountType accountType = advertiserAccountTypeTF.createPersistent(CampaignType.TEXT);
        AdvertiserAccount account = advertiserAccountTF.createPersistent(accountType);
        Campaign campaign = textCampaignTF.createPersistent(account);

        int ccgCount = 3;

        RateType rateType = RateType.CPC;
        CCGType ccgType = CCGType.TEXT;
        TGTType tgtType = TGTType.KEYWORD;
        BigDecimal cpc = new BigDecimal(123);

        Set<Long> ccgIds = new HashSet<Long>(ccgCount);
        for (int i = 0; i < ccgCount; i++) {
            CampaignCreativeGroup ccg = textCCGTF.create(campaign);
            ccg.setCcgType(ccgType);
            ccg.setTgtType(tgtType);
            ccg.setCcgRate(textCCGTF.createCcgRate(ccg, rateType, cpc));
            ccg.setDateStart(campaign.getDateStart());
            textCCGTF.persist(ccg);
            ccgIds.add(ccg.getId());
        }

        List<CCGEntityTO> foundCCGs = accountTypeService.getCCGRateTypeListLinkedToAccountType(accountType, rateType, ccgType, tgtType);
        Set<Long> foundCCGIds = new HashSet<Long>(ccgCount);
        for (CCGEntityTO to : foundCCGs) {
            foundCCGIds.add(to.getId());
        }
        assertTrue(foundCCGIds.equals(ccgIds));

        RateType anotherRateType = RateType.CPA;
        foundCCGs = accountTypeService.getCCGRateTypeListLinkedToAccountType(accountType, anotherRateType, ccgType, tgtType);
        foundCCGIds.clear();
        for (CCGEntityTO to : foundCCGs) {
            foundCCGIds.add(to.getId());
        }
        assertFalse(foundCCGIds.equals(ccgIds));
    }

    @Test
    public void testGetAllowedKeywordRateTypes() {
        AccountType accountType = advertiserAccountTypeTF.create();

        AccountTypeCCGType displayCPA = new AccountTypeCCGType(accountType, CCGType.DISPLAY, TGTType.CHANNEL, RateType.CPA);
        AccountTypeCCGType displayCPM = new AccountTypeCCGType(accountType, CCGType.DISPLAY, TGTType.CHANNEL, RateType.CPM);
        AccountTypeCCGType displayCPC = new AccountTypeCCGType(accountType, CCGType.DISPLAY, TGTType.CHANNEL, RateType.CPC);

        AccountTypeCCGType textChannelCPA = new AccountTypeCCGType(accountType, CCGType.TEXT, TGTType.CHANNEL, RateType.CPA);
        AccountTypeCCGType textChannelCPM = new AccountTypeCCGType(accountType, CCGType.TEXT, TGTType.CHANNEL, RateType.CPM);
        AccountTypeCCGType textChannelCPC = new AccountTypeCCGType(accountType, CCGType.TEXT, TGTType.CHANNEL, RateType.CPC);

        AccountTypeCCGType textKeywordCPC = new AccountTypeCCGType(accountType, CCGType.TEXT, TGTType.KEYWORD, RateType.CPC);

        testGetAllowedKeywordRateTypes(accountType, CCGType.DISPLAY, Arrays.asList(displayCPA));
        testGetAllowedKeywordRateTypes(accountType, CCGType.DISPLAY, Arrays.asList(displayCPM));
        testGetAllowedKeywordRateTypes(accountType, CCGType.DISPLAY, Arrays.asList(displayCPC));

        testGetAllowedKeywordRateTypes(accountType, CCGType.TEXT, Arrays.asList(textChannelCPA));
        testGetAllowedKeywordRateTypes(accountType, CCGType.TEXT, Arrays.asList(textChannelCPM));
        testGetAllowedKeywordRateTypes(accountType, CCGType.TEXT, Arrays.asList(textChannelCPC));
        testGetAllowedKeywordRateTypes(accountType, CCGType.TEXT, Arrays.asList(textKeywordCPC));

        List<AccountTypeCCGType> allDisplayTypes = Arrays.asList(displayCPA, displayCPM, displayCPC);
        testGetAllowedKeywordRateTypes(accountType, CCGType.DISPLAY, allDisplayTypes);

        List<AccountTypeCCGType> allTextTypes = Arrays.asList(textChannelCPA, textChannelCPM, textChannelCPC, textKeywordCPC);
        testGetAllowedKeywordRateTypes(accountType, CCGType.TEXT, allTextTypes);

        List<AccountTypeCCGType> allTextTypes2 = Arrays.asList(textChannelCPA, textChannelCPM, textKeywordCPC);
        testGetAllowedKeywordRateTypes(accountType, CCGType.TEXT, allTextTypes2);

        testGetAllowedKeywordRateTypes(accountType, CCGType.TEXT, Arrays.asList(textChannelCPA, textKeywordCPC));
    }

    private void testGetAllowedKeywordRateTypes(AccountType accountType, CCGType ccgType, List<AccountTypeCCGType> ccgTypes) {
        accountType.setCcgTypes(new HashSet<>(ccgTypes));
        Set<RateType> rateTypes = new HashSet<>();
        for (AccountTypeCCGType accountTypeCCGType : ccgTypes) {
            rateTypes.add(accountTypeCCGType.getRateType());
        }

        List<RateType> allowedRateTypes = accountType.getAllowedRateTypes(ccgType);
        assertEquals(rateTypes.size(), allowedRateTypes.size());

        for (RateType rateType : allowedRateTypes) {
            assertTrue(rateTypes.contains(rateType));
        }
    }

    @Test
    public void testHasSiteLinkedByAdvExclusionApproval() {
        AccountType accountType = publisherAccountTypeTF.create();
        CreativeTemplate template = creativeTemplateTF.createPersistent();
        CreativeSize size = creativeSizeTF.createPersistent();
        accountType.getCreativeSizes().add(size);
        accountType.getTemplates().add(template);
        accountType.setAdvExclusions(AdvExclusionsType.SITE_LEVEL);
        accountType.setAdvExclusionApproval(AdvExclusionsApprovalType.ACCEPTED);
        accountType.setShowBrowserPassbackTag(true);
        accountType.setShowIframeTag(true);
        publisherAccountTypeTF.persist(accountType);
        entityManager.flush();
        entityManager.refresh(accountType);

        PublisherAccount publisherAccount = publisherAccountTF.create();
        publisherAccount.setAccountType(accountType);
        publisherAccountTF.persist(publisherAccount);
        entityManager.flush();
        entityManager.refresh(publisherAccount);

        Site site = siteTF.create();
        site.setAccount(publisherAccount);

        CreativeCategory contentCC = creativeCategoryTF.createPersistent(CreativeCategoryType.CONTENT, ApproveStatus.APPROVED);

        site = siteTF.builder(site)
                .enableExclusions()
                .addCategoryExclusion(contentCC, CategoryExclusionApproval.APPROVAL)
                .getSite();
        siteTF.persist(site);
        entityManager.flush();
        entityManager.refresh(site);

        boolean result = accountTypeService.hasSitesLinkedByExclusionApproval(accountType);
        assertTrue(result);
    }

    private Long getAccountTypeIdForLinkedAccount(){
        return jdbcTemplate.queryForLong(" select ACCOUNT_TYPE_ID  from ACCOUNT a" +
                " where a.ACCOUNT_TYPE_ID is not null limit 1");
    }

    private AccountType updateAccountType(AccountType at) {
        accountTypeService.update(at);
        at = accountTypeService.findById(at.getId());
        entityManager.clear();
        return at;
    }


    @Test
    public void testGetTagsLinkedByInventoryEstimationFlag() {
        Site site = siteTF.createPersistent();

        Tag tag1 = tagsTF.create(site);
        tag1.setInventoryEstimationFlag(true);
        tagsTF.persist(tag1);

        Tag tag2 = tagsTF.create(site);
        tag2.setInventoryEstimationFlag(false);
        tagsTF.persist(tag2);

        Long accountTypeId = site.getAccount().getAccountType().getId();
        List<EntityTO> res = accountTypeService.getTagsLinkedByInventoryEstimationFlag(accountTypeId);
        assertEquals(1, res.size());
        assertEquals(tag1.getId(), res.get(0).getId());
    }
}
