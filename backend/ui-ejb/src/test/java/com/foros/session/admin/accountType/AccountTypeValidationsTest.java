package com.foros.session.admin.accountType;

import com.foros.AbstractValidationsTest;
import com.foros.model.ApproveStatus;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.AccountType;
import com.foros.model.security.AdvExclusionsApprovalType;
import com.foros.model.security.AdvExclusionsType;
import com.foros.model.site.CategoryExclusionApproval;
import com.foros.model.site.Site;
import com.foros.model.site.WDTag;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.time.TimeSpan;
import com.foros.model.time.TimeUnit;
import com.foros.session.account.AccountServiceBean;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.AdvertiserAccountTypeTestFactory;
import com.foros.test.factory.CreativeCategoryTestFactory;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.CreativeTemplateTestFactory;
import com.foros.test.factory.DeviceChannelTestFactory;
import com.foros.test.factory.DisplayCCGTestFactory;
import com.foros.test.factory.DisplayCampaignTestFactory;
import com.foros.test.factory.DisplayCreativeTestFactory;
import com.foros.test.factory.InternalAccountTypeTestFactory;
import com.foros.test.factory.PublisherAccountTestFactory;
import com.foros.test.factory.PublisherAccountTypeTestFactory;
import com.foros.test.factory.SiteTestFactory;
import com.foros.test.factory.WDTagTestFactory;
import com.foros.util.RandomUtil;

