package com.foros.session.site;

import com.foros.AbstractValidationsTest;
import com.foros.model.ApproveStatus;
import com.foros.model.account.PublisherAccount;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.AccountType;
import com.foros.model.security.AdvExclusionsApprovalType;
import com.foros.model.security.AdvExclusionsType;
import com.foros.model.site.CategoryExclusionApproval;
import com.foros.model.site.Site;
import com.foros.model.site.SiteCategory;
import com.foros.model.site.SiteCreativeCategoryExclusion;
import com.foros.model.template.CreativeTemplate;
import com.foros.session.account.AccountServiceBean;
import com.foros.test.factory.CreativeCategoryTestFactory;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.CreativeTemplateTestFactory;
import com.foros.test.factory.PublisherAccountTestFactory;
import com.foros.test.factory.PublisherAccountTypeTestFactory;
import com.foros.test.factory.SiteTestFactory;
import com.foros.util.RandomUtil;

import group.Db;
import group.Validation;

import java.io.File;
import java.util.LinkedHashSet;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class SiteValidationsTest extends AbstractValidationsTest {

    @Autowired
    public SiteTestFactory siteTF;

    @Autowired
    private CreativeTemplateTestFactory creativeTemplateTF;

    @Autowired
    private CreativeSizeTestFactory creativeSizeTF;

    @Autowired
    private AccountServiceBean accountService;

    @Autowired
    private PublisherAccountTypeTestFactory publisherTestFactory;

    @Autowired
    private PublisherAccountTestFactory publisherAccountTF;

    @Autowired
    private CreativeCategoryTestFactory creativeCategoryTF;

    @Test
    public void testValidateCreate() throws Exception {
        Site site = siteTF.create();
        validate("Site.create", site);
        assertEquals(0, violations.size());

        site.setName(null);
        site.setSiteUrl(RandomUtil.getRandomString());
        site.setNotes(RandomUtil.getRandomString(2001));
        validate("Site.create", site);
        assertEquals(3, violations.size());
        assertHasViolation("name");
        assertHasViolation("siteUrl");
        assertHasViolation("notes");

    }

    @Test
    public void testValidateUpdate() throws Exception {
        Site site = siteTF.createPersistent();
        getEntityManager().flush();
        SiteCategory siteCategory = site.getSiteCategory();
        getEntityManager().remove(siteCategory);
        validate("Site.update", site);
        assertEquals(1, violations.size());
        assertHasViolation("siteCategory");

    }

    @Test
    public void testValidateBulkUpdate() throws Exception {
        Site site = siteTF.createPersistent();
        commitChanges();
        clearContext();

        site.getAccount().setName("changedName");
        validate("Site.createOrUpdate", site);
        assertEquals(1, violations.size());
        assertHasViolation("account.name");

        violations.clear();
        site.getAccount().setId(null);
        site.getAccount().setName(null);
        validate("Site.createOrUpdate", site);
        assertEquals(2, violations.size());
        assertHasViolation("account.name");
        assertHasViolation("account.id");


    }

    @Test
    public void testSiteWithCategoryExclusions() throws Exception {
        CreativeCategory tag1 = getCreativeCategory(101);
        SiteCreativeCategoryExclusion categoryExclusion1 = new SiteCreativeCategoryExclusion();
        categoryExclusion1.setCreativeCategory(tag1);
        Site site = siteTF.builder()
            .enableExclusions()
            .getSite();
        site.setCategoryExclusions(new LinkedHashSet<SiteCreativeCategoryExclusion>());
        site.getCategoryExclusions().add(categoryExclusion1);
        validate("Site.create", site);
        assertHasViolation("selectedTags");
    }

    @Test
    public void testValidateAdvExclusionApprovalChanges() throws Exception {
        AccountType accountType = publisherTestFactory.create();
        CreativeTemplate template = creativeTemplateTF.createPersistent();
        CreativeSize size = creativeSizeTF.createPersistent();
        accountType.getCreativeSizes().add(size);
        accountType.getTemplates().add(template);
        accountType.setAdvExclusions(AdvExclusionsType.SITE_LEVEL);
        accountType.setAdvExclusionApproval(AdvExclusionsApprovalType.REJECTED);
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

        Site site = siteTF.create();
        site.setAccount(publisherAccount);

        CreativeCategory contentCC = creativeCategoryTF.createPersistent(CreativeCategoryType.CONTENT, ApproveStatus.APPROVED);

        site = siteTF.builder(site)
                .enableExclusions()
                .addCategoryExclusion(contentCC, CategoryExclusionApproval.APPROVAL)
                .getSite();

        validate("Site.create", site);
        assertEquals(1, violations.size());
    }

    private CreativeCategory getCreativeCategory(int length) {
        CreativeCategory tag = new CreativeCategory();
        tag.setType(CreativeCategoryType.TAG);
        tag.setQaStatus(ApproveStatus.HOLD.getLetter());
        tag.setDefaultName(RandomUtil.getRandomString(length));
        return tag;
    }

    @Test
    public void testFileTypeValidity() throws Exception {
        File file = new File(getClass().getResource("valid.csv").getFile());
        assertTrue(file.exists());
        validate("Site.fileUpload", file);
        assertHasNoViolation("fileToUpload");


        file = new File(getClass().getResource("invalid.png").getFile());
        assertTrue(file.exists());
        validate("Site.fileUpload", file);
        assertHasViolation("fileToUpload");
    }



}
