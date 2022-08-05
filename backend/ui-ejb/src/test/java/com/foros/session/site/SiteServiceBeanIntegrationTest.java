package com.foros.session.site;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.ApproveStatus;
import com.foros.model.DisplayStatus;
import com.foros.model.FrequencyCap;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.PublisherAccount;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.AccountType;
import com.foros.model.security.User;
import com.foros.model.site.CategoryExclusionApproval;
import com.foros.model.site.Site;
import com.foros.model.site.SiteCreativeCategoryExclusion;
import com.foros.model.site.Tag;
import com.foros.model.site.TagPricing;
import com.foros.security.MockPrincipal;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.session.EntityTO;
import com.foros.session.bulk.Result;
import com.foros.session.site.SiteSelector.Builder;
import com.foros.test.factory.CountryTestFactory;
import com.foros.test.factory.CreativeCategoryTestFactory;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.PublisherAccountTestFactory;
import com.foros.test.factory.PublisherAccountTypeTestFactory;
import com.foros.test.factory.SiteTestFactory;
import com.foros.test.factory.TagsTestFactory;
import com.foros.test.factory.TestFactory;
import com.foros.util.EntityUtils;
import com.foros.util.StringUtil;

import group.Db;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class SiteServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {

    private static final int MAX_RESULTS = 65000;

    @Autowired
    private TagsService tagsService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private SiteTestFactory siteTF;

    @Autowired
    private TagsTestFactory tagsTF;

    @Autowired
    private CreativeCategoryTestFactory creativeCategoryTF;

    @Autowired
    private PublisherAccountTestFactory publisherAccountTF;

    @Autowired
    private CountryTestFactory countryTF;

    @Autowired
    private PublisherAccountTypeTestFactory publisherTypeTF;

    @Autowired
    private CreativeSizeTestFactory creativeSizeTF;

    @Test
    public void testCreate() throws Exception {
        Site site = siteTF.create();

        Long id = siteService.create(site);
        getEntityManager().flush();

        assertNotNull("ID wasn't set", site.getId());
        assertEquals(id, site.getId());

        getEntityManager().refresh(site);

        assertEquals("Status wasn't set", Status.ACTIVE, site.getStatus());
        assertTrue("Site wasn't added to the account", site.getAccount().getSites().contains(site));
    }

    @Test
    public void testCreateWithCategoryExclusions() throws Exception {
        CreativeCategory tagCC = creativeCategoryTF.createPersistent(CreativeCategoryType.TAG, ApproveStatus.HOLD);
        CreativeCategory visualCC = creativeCategoryTF.createPersistent(CreativeCategoryType.VISUAL, ApproveStatus.HOLD);

        Site site = siteTF.builder()
            .enableExclusions()
            .addCategoryExclusion(tagCC, CategoryExclusionApproval.REJECT)
            .addCategoryExclusion(visualCC, CategoryExclusionApproval.REJECT)
            .getSite();

        siteService.create(site);
        entityManager.flush();
        entityManager.refresh(site);

        Set<SiteCreativeCategoryExclusion> exclusions = site.getCategoryExclusions();
        assertEquals(2, exclusions.size());
        for (SiteCreativeCategoryExclusion exclusion : exclusions) {
            assertNotNull(exclusion.getId());
            assertEquals((Object) site.getId(), exclusion.getId().getSiteId());
            assertEquals((Object) exclusion.getCreativeCategory().getId(), exclusion.getId().getCreativeCategoryId());
        }
    }

    @Test
    public void testUpdateWithCategoryExclusions() throws Exception {
        CreativeCategory tagCC = creativeCategoryTF.createPersistent(CreativeCategoryType.TAG, ApproveStatus.HOLD);
        CreativeCategory visualCC = creativeCategoryTF.createPersistent(CreativeCategoryType.VISUAL, ApproveStatus.HOLD);

        Site site = siteTF.builder()
            .enableExclusions()
            .addCategoryExclusion(tagCC, CategoryExclusionApproval.REJECT)
            .addCategoryExclusion(visualCC, CategoryExclusionApproval.REJECT)
            .persist()
            .getSite();

        CreativeCategory newCC = creativeCategoryTF.createPersistent(CreativeCategoryType.VISUAL, ApproveStatus.HOLD);

        siteTF.builder(site).addCategoryExclusion(newCC, CategoryExclusionApproval.REJECT);

        siteService.update(site);
        entityManager.flush();

        siteTF.builder(site)
            .refresh()
            .loadLazy();

        assertEquals(3, site.getCategoryExclusions().size());

        site.getCategoryExclusions().remove(site.getCategoryExclusions().iterator().next());

        getEntityManager().clear();

        siteService.update(site);
        entityManager.flush();
        assertEquals(2, site.getCategoryExclusions().size());
    }

    @Test
    public void testGetByAccount() throws Exception {
        PublisherAccount publisherAccount = createPublisherAccount();
        ApplicationPrincipal publisherUserPrincipal = loadPrincipal(publisherAccount.getUsers().iterator().next());

        Site site = siteTF.createPersistent(publisherAccount);
        Site site1 = siteTF.createPersistent(publisherAccount);
        populateTags(site1);

        // Internal user with all Tags(inclusive of Deleted)
        currentUserRule.setPrincipal(DEFAULT_ADMIN_PRINCIPAL);
        setDeletedObjectsVisible(true);
        Collection<Site> allSites = findSitesByAccountId(true, site.getAccount().getId());
        assertEquals("Site must be the same", true, allSites.contains(site));
        assertEquals("Sites must be the same", 2, allSites.size());
        getEntityManager().clear();

        // External user with Active Tags(exclusion of all the sites which dont have Active tags)
        currentUserRule.setPrincipal(publisherUserPrincipal);
        setDeletedObjectsVisible(false);
        allSites = findSitesByAccountId(false, site.getAccount().getId());
        assertEquals("Site with existing tags", false, allSites.contains(site));
        assertEquals("Sites must be the same", 1, allSites.size());
        getEntityManager().clear();
        // For External User all tags should be Active
        checkActiveTagsForSites(allSites);

        // Delete Site
        siteTF.delete(site.getId());
        // Delete tag
        tagsService.delete(updateSiteWithTagPricing(site1).getId());
        entityManager.flush();

        getEntityManager().clear();
        // Internal user test for inclusive of deleted sites
        currentUserRule.setPrincipal(DEFAULT_ADMIN_PRINCIPAL);
        setDeletedObjectsVisible(true);
        allSites = findSitesByAccountId(true, site.getAccount().getId());
        assertEquals("Site must be the same", true, allSites.contains(site));
        assertEquals("Sites must be the same", 2, allSites.size());
        getEntityManager().clear();

        // External user test for exclusion of deleted sites
        currentUserRule.setPrincipal(publisherUserPrincipal);
        setDeletedObjectsVisible(false);
        allSites = findSitesByAccountId(false, site.getAccount().getId());
        assertEquals("Site with existing tags", false, allSites.contains(site));
        assertEquals("Sites must be the same", 1, allSites.size());
        // For External User all tags should be Active
        checkActiveTagsForSites(allSites);

        // delete site with tags
        siteTF.delete(site1.getId());
        getEntityManager().clear();
        // Internal user test for inclusive of deleted sites
        currentUserRule.setPrincipal(DEFAULT_ADMIN_PRINCIPAL);
        allSites = findSitesByAccountId(true, site.getAccount().getId());
        assertEquals("Site must be the same", true, allSites.contains(site));
        assertEquals("Sites must be the same", 2, allSites.size());
        getEntityManager().clear();

        // External user test for exclusion of deleted sites
        currentUserRule.setPrincipal(publisherUserPrincipal);
        allSites = findSitesByAccountId(false, site.getAccount().getId());
        assertEquals("Site with existing tags", false, allSites.contains(site1));
        assertEquals("Sites must be the same", 0, allSites.size());
    }

    @Test
    public void testViewSiteFetched() throws Exception {
        PublisherAccount publisherAccount = createPublisherAccount();
        ApplicationPrincipal publisherUserPrincipal = loadPrincipal(publisherAccount.getUsers().iterator().next());

        Site site = siteTF.createPersistent(publisherAccount);
        populateTags(site);

        // Internal user with all sites
        currentUserRule.setPrincipal(DEFAULT_ADMIN_PRINCIPAL);
        Site siteEntity = siteService.viewSiteFetched(site.getId());
        assertEquals("Site must be the same", siteEntity.getId(), site.getId());
        getEntityManager().clear();

        // External user with Active Tags(exclusion of all the sites which dont have Active tags)
        currentUserRule.setPrincipal(publisherUserPrincipal);
        siteEntity = siteService.viewSiteFetched(site.getId());
        assertEquals("Site must be the same", siteEntity.getId(), site.getId());
        //All tags are Active for External User
        checkActiveTags(siteEntity.getTags());
        // Delete tag
        tagsService.delete(updateSiteWithTagPricing(site).getId());

        getEntityManager().clear();
        // Internal User with all inclusive of site with deleted tags
        currentUserRule.setPrincipal(DEFAULT_ADMIN_PRINCIPAL);
        siteEntity = siteService.viewSiteFetched(site.getId());
        assertEquals("Site must be the publisherAccount", siteEntity.getId(), site.getId());

        getEntityManager().clear();
        // External User with all Active tags
        currentUserRule.setPrincipal(publisherUserPrincipal);
        siteEntity = siteService.viewSiteFetched(site.getId());
        assertEquals("Site must be the same", siteEntity.getId(), site.getId());
        // All tags are Active for External User
        checkActiveTags(siteEntity.getTags());

        // Internal User with Deleted Sites
        siteTF.delete(site.getId());
        getEntityManager().clear();
        currentUserRule.setPrincipal(DEFAULT_ADMIN_PRINCIPAL);
        setDeletedObjectsVisible(true);
        siteEntity = siteService.viewSiteFetched(site.getId());
        assertEquals("Site must be the same", siteEntity.getId(), site.getId());
    }

    @Test
    public void testUsedAccounts() {
        setDeletedObjectsVisible(true);
        PublisherAccount publisher = publisherAccountTF.createPersistent();
        // Creating existing site
        Site existingSite = siteTF.createPersistent();
        existingSite.setAccount(publisher);
        siteTF.update(existingSite);

        getEntityManager().clear();
        publisherAccountTF.delete(publisher);
        commitChanges();

        List<EntityTO> accounts = siteService.findUsedAccounts();
        assertTrue(accountExists(accounts, publisher));

        getEntityManager().clear();
        setDeletedObjectsVisible(false);
        accounts = siteService.findUsedAccounts();
        assertFalse(accountExists(accounts, publisher));
    }

    private PublisherAccount createPublisherAccount() throws Exception {
        AccountType publisherType = publisherTypeTF.create();
        publisherTypeTF.persist(publisherType);
        return publisherAccountTF.createPersistent(publisherType);
    }

    private Site populateTags(Site site) throws Exception {
        CreativeSize size = creativeSizeTF.createPersistent();
        LinkedHashSet<CreativeSize> sizes = new LinkedHashSet<>();
        sizes.add(size);
        site.getAccount().getAccountType().getCreativeSizes().addAll(sizes);
        updateSiteWithTagPricing(site);
        return site;
    }

    private Tag updateSiteWithTagPricing(Site site) throws Exception {
        TagPricing pricing1 = tagsTF.createTagPricing(null, new BigDecimal(1));
        TagPricing pricing2 = tagsTF.createTagPricing("GB", new BigDecimal(2));
        return tagsTF.createPersistent(site, pricing1, pricing2);
    }

    private Collection<Site> findSitesByAccountId(boolean isInternal, Long id) throws Exception {
        return siteService.getByAccount(id, excludeNoTagsSite(isInternal));
    }

    private boolean excludeNoTagsSite(boolean isInternal) {
        if (isInternal) {
            return false;
        } else {
            return true;
        }
    }

    private void checkActiveTagsForSites(Collection<Site> allSites) {
        for (Site siteObj : allSites) {
            checkActiveTags(siteObj.getTags());
        }
    }

    private void checkActiveTags(Set<Tag> tags) {
        for (Tag tagObj : tags) {
            assertEquals("Tag must be Active", Status.ACTIVE, tagObj.getStatus());
        }
    }

    private ApplicationPrincipal loadPrincipal(User user) {
        return new MockPrincipal(user.getEmail(), user.getId(), user.getAccount().getId(), user.getRole().getId(),
            Long.parseLong(StringUtil.toString(user.getRole().getAccountRole().getId())));

    }

    private boolean accountExists(List<EntityTO> entities, Account account) {
        for (EntityTO entity : entities) {
            if (entity.getId().equals(account.getId())) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testDisplayStatus() throws Exception {
        Site site = siteTF.createPersistent();

        HashSet<DisplayStatus> displayStatuses = new HashSet<>(Site.getAvailableDisplayStatuses());
        for (Status status : EntityUtils.getAllowedStatuses(Site.class)) {
            for (ApproveStatus qa_status : EntityUtils.getAllowedQAStatuses(Site.class)) {
                site.setStatus(status);
                site.setQaStatus(qa_status);
                siteService.update(site);
                commitChanges();
                entityManager.refresh(site);
                displayStatuses.remove(site.getDisplayStatus());
                verifyBulkUpdateDisplayStatus(site);
            }
        }

        site.setStatus(Status.ACTIVE);
        site.setQaStatus(ApproveStatus.APPROVED);

        Tag tag = tagsTF.createPersistent(site);

        Set<Tag> tags = new LinkedHashSet<>();
        tags.add(tag);
        site.setTags(tags);

        siteService.update(site);
        commitChanges();
        entityManager.refresh(site);
        displayStatuses.remove(site.getDisplayStatus());
        verifyBulkUpdateDisplayStatus(site);

        ((Tag) site.getTags().toArray()[0]).setStatus(Status.DELETED);
        siteService.update(site);
        commitChanges();
        entityManager.refresh(site);

        verifyBulkUpdateDisplayStatus(site);

        assertEquals(displayStatuses.size(), 0);
    }

    @Test
    public void testExistingDisplayStatus() throws Exception {
        Site existingSite = siteTF.create();
        existingSite.setQaStatus(ApproveStatus.APPROVED);
        siteTF.persist(existingSite);

        Site newSite = new Site();
        DisplayStatus rightStatus = existingSite.getDisplayStatus();

        newSite.setStatus(existingSite.getStatus());
        newSite.setId(existingSite.getId());
        newSite.setVersion(existingSite.getVersion());
        newSite.setName(TestFactory.getTestEntityRandomName());
        newSite.setAccount(existingSite.getAccount());
        newSite.setSiteCategory(countryTF.createSiteCategoryPersistent(existingSite.getAccount().getCountry()));
        siteService.update(newSite);
        newSite = siteTF.builder(existingSite.getId()).getSite();
        assertEquals(rightStatus, existingSite.getDisplayStatus());
        assertTrue(newSite.getDisplayStatus() != null);
        assertTrue(rightStatus.equals(newSite.getDisplayStatus()));
    }

    @Test
    public void testStatuses() {
        Site site = siteTF.builder().persist().getSite();

        // delete
        siteService.delete(site.getId());
        entityManager.flush();
        entityManager.refresh(site);
        assertEquals(Status.DELETED, site.getStatus());

        // undelete
        siteService.undelete(site.getId());
        entityManager.flush();
        entityManager.refresh(site);
        assertEquals(Status.ACTIVE, site.getStatus());

        // decline
        siteService.decline(site.getId(), "test reason");
        entityManager.flush();
        entityManager.refresh(site);
        assertEquals(ApproveStatus.DECLINED, site.getQaStatus());

        // approve
        siteService.approve(site.getId());
        entityManager.flush();
        entityManager.refresh(site);
        assertEquals(ApproveStatus.APPROVED, site.getQaStatus());
    }

    @Test
    public void testCreateWithFreqCaps() {
        Site site;

        // with empty Frequency Cap
        site = siteTF.builder()
            .enableFrequencyCaps()
            .getSite();

        site.setFrequencyCap(new FrequencyCap());

        siteService.create(site);
        getEntityManager().flush();

        siteTF.builder(site).refresh();
        assertNull(site.getFrequencyCap());

        // with normal Frequency Cap
        site = siteTF.builder()
            .enableFrequencyCaps()
            .getSite();

        FrequencyCap cap = new FrequencyCap();
        cap.setLifeCount(10);
        site.setFrequencyCap(cap);

        siteService.create(site);
        entityManager.flush();

        siteTF.builder(site).refresh();
        assertNotNull(site.getFrequencyCap());
        assertEquals((Object) 10, site.getFrequencyCap().getLifeCount());
    }

    @Test
    public void testUpdateWithFreqCaps() {
        Site site;

        // with empty Frequency Cap
        site = siteTF.builder()
            .enableFrequencyCaps()
            .persist()
            .getSite();
        getEntityManager().flush();

        site.setFrequencyCap(new FrequencyCap());

        getEntityManager().clear();
        siteService.update(site);

        site = siteTF.builder(site.getId()).getSite();
        assertNull(site.getFrequencyCap());

        // with normal Frequency Cap
        FrequencyCap cap = new FrequencyCap();
        cap.setLifeCount(10);
        site.setFrequencyCap(cap);

        getEntityManager().clear();
        siteService.update(site);

        site = siteTF.builder(site.getId()).getSite();
        assertNotNull(site.getFrequencyCap());
        assertEquals((Object) 10, site.getFrequencyCap().getLifeCount());

        // with null Frequency Cap
        site.setFrequencyCap(null);

        getEntityManager().clear();
        siteService.update(site);

        site = siteTF.builder(site.getId()).getSite();
        assertNull(site.getFrequencyCap());
    }

    @Test
    public void testDuplicates() throws Exception {
        PublisherAccount publisher = publisherAccountTF.createPersistent();

        // Creating existing site
        Site existingSite = siteTF.createPersistent();
        existingSite.setAccount(publisher);
        siteTF.update(existingSite);

        List<Site> siteList = new ArrayList<>();

        Site duplicatedCreate = new Site(null, existingSite.getName());
        duplicatedCreate.setAccount(publisher);
        siteList.add(duplicatedCreate);

        assertEquals(1, siteService.findDuplicated(siteList, publisher.getId()).size());

        Site site = siteTF.createPersistent();
        site.setAccount(publisher);
        siteTF.update(site);

        Site duplicatedNameUpdate = new Site(site.getId(), existingSite.getName());
        duplicatedNameUpdate.setAccount(publisher);
        siteList.add(duplicatedNameUpdate);

        assertEquals(1, siteService.findDuplicated(siteList, publisher.getId()).size());

        Site duplicatedWithName = new Site(existingSite.getId(), site.getName());
        duplicatedWithName.setAccount(publisher);
        siteList.add(duplicatedWithName);

        assertEquals(2, siteService.findDuplicated(siteList, publisher.getId()).size());

        Site uniqueSite = siteTF.create();
        duplicatedWithName.setAccount(publisher);
        siteList.add(uniqueSite);

        assertEquals(2, siteService.findDuplicated(siteList, publisher.getId()).size());
    }

    @Test
    public void testFetchSitesForCsvDownload() throws Exception {
        PublisherAccount publisherAccount = createPublisherAccount();
        Site site1 = siteTF.createPersistent(publisherAccount);
        Site site2 = siteTF.createPersistent(publisherAccount);
        Long accountId = publisherAccount.getId();

        populateTags(site1);
        populateTags(site2);
        getEntityManager().clear();

        currentUserRule.setPrincipal(DEFAULT_ADMIN_PRINCIPAL);
        Collection<Site> allSites = siteService.fetchSitesForCsvDownload(Arrays.asList(accountId), MAX_RESULTS);
        // basic check
        assertEquals("Site must be the same", true, allSites.contains(site1));
        assertEquals("Sites count must be the same", 2, allSites.size());

        // delete site
        siteTF.delete(site2.getId());
        commitChanges();

        // checks when site is deleted
        allSites = siteService.fetchSitesForCsvDownload(Arrays.asList(accountId), MAX_RESULTS);
        assertEquals("Site must be the same", false, allSites.contains(site2));
        assertEquals("Sites must be the same", 1, allSites.size());

        // delete tag associated with a site
        Tag tag0 = site1.getTags().iterator().next();

        tagsService.delete(tag0.getId());
        commitChanges();

        allSites = siteService.fetchSitesForCsvDownload(Arrays.asList(accountId), MAX_RESULTS);
        Site site0 = allSites.iterator().next();
        assertEquals("Sites must be the same", 1, allSites.size());
        assertEquals("Tags must be the same", 0, site0.getTags().size());

        // check for deleted tag pricing scenario
        tagsService.undelete(tag0.getId());
        tag0 = tagsService.find(tag0.getId());
        tag0.getTagPricings().size();
        commitChanges();

        deleteTagPricing(tag0);
        getEntityManager().clear();

        allSites = siteService.fetchSitesForCsvDownload(Arrays.asList(accountId), MAX_RESULTS);
        site0 = allSites.iterator().next();
        tag0 = site0.getTags().iterator().next();
        assertEquals("Sites must be the same", 1, allSites.size());
        assertEquals("Tags must be the same", 1, site0.getTags().size());
        assertEquals("Tag pricings must be the same", 1, tag0.getTagPricings().size());
    }

    private void deleteTagPricing(Tag tag) {
        Tag copyTag = tagsTF.copy(tag);
        copyTag.setVersion(tag.getVersion());
        copyTag.setSite(tag.getSite());

        for (TagPricing tp : tag.getTagPricings()) {
            if (tp.getCountry() == null) {
                TagPricing copyTP = tagsTF.copy(tp);
                copyTP.setVersion(tp.getVersion());

                copyTag.getTagPricings().add(copyTP);
            }
        }

        tagsService.update(copyTag);
        getEntityManager().flush();
    }

    @Test
    public void testGetIndex() {
        PublisherAccount publisher = publisherAccountTF.createPersistent();
        siteTF.createPersistent(publisher);
        Site s2 = siteTF.createPersistent(publisher);
        siteTF.delete(s2.getId());

        ApplicationPrincipal external = loadPrincipal(publisher.getUsers().iterator().next());
        currentUserRule.setPrincipal(external);
        assertEquals(1, siteService.getIndex(publisher.getId()).size());

        currentUserRule.setPrincipal(DEFAULT_ADMIN_PRINCIPAL);
        setDeletedObjectsVisible(true);
        assertEquals(2, siteService.getIndex(publisher.getId()).size());

        setDeletedObjectsVisible(false);
        assertEquals(1, siteService.getIndex(publisher.getId()).size());
    }

    @Test
    public void testGetCategoryExclusions() {
        Site site = siteTF.createPersistent();
        List<SiteCreativeCategoryExclusion> exclusions = siteService.getCategoryExclusions(site.getId());
        assertNotNull(exclusions);
    }

    @Test
    public void testGet() {
        Site site = siteTF.createPersistent();

        SiteSelector.Builder builder = new Builder();
        builder.accountIds(Arrays.asList(site.getAccount().getId()));
        Result<Site> result = siteService.get(builder.build());
        assertTrue(!result.getEntities().isEmpty());

        builder.accountIds(Collections.EMPTY_LIST);
        builder.siteIds(Arrays.asList(site.getId()));
        result = siteService.get(builder.build());
        assertEquals(1, result.getEntities().size());
        assertEquals(result.getEntities().get(0), site);

        siteService.delete(site.getId());
        commitChanges();

        builder.siteStatuses(Arrays.asList(Status.ACTIVE));
        result = siteService.get(builder.build());
        assertTrue(result.getEntities().isEmpty());

        builder.siteStatuses(Arrays.asList(Status.DELETED));
        result = siteService.get(builder.build());
        assertEquals(1, result.getEntities().size());
        assertEquals(result.getEntities().get(0), site);


    }
}