import group.Db;
import group.Validation;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class AccountTypeValidationsTest extends AbstractValidationsTest {
    @Autowired
    private InternalAccountTypeTestFactory internalFactory;

    @Autowired
    private AdvertiserAccountTypeTestFactory advertiserFactory;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    private CreativeTemplateTestFactory creativeTemplateTF;

    @Autowired
    private CreativeSizeTestFactory creativeSizeTF;

    @Autowired
    private AccountServiceBean accountService;

    @Autowired
    private DisplayCreativeTestFactory displayCreativeTF;

    @Autowired
    private PublisherAccountTypeTestFactory publisherTestFactory;

    @Autowired
    private PublisherAccountTestFactory publisherAccountTF;

    @Autowired
    private SiteTestFactory siteTestFactory;

    @Autowired
    private WDTagTestFactory wdTagTF;

    @Autowired
    private CreativeCategoryTestFactory creativeCategoryTF;

    @Autowired
    private DeviceChannelTestFactory deviceChannelTF;

    @Autowired
    private DisplayCampaignTestFactory displayCampaignTF;

    @Autowired
    private DisplayCCGTestFactory displayCCGTF;

    @Test
    public void testValidateCreate() throws Exception {
        AccountType accountType = internalFactory.create();
        validate("AccountType.create", accountType);
        assertEquals(0, violations.size());

        accountType.setAccountRole(null);
        accountType.setName(null);
        validate("AccountType.create", accountType);
        assertEquals(2, violations.size());
        assertHasViolation("name", "accountRole");
    }

    @Test
    public void testValidateIoManagement() throws Exception {
        AccountType accountType = advertiserFactory.create();
        validate("AccountType.create", accountType);
        assertEquals(0, violations.size());

        accountType.setIoManagement(null);
        validate("AccountType.create", accountType);
        assertEquals(1, violations.size());
        assertHasViolation("ioManagement");
    }

    @Test
    public void testValidateCreateWithoutSizesTemplates() throws Exception {
        AccountType accountType = advertiserFactory.create();
        accountType.getCreativeSizes().clear();
        accountType.getTemplates().clear();
        validate("AccountType.create", accountType);
        assertEquals(2, violations.size());
        assertHasViolation("AccountType.sizes.notAvailable", "AccountType.templates.notAvailable");

        CreativeTemplate template = creativeTemplateTF.createPersistent();
        CreativeSize size = creativeSizeTF.createPersistent();
        accountType.getCreativeSizes().add(size);
        accountType.getTemplates().add(template);
        validate("AccountType.create", accountType);
        assertEquals(0, violations.size());
    }

    @Test
    public void testValidateUpdateCreativeTemplates() throws Exception {
        AccountType accountType1 = advertiserFactory.create();
        CreativeTemplate template = creativeTemplateTF.createPersistent();
        CreativeTemplate template1 = creativeTemplateTF.createPersistent();
        CreativeSize size1 = creativeSizeTF.createPersistent();
        accountType1.getCreativeSizes().add(size1);
        accountType1.getTemplates().add(template);
        accountType1.getTemplates().add(template1);
        advertiserFactory.persist(accountType1);
        AdvertiserAccount advertiserAccount = advertiserAccountTF.create();
        advertiserAccount.setAccountType(accountType1);
        accountService.createExternalAccount(advertiserAccount);
        Creative creative = displayCreativeTF.createPersistent(advertiserAccount, template1, size1);
        creative.setAccount(advertiserAccount);
        AccountType accountTypeToUpdate = advertiserFactory.create();
        accountTypeToUpdate.getCreativeSizes().clear();
        accountTypeToUpdate.getTemplates().add(template);
        accountTypeToUpdate.setId(accountType1.getId());
        accountTypeToUpdate.setVersion(accountType1.getVersion());

        validate("AccountType.update", accountTypeToUpdate);
        assertEquals(1, violations.size());
        assertHasViolation("AccountType.sizes.notAvailable");
    }

    @Test
    public void testValidateUpdateDiscoverTemplates() throws Exception {
        AccountType accountType1 = publisherTestFactory.create();
        DiscoverTemplate template = createDiscoverTemplate();
        DiscoverTemplate template1 = createDiscoverTemplate();
        CreativeSize size1 = creativeSizeTF.createPersistent();
        accountType1.getCreativeSizes().add(size1);
        accountType1.getTemplates().add(template);
        accountType1.getTemplates().add(template1);
        accountType1.setWdTagsFlag(true);
        publisherTestFactory.persist(accountType1);
        PublisherAccount publisherAccount = publisherAccountTF.create();
        publisherAccount.setAccountType(accountType1);
        accountService.createExternalAccount(publisherAccount);
        WDTag wdTag = wdTagTF.create();
        wdTag.setTemplate(template1);
        wdTag.getSite().setAccount(publisherAccount);
        persist(wdTag);
        AccountType accountTypeToUpdate = publisherTestFactory.create();
        accountTypeToUpdate.getCreativeSizes().clear();
        accountTypeToUpdate.getTemplates().add(template);
        accountTypeToUpdate.setWdTagsFlag(true);
        accountTypeToUpdate.setId(accountType1.getId());
        accountTypeToUpdate.setVersion(accountType1.getVersion());
        accountTypeToUpdate.setShowIframeTag(false);
        accountTypeToUpdate.setShowBrowserPassbackTag(false);
        validate("AccountType.update", accountTypeToUpdate);
        assertEquals(1, violations.size());
        assertHasViolation("AccountType.sizes.notAvailable");
    }

    @Test
    public void testValidateFieldChanges() throws Exception {
        AccountType accountType = advertiserFactory.create();
        CreativeTemplate template = creativeTemplateTF.createPersistent();
        CreativeSize size = creativeSizeTF.createPersistent();
        accountType.getCreativeSizes().add(size);
        accountType.getTemplates().add(template);
        accountType.setSiteTargetingFlag(true);
        advertiserFactory.persist(accountType);

        AdvertiserAccount account = advertiserAccountTF.createPersistent(accountType);
        CampaignCreativeGroup ccg = displayCCGTF.create(displayCampaignTF.createPersistent(account));
        ccg.setIncludeSpecificSitesFlag(true);
        displayCCGTF.persist(ccg);

        AccountType accountTypeToUpdate = advertiserFactory.create();
        accountTypeToUpdate.getCreativeSizes().add(size);
        accountTypeToUpdate.getTemplates().add(template);
        accountTypeToUpdate.setSiteTargetingFlag(false);
        accountTypeToUpdate.setId(accountType.getId());
        accountTypeToUpdate.setVersion(accountType.getVersion());
        validate("AccountType.update", accountTypeToUpdate);
        assertEquals(1, violations.size());
    }

    @Test
    public void testValidateChannelCheckIntervals() {
        AccountType accountType = internalFactory.create();

        accountType.setChannelCheck(true);

        TimeSpan ts1 = new TimeSpan(1L, TimeUnit.SECOND);
        TimeSpan ts2 = new TimeSpan(1L, TimeUnit.HOUR);
        TimeSpan ts3 = new TimeSpan(1L, TimeUnit.DAY);
        TimeSpan tooBig = new TimeSpan(50L, TimeUnit.DAY);

        accountType.setChannelFirstCheck(ts1);
        accountType.setChannelSecondCheck(ts2);
        accountType.setChannelThirdCheck(ts3);

        validate("AccountType.create", accountType);
        assertEquals(0, violations.size());

        accountType.setChannelThirdCheck(tooBig);

        validate("AccountType.create", accountType);
        assertEquals(1, violations.size());

        accountType.setChannelSecondCheck(ts3);
        accountType.setChannelThirdCheck(ts2);

        validate("AccountType.create", accountType);
        assertEquals(1, violations.size());
        assertHasViolation("channelThirdCheck");

        accountType.setChannelFirstCheck(ts2);
        accountType.setChannelSecondCheck(ts1);
        accountType.setChannelThirdCheck(ts3);

        validate("AccountType.create", accountType);
        assertEquals(1, violations.size());
        assertHasViolation("channelSecondCheck");

    }

    @Test
    public void testValidateCampaignCheckIntervals() {
        AccountType accountType = internalFactory.create();

        accountType.setCampaignCheck(true);

        TimeSpan ts1 = new TimeSpan(1L, TimeUnit.SECOND);
        TimeSpan ts2 = new TimeSpan(1L, TimeUnit.HOUR);
        TimeSpan ts3 = new TimeSpan(1L, TimeUnit.DAY);
        TimeSpan tooBig = new TimeSpan(50L, TimeUnit.DAY);

        accountType.setCampaignFirstCheck(ts1);
        accountType.setCampaignSecondCheck(ts2);
        accountType.setCampaignThirdCheck(ts3);

        validate("AccountType.create", accountType);
        assertEquals(0, violations.size());

        accountType.setCampaignThirdCheck(tooBig);

        validate("AccountType.create", accountType);
        assertEquals(1, violations.size());

        accountType.setCampaignSecondCheck(ts3);
        accountType.setCampaignThirdCheck(ts2);

        validate("AccountType.create", accountType);
        assertEquals(1, violations.size());
        assertHasViolation("campaignThirdCheck");

        accountType.setCampaignFirstCheck(ts2);
        accountType.setCampaignSecondCheck(ts1);
        accountType.setCampaignThirdCheck(ts3);

        validate("AccountType.create", accountType);
        assertEquals(1, violations.size());
        assertHasViolation("campaignSecondCheck");
    }

    @Test
    public void testValidateAdvExclusionApprovalChanges() throws Exception {
        AccountType accountType = publisherTestFactory.create();
        CreativeTemplate template = creativeTemplateTF.createPersistent();
        CreativeSize size = creativeSizeTF.createPersistent();
        accountType.getCreativeSizes().add(size);
        accountType.getTemplates().add(template);
        accountType.setAdvExclusions(AdvExclusionsType.SITE_LEVEL);
        accountType.setAdvExclusionApproval(AdvExclusionsApprovalType.ACCEPTED);
        accountType.setShowBrowserPassbackTag(true);
        accountType.setShowIframeTag(true);
        publisherTestFactory.persist(accountType);
        entityManager.flush();
        entityManager.refresh(accountType);

        PublisherAccount publisherAccount = publisherAccountTF.create();
        publisherAccount.setAccountType(accountType);
        accountService.createExternalAccount(publisherAccount);
        entityManager.flush();
        entityManager.refresh(publisherAccount);

        Site site = siteTestFactory.create();
        site.setAccount(publisherAccount);

        CreativeCategory contentCC = creativeCategoryTF.createPersistent(CreativeCategoryType.CONTENT, ApproveStatus.APPROVED);

        site = siteTestFactory.builder(site)
                .enableExclusions()
                .addCategoryExclusion(contentCC, CategoryExclusionApproval.APPROVAL)
                .getSite();
        siteTestFactory.persist(site);
        entityManager.flush();
        entityManager.refresh(site);

        AccountType accountTypeToUpdate = publisherTestFactory.create();
        accountTypeToUpdate.getCreativeSizes().add(size);
        accountTypeToUpdate.getTemplates().add(template);
        accountTypeToUpdate.setShowBrowserPassbackTag(true);
        accountTypeToUpdate.setShowIframeTag(true);
        accountTypeToUpdate.setAdvExclusions(AdvExclusionsType.SITE_LEVEL);
        accountTypeToUpdate.setAdvExclusionApproval(AdvExclusionsApprovalType.REJECTED);
        accountTypeToUpdate.setId(accountType.getId());
        accountTypeToUpdate.setVersion(accountType.getVersion());
        validate("AccountType.update", accountTypeToUpdate);
        assertEquals(1, violations.size());
    }

    private DiscoverTemplate createDiscoverTemplate() {
        DiscoverTemplate template =  new DiscoverTemplate();
        template.setDefaultName(RandomUtil.getRandomString(5));
        persist(template);
        return template;
    }
}
