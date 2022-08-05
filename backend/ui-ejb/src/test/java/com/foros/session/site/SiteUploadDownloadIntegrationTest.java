package com.foros.session.site;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.Status;
import com.foros.model.account.PublisherAccount;
import com.foros.model.creative.CreativeSize;
import com.foros.model.site.Site;
import com.foros.model.site.SiteRateType;
import com.foros.model.site.Tag;
import com.foros.model.site.TagPricing;
import com.foros.session.UploadContext;
import com.foros.session.creative.CreativeSizeService;
import com.foros.test.factory.PublisherAccountTestFactory;
import com.foros.test.factory.SiteTestFactory;
import com.foros.test.factory.TagsTestFactory;
import com.foros.util.StringUtil;

import group.Db;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class SiteUploadDownloadIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    public SiteUploadService siteUploadService;

    @Autowired
    public SiteService siteService;

    @Autowired
    public CreativeSizeService creativeSizeService;

    @Autowired
    public PublisherAccountTestFactory publisherAccountTF;

    @Autowired
    public SiteTestFactory siteTF;

    @Autowired
    public TagsTestFactory tagsTF;

    @Test
    public void testCreateSite() throws Exception {
        List<Site> sites = new ArrayList<Site>();
        Site site = siteTF.create();
        Tag tag = tagsTF.create(site, tagsTF.createTagPricing(null, new BigDecimal("20.11")),
                tagsTF.createTagPricing("US", new BigDecimal("0.11")));

        site.getTags().add(tag);
        sites.add(site);

        PublisherAccount siteAccount = site.getAccount();
        commitChanges();

        // validate sites
        SiteUploadValidationResultTO validationResult = siteUploadService.validateAll(sites);
        assertEquals("Unexpected number of validation errors", 0, validationResult.getLineWithErrors());

        // submit sites
        siteUploadService.createOrUpdateAll(validationResult.getId());
        entityManager.flush();

        site = fetchSite(siteAccount);
        tag = site.getTags().iterator().next();
        TagPricing[] pricings = tag.getTagPricings().toArray(new TagPricing[tag.getTagPricings().size()]);

        assertSite(site.getId(), "STATUS", String.class, "A");
        assertTag(tag.getId(), "SITE_ID", Long.class, site.getId());
        assertTagPricing(pricings[0].getId(), null, new BigDecimal("20.11"));
        assertTagPricing(pricings[1].getId(), "US", new BigDecimal("0.11"));
    }

    @Test
    public void testInvalidCreativeSize() throws Exception {
        Site site = siteTF.create();
        Tag tag = tagsTF.create(site, tagsTF.createTagPricing(null, new BigDecimal("20.11")));

        CreativeSize size = new CreativeSize();
        size.setProtocolName(siteTF.getTestEntityRandomName());
        tag.getSizes().add(size);
        site.getTags().add(tag);
        commitChanges();

        SiteUploadValidationResultTO validationResult = siteUploadService.validateAll(Arrays.asList(site));
        assertEquals("Expects validation errors", 1, validationResult.getLineWithErrors());

        UploadContext status = SiteUploadUtil.getUploadContext(tag);

        assertEquals("Error message mismatch", StringUtil.getLocalizedString("unauthorized.creative.size.usage"), status.getErrors().get(0).getMessage());

    }

    @Test
    public void testChangeSiteAccount() throws IOException {
        Site site = siteTF.createPersistent();
        PublisherAccount siteAccount = site.getAccount();
        commitChanges();

        // create new publisher
        PublisherAccount account2 = publisherAccountTF.createPersistent(siteAccount.getAccountType());

        commitChanges();

        site = siteTF.copy(fetchSite(siteAccount));
        site.setAccount(account2);
        SiteUploadValidationResultTO validationResult = siteUploadService.validateAll(Arrays.asList(site));
        assertEquals("Unexpected number of validation errors", 1, validationResult.getLineWithErrors());

        UploadContext status = SiteUploadUtil.getUploadContext(site);

        assertEquals("Error message mismatch", StringUtil.getLocalizedString("site.error.change.account", site.getName()), status.getErrors().get(0).getMessage());
    }

    @Test
    public void testInvalidSiteName1() throws IOException {
        // create case
        Site site = siteTF.create();

        site.setName("<>{}" + siteTF.getTestEntityRandomName());
        commitChanges();

        SiteUploadValidationResultTO validationResult = siteUploadService.validateAll(Arrays.asList(site));
        assertEquals("Unexpected number of validation errors", 1, validationResult.getLineWithErrors());
    }

    @Test
    public void testInvalidSiteName2() throws IOException {
        // create case
        Site site = siteTF.createPersistent();
        PublisherAccount account = site.getAccount();
        commitChanges();

        site = siteTF.copy(site);
        site.setName("<>{}" + siteTF.getTestEntityRandomName());

        SiteUploadValidationResultTO validationResult = siteUploadService.validateAll(Arrays.asList(site));
        assertEquals("Unexpected number of validation errors", 1, validationResult.getLineWithErrors());
    }

    @Test
    public void testInvalidSiteUrl() throws IOException {
        Site site = siteTF.create();
        site.setSiteUrl("invalidUrl");
        commitChanges();

        SiteUploadValidationResultTO validationResult = siteUploadService.validateAll(Arrays.asList(site));
        assertEquals("Unexpected number of validation errors", 1, validationResult.getLineWithErrors());
    }

    @Test
    public void testUpdateTagPricing() throws Exception {
        Site site = siteTF.createPersistent();
        TagPricing defaultPricing = tagsTF.createTagPricing(null, new BigDecimal("20.11"));
        Tag tag = tagsTF.createPersistent(site, defaultPricing);
        site.getTags().add(tag);
        siteTF.update(site);
        commitChanges();

        Site updatedSite = siteTF.copy(site);
        Tag updatedTag = tagsTF.copy(tag);
        TagPricing updatedPricing = tagsTF.copy(tag.getTagPricings().toArray(new TagPricing[1])[0]);

        updatedPricing.setTags(updatedTag);
        updatedPricing.getSiteRate().setRate(new BigDecimal("1.23"));
        updatedPricing.getSiteRate().setRateType(SiteRateType.CPM);
        updatedTag.setSite(updatedSite);
        updatedTag.getTagPricings().add(updatedPricing);
        updatedSite.getTags().add(updatedTag);

        commitChanges();

        // validate sites
        PublisherAccount siteAccount = site.getAccount();
        SiteUploadValidationResultTO validationResult = siteUploadService.validateAll(Arrays.asList(updatedSite));
        assertEquals("Unexpected number of validation errors", 0, validationResult.getLineWithErrors());

        // submit sites
        siteUploadService.createOrUpdateAll(validationResult.getId());
        commitChanges();

        assertTagPricing(updatedPricing.getId(), null, new BigDecimal("1.23"));
    }

    @Test
    public void testDeleteSite() throws Exception {
        Site site = siteTF.createPersistent();
        commitChanges();

        Site updatedSite = siteTF.copy(site);
        updatedSite.setStatus(Status.DELETED);

        // validate sites
        PublisherAccount siteAccount = site.getAccount();
        SiteUploadValidationResultTO validationResult = siteUploadService.validateAll(Arrays.asList(updatedSite));
        assertEquals("Unexpected number of validation errors", 0, validationResult.getLineWithErrors());

        // submit sites
        siteUploadService.createOrUpdateAll(validationResult.getId());
        commitChanges();

        assertEquals("Unexpected status", siteService.find(site.getId()).getStatus(), Status.DELETED);
    }

    @Test
    public void testChangeTagSite() throws Exception {
        Site site = siteTF.createPersistent();
        Tag tag = tagsTF.createPersistent(site);
        site.getTags().add(tag);
        siteTF.update(site);

        Site anotherSite = siteTF.copy(siteTF.createPersistent());
        Tag updateTag = tagsTF.copy(tag);

        anotherSite.getTags().add(updateTag);
        updateTag.setSite(anotherSite);
        commitChanges();

        // validate sites
        PublisherAccount siteAccount = site.getAccount();
        SiteUploadValidationResultTO validationResult = siteUploadService.validateAll(Arrays.asList(anotherSite));

        assertEquals("Unexpected number of validation errors", 1, validationResult.getLineWithErrors());
    }

    @Test
    public void testDuplicated(){
        Site site = siteTF.createPersistent();
        Site anotherSite = siteTF.create();

        anotherSite.setName(site.getName());
        anotherSite.setAccount(new PublisherAccount(site.getAccount().getId()));
        commitChanges();

        assertEquals(1, siteService.findDuplicated(Arrays.asList(site, anotherSite), anotherSite.getId()).size());
    }

    @Test
    public void testSiteRateHistory() throws Exception {
        Site site = siteTF.createPersistent();
        TagPricing defaultPricing = tagsTF.createTagPricing(null, new BigDecimal("20.11"));
        Tag tag = tagsTF.createPersistent(site, defaultPricing);
        site.getTags().add(tag);
        siteTF.update(site);
        commitChanges();

        Site updatedSite = siteTF.copy(site);
        Tag updatedTag = tagsTF.copy(tag);
        TagPricing updatedPricing = tagsTF.copy(tag.getTagPricings().toArray(new TagPricing[1])[0]);

        updatedPricing.setTags(updatedTag);
        updatedPricing.getSiteRate().setRate(new BigDecimal("1.23"));
        updatedPricing.getSiteRate().setRateType(SiteRateType.CPM);
        updatedTag.setSite(updatedSite);
        updatedTag.getTagPricings().add(updatedPricing);
        updatedSite.getTags().add(updatedTag);

        // validate sites
        SiteUploadValidationResultTO validationResult = siteUploadService.validateAll(Arrays.asList(updatedSite));
        assertEquals("Unexpected number of validation errors", 0, validationResult.getLineWithErrors());

        // submit sites
        siteUploadService.createOrUpdateAll(validationResult.getId());
        commitChanges();

        assertTagPricing(updatedPricing.getId(), null, new BigDecimal("1.23"));

        List result = getEntityManager().createQuery("select sr from SiteRate sr where sr.tagPricing.id=:id")
                .setParameter("id", updatedPricing.getId()).getResultList();
        assertEquals("Must have two site rates", 2, result.size());

    }

    private <T> void assertSite(Long id, String columnName, Class<T> clazz, T expected) {
        T actual = jdbcTemplate.queryForObject("select " + columnName + " from site where SITE_ID=?", clazz, id);
        assertEquals(columnName, expected, actual);
    }

    private <T> void assertTag(Long id, String columnName, Class<T> clazz, T expected) {
        T actual = jdbcTemplate.queryForObject("select " + columnName + " from TAGS where TAG_ID=?", clazz, id);
        assertEquals(columnName, expected, actual);
    }

    private void assertTagPricing(Long id, String countryCode, BigDecimal expected) {
        BigDecimal actual;
        if (countryCode == null) {
            actual = jdbcTemplate.queryForObject(
                    "select sr.RATE from TAGPRICING tp, SITERATE sr " +
                            "where tp.SITE_RATE_ID=sr.SITE_RATE_ID and tp.TAG_PRICING_ID=? and tp.COUNTRY_CODE is null", BigDecimal.class, id);
        } else {
            actual = jdbcTemplate.queryForObject(
                    "select sr.RATE from TAGPRICING tp, SITERATE sr " +
                            "where tp.SITE_RATE_ID=sr.SITE_RATE_ID and tp.TAG_PRICING_ID=? and tp.COUNTRY_CODE=?", BigDecimal.class, id, countryCode);
        }
        assertTrue("Site rate " + expected + " !=  " + actual, expected.subtract(actual).abs().doubleValue() < 0.000001);
    }


    private Site fetchSite(PublisherAccount siteAccount) {
        return getEntityManager().find(PublisherAccount.class, siteAccount.getId()).getSites().iterator().next();
    }
}
