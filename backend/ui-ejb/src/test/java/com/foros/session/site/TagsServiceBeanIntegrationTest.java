package com.foros.session.site;

import static com.foros.config.ConfigParameters.DEFAULT_ADSERVING_DOMAIN;
import static com.foros.config.ConfigParameters.DEFAULT_STATIC_DOMAIN;
import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.config.MockConfigService;
import com.foros.model.Status;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.AccountType;
import com.foros.model.site.ContentCategory;
import com.foros.model.site.PassbackType;
import com.foros.model.site.Site;
import com.foros.model.site.SiteRate;
import com.foros.model.site.SiteRateType;
import com.foros.model.site.Tag;
import com.foros.model.site.TagOptGroupState;
import com.foros.model.site.TagOptGroupStatePK;
import com.foros.model.site.TagOptionValue;
import com.foros.model.site.TagOptionValuePK;
import com.foros.model.site.TagPricing;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionGroupType;
import com.foros.model.template.OptionType;
import com.foros.model.template.TemplateFileType;
import com.foros.session.admin.accountType.AccountTypeService;
import com.foros.session.fileman.FileSystem;
import com.foros.session.fileman.PathProviderService;
import com.foros.test.factory.CountryTestFactory;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.CreativeTemplateTestFactory;
import com.foros.test.factory.OptionGroupTestFactory;
import com.foros.test.factory.OptionTestFactory;
import com.foros.test.factory.PublisherAccountTypeTestFactory;
import com.foros.test.factory.SiteTestFactory;
import com.foros.test.factory.TagsTestFactory;
import com.foros.tx.TransactionSupportServiceMock;

import group.Db;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.OptimisticLockException;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

@Category(Db.class)
public class TagsServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private TagsService tagsService;

    @Autowired
    private SiteTestFactory siteTF;

    @Autowired
    private CreativeSizeTestFactory sizeTF;

    @Autowired
    private CreativeTemplateTestFactory templateTF;

    @Autowired
    private TagsTestFactory tagsTF;

    @Autowired
    private PathProviderService pathProviderService;

    @Autowired
    private OptionGroupTestFactory optionGroupTF;

    @Autowired
    private OptionTestFactory optionTF;

    @Autowired
    private MockConfigService configService;

    @Autowired
    private AccountTypeService accountTypeService;

    @Autowired
    private PublisherAccountTypeTestFactory accountTypeTF;

    @Autowired
    private CountryTestFactory countryTF;

    @Autowired
    private TransactionSupportServiceMock transactionSupportService;

    private String oldUserDir;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        URL thisUrl = getClass().getResource(getClass().getSimpleName() + ".class");
        oldUserDir = System.getProperty("user.dir");
        File thisDir = new File(thisUrl.toURI()).getParentFile();
        System.setProperty("user.dir", new File(thisDir, "xml").getAbsolutePath());
    }

    @After
    public void tearDown() throws Exception {
        System.setProperty("user.dir", oldUserDir);
    }

    @Test
    public void testCreate() {
        Site site = siteTF.createPersistent();
        Tag tag = tagsTF.create(site);

        Set<ContentCategory> contentCategories = new HashSet<>();
        contentCategories.add(countryTF.createContentCategoryPersistent(site.getAccount().getCountry()));
        contentCategories.add(countryTF.createContentCategoryPersistent(site.getAccount().getCountry()));
        tag.setContentCategories(contentCategories);

        TagPricing tp = new TagPricing();
        tp.setStatus(Status.ACTIVE);
        SiteRate sr = new SiteRate();
        sr.setRate(new BigDecimal(10));
        sr.setRateType(SiteRateType.CPM);
        tp.setSiteRate(sr);
        tag.getTagPricings().add(tp);

        Long id = tagsService.create(tag);
        assertNotNull(entityManager.find(Tag.class, id));

        Tag tagSaved = tagsService.viewFetched(id);
        Set<ContentCategory> savedContentCategories = tagSaved.getContentCategories();
        assertEquals(2, savedContentCategories.size());
    }

    @Test
    public void testCreateInventoryEstimation() {
        Site site = siteTF.createPersistent();
        Tag tag = tagsTF.create(site);
        tag.setPassbackType(null);
        tag.setInventoryEstimationFlag(true);
        Long id = tagsService.create(tag);
        getEntityManager().flush();
        getEntityManager().clear();
        tag = tagsService.find(id);
        assertSame(PassbackType.HTML_URL, tag.getPassbackType());
    }

    @Test(expected = NullPointerException.class)
    public void testCreateWithNullSite() {
        Tag tag = new Tag();
        tagsTF.populate(tag);
        tag.setSite(null);
        tagsService.create(tag);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateWithNullSize() {
        Site site = siteTF.createPersistent();
        Tag tag = tagsTF.create(site);
        tag.setSizes(null);
        tagsService.create(tag);
    }

    @Test
    public void testFindTags() {
        setDeletedObjectsVisible(false);
        Site site = siteTF.createPersistent();
        Tag tag = tagsTF.create(site);
        tagsService.create(tag);
        tagsService.delete(tag.getId());
        assertEquals(0, tagsService.findBySite(site.getId()).size());
        setDeletedObjectsVisible(true);
        assertEquals(1, tagsService.findBySite(site.getId()).size());
    }

    @Test
    public void testCreateWithPricings() {
        Site site = siteTF.createPersistent();
        TagPricing pricing1 = tagsTF.createTagPricing("GB", new BigDecimal(1));
        TagPricing pricing2 = tagsTF.createTagPricing(null, new BigDecimal(2));

        Tag tag = tagsTF.create(site, pricing1, pricing2);
        Long id = tagsService.create(tag);

        tag = tagsService.viewFetched(id);
        assertNotNull(tag);
        List<TagPricing> createdPricings = tag.getTagPricings();
        assertEquals(2, createdPricings.size());
    }

    @Test
    public void testCreateWithPassbackHtml() throws Exception {
        Site site = siteTF.createPersistent();
        Tag tag = tagsTF.create(site);
        String passbackHtml = "some test\ntext";
        tag.setPassbackHtml(passbackHtml);

        tagsService.create(tag);

        transactionSupportService.doBeforeCompletion();
        transactionSupportService.doCommit();

        FileSystem tagsFS = pathProviderService.getTags().createFileSystem();
        try(InputStream is = tagsFS.readFile(tag.getPassback())) {
            String fileContent = IOUtils.toString(is);
            assertEquals("Wrong tag file content", passbackHtml, fileContent);
        }
    }

    @Test
    public void testUpdate() {
        Site site = siteTF.createPersistent();
        Tag tag = tagsTF.createPersistent(site);

        tag.setName(tagsTF.getTestEntityRandomName());
        tag.setInventoryEstimationFlag(false);

        Set<ContentCategory> contentCategories = new HashSet<>();
        contentCategories.add(countryTF.createContentCategoryPersistent(site.getAccount().getCountry()));
        contentCategories.add(countryTF.createContentCategoryPersistent(site.getAccount().getCountry()));
        tag.setContentCategories(contentCategories);

        Tag savedTag = entityManager.find(Tag.class, tag.getId());

        assertEquals("Id", tag.getId(), savedTag.getId());
        assertEquals("Passback", tag.getPassback(), savedTag.getPassback());
        assertEquals("Name", tag.getName(), savedTag.getName());
        assertEquals("Flag", tag.getFlags(), savedTag.getFlags());

        assertEquals(savedTag.getContentCategories().size(), contentCategories.size());
        assertTrue(savedTag.getContentCategories().contains(contentCategories.iterator().next()));
    }

    @Test
    public void testDelete() {
        Site site = siteTF.createPersistent();
        Tag tag = tagsTF.createPersistent(site);
        Long id = tag.getId();
        tagsService.delete(id);
        tag = tagsService.view(id);
        assertEquals(tag.getStatus(), Status.DELETED);
    }

    @Test
    public void testUndelete() {
        Site site = siteTF.createPersistent();
        Tag tag = tagsTF.createPersistent(site);
        Long id = tag.getId();
        tagsService.delete(id);
        tagsService.undelete(id);
        tag = tagsService.view(id);
        assertEquals(tag.getStatus(), Status.ACTIVE);
    }

    @Test
    public void testAddOptionGroup() throws Exception {
        Tag tag = tagsTF.createPersistent();
        OptionGroup optionGroup = optionGroupTF.createPersistent(tag.getSizes().iterator().next(), OptionGroupType.Publisher);

        clearContext();

        TagOptGroupState tagOptGroupState = tag.getGroupStates().iterator().next();
        tagOptGroupState.setEnabled(!tagOptGroupState.getEnabled());
        tagOptGroupState = new TagOptGroupState();
        tagOptGroupState.setId(new TagOptGroupStatePK(optionGroup.getId(), tag.getId()));
        tagOptGroupState.setCollapsed(true);
        tagOptGroupState.setEnabled(true);
        tag.getGroupStates().add(tagOptGroupState);

        tagsService.updateOptions(tag);
        commitChanges();
        clearContext();

        tag = tagsService.find(tag.getId());
        assertTrue(tag.getGroupStates().contains(tagOptGroupState));
    }

    @Test
    public void testPreview() {
        configService.set(DEFAULT_STATIC_DOMAIN, "DEFAULT_STATIC_DOMAIN");
        configService.set(DEFAULT_ADSERVING_DOMAIN, "DEFAULT_ADSERVING_DOMAIN");

        Site site = siteTF.createPersistent();
        Tag tag = tagsTF.createPersistent(site);

        // tag
        String html = tagsService.generateTagHtml(tag);
        assertNotNull(html);
        assertFalse(html.contains("TAG_ID"));
        assertFalse(html.contains("SIZE"));
        assertFalse(html.contains("ADSERVER_URL"));

        // preview
        String htmlPreview = tagsService.generateTagPreviewHtml(tag);
        assertNotNull(htmlPreview);
        assertFalse(htmlPreview.contains("TAG_ID"));
        assertFalse(htmlPreview.contains("SIZE"));
        assertFalse(htmlPreview.contains("ADSERVER_URL"));

        // inventory
        String htmlInventoryEstimation = tagsService.generateInventoryEstimationTagHtml(tag);
        assertNotNull(htmlInventoryEstimation);
        assertFalse(htmlInventoryEstimation.contains("TAG_ID"));
        assertFalse(htmlInventoryEstimation.contains("SIZE"));
        assertFalse(htmlInventoryEstimation.contains("ADSERVER_URL"));

        // iframe
        String htmlIframe = tagsService.generateIframeTagHtml(tag);
        assertNotNull(htmlIframe);
        assertFalse(htmlIframe.contains("WIDTH"));
        assertFalse(htmlIframe.contains("HEIGHT"));
        assertFalse(htmlIframe.contains("ADSERVER_URL"));

        // browser passback
        // with passback HTML
        tag.setPassbackType(PassbackType.HTML_CODE);
        tag.setPassback("/test.html");
        String htmlBrowserPassback = tagsService.generateBrowserPassbackTagHtml(tag);
        assertNotNull(htmlBrowserPassback);
        assertTrue(htmlBrowserPassback.contains("DEFAULT_STATIC_DOMAIN"));
        assertTrue(htmlBrowserPassback.contains(tag.getPassback()));
        assertTrue(htmlBrowserPassback.contains("PSpt=\"html\""));

        // with passback URL
        tag.setPassbackType(PassbackType.HTML_URL);
        tag.setPassback("http://preview.url");
        String htmlBrowserPassback2 = tagsService.generateBrowserPassbackTagHtml(tag);
        assertNotNull(htmlBrowserPassback2);
        assertFalse(htmlBrowserPassback2.contains("DEFAULT_STATIC_DOMAIN"));
        assertTrue(htmlBrowserPassback2.contains(tag.getPassback()));
        assertTrue(htmlBrowserPassback2.contains("PSpt=\"html\""));
    }

    @Test(expected = OptimisticLockException.class)
    public void testTagUpdateForOptimisticLockException() {
        Site site = siteTF.createPersistent();
        TagPricing pricing = tagsTF.createTagPricing("GB", new BigDecimal(1));
        Tag tag = tagsTF.create(site, pricing);
        Long id = tagsService.create(tag);
        entityManager.flush();
        tag = tagsService.viewFetched(id);

        // update tag with adserving data change
        pricing = tag.getTagPricings().iterator().next();
        pricing.setSiteRate(new SiteRate());
        pricing.getSiteRate().setRate(new BigDecimal(2));
        pricing.getSiteRate().setRateType(SiteRateType.CPM);
        pricing.getSiteRate().setEffectiveDate(new Date(System.currentTimeMillis()));
        tagsService.update(tag);
        entityManager.flush();
        entityManager.clear();

        // now try to update same tag and expect OptimisticLockException
        tag.setVersion(new Timestamp(0));
        pricing = tag.getTagPricings().iterator().next();
        pricing.setSiteRate(new SiteRate());
        pricing.getSiteRate().setRate(new BigDecimal(3));
        pricing.getSiteRate().setRateType(SiteRateType.CPM);
        pricing.getSiteRate().setEffectiveDate(new Date(System.currentTimeMillis()));
        getEntityManager().clear();
        tagsService.update(tag);
        entityManager.flush();
    }

    @Test
    public void testCreateWithAdservingMode() {
        Site site = siteTF.createPersistent();
        AccountType accountType = site.getAccount().getAccountType();
        //Account has inventory estimation flag disable so Tags will be in adserving mode by default
        accountType.setPublisherInventoryEstimationFlag(false);
        accountTypeService.update(accountType);

        Tag tag = tagsTF.create(site);
        tag.setInventoryEstimationFlag(false);

        Long id = tagsService.create(tag);
        assertNotNull(entityManager.find(Tag.class, id));

        Tag savedTag = entityManager.find(Tag.class, tag.getId());

        assertEquals("Id", tag.getId(), savedTag.getId());
        assertEquals("Passback", tag.getPassback(), savedTag.getPassback());
    }

    @Test
    public void testChangeSize() {
        Tag tag = tagsTF.createPersistent();
        clearContext();

        tag = tagsService.find(tag.getId());
        assertTrue(tag.getOptions().size() == 0);

        Option option = tag.getSizes().iterator().next().getPublisherOptions().iterator().next();
        TagOptionValuePK optionValuePK = new TagOptionValuePK(tag.getId(), option.getId());
        TagOptionValue optionValue = new TagOptionValue(optionValuePK);
        optionValue.setOption(option);
        optionValue.setTag(tag);
        optionValue.setValue("test");
        tag.getOptions().add(optionValue);
        tagsService.updateOptions(tag);
        commitChanges();
        clearContext();

        tag = tagsService.find(tag.getId());
        assertTrue(tag.getOptions().iterator().next().getId().equals(optionValuePK));

        tag.getTagsExclusions().size();
        tag.getTagPricings().size();
        tag.getOptions().clear();
        tag.unregisterChange("options");
        clearContext();
        CreativeSize newSize = sizeTF.createPersistent();
        tag.setSizes(Collections.singleton(newSize));
        clearContext();
        tagsService.update(tag);
        commitChanges();
        clearContext();

        tag = tagsService.find(tag.getId());
        assertTrue(tag.getOptions().size() == 0);
    }

    @Test
    public void testOptionValuesCreated() {
        CreativeSize size = createEmptySize();
        OptionGroup sizeGroup = optionGroupTF.createPersistent(size, OptionGroupType.Publisher);
        optionTF.createPersistent(sizeGroup, OptionType.STRING);
        attachTemplateToSizeWithOptions(size, 2);
        attachTemplateToSizeWithOptions(size, 3);

        AccountType accountType = accountTypeTF.createPersistent(size, null);
        Site site = siteTF.createPersistent(accountType);
        Tag tag = tagsTF.create(site, size);
        Long tagId = tagsService.create(tag);
        commitChanges();

        // There should be no option values
        checkOptionValues(Collections.<Option>emptyList(), tagId);
    }

    @Test
    public void testOptionValuesOnSizeUpdate() {
        AccountType accountType = accountTypeTF.createPersistent();
        CreativeSize size1 = sizeTF.createPersistent();
        accountType.getCreativeSizes().add(size1);
        accountTypeTF.update(accountType);

        CreativeSize size2 = createEmptySize();
        OptionGroup sizeGroup = optionGroupTF.createPersistent(size2, OptionGroupType.Publisher);
        optionTF.createPersistent(sizeGroup, OptionType.STRING);
        optionTF.createPersistent(sizeGroup, OptionType.INTEGER);
        attachTemplateToSizeWithOptions(size2, 4);
        attachTemplateToSizeWithOptions(size2, 7);
        accountType.getCreativeSizes().add(size2);
        accountTypeTF.update(accountType);

        Site site = siteTF.createPersistent(accountType);
        Tag tag = tagsTF.create(site, size1);
        Long tagId = tagsService.create(tag);
        commitChanges();

        tag = tagsService.find(tagId);
        clearContext();
        tag.getSizes().add(size2);
        tagsService.update(tag);
        commitChanges();

        // There should be no option values
        checkOptionValues(Collections.<Option>emptyList(), tagId);
    }

    private void checkOptionValues(Collection<Option> expectedOptions, Long tagId) {
        List<Long> createdTOVs = jdbcTemplate.query("select option_id from tagoptionvalue where tag_id = " + tagId, new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getLong(1);
            }
        });

        assertEquals("Wrong option values count", expectedOptions.size(), createdTOVs.size());
        for (Option option : expectedOptions) {
            assertTrue("Expected option is absent", createdTOVs.contains(option.getId()));
        }
    }

    private CreativeSize createEmptySize() {
        CreativeSize size = sizeTF.create();
        sizeTF.persist(size);
        assertTrue(size.getAllOptions().size() == 0);
        return size;
    }

    private Collection<Option> attachTemplateToSizeWithOptions(CreativeSize size, int optionsCount) {
        Set<Option> result = new HashSet<>();
        CreativeTemplate template = templateTF.createPersistent();
        templateTF.createPersistentTemplateFile(template, TemplateFileType.TEXT, "html", size, templateTF.getTestEntityRandomName());
        for (int i = 0; i < optionsCount; i++) {
            OptionGroup optionGroup = optionGroupTF.createPersistent(template, OptionGroupType.Publisher);
            result.add(optionTF.createPersistent(optionGroup, OptionType.STRING));
        }
        return result;
    }
}
