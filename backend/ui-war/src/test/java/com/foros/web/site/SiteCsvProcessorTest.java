package com.foros.web.site;

import com.foros.AbstractUnitTest;
import com.foros.action.site.csv.SiteCsvProcessor;
import com.foros.action.site.csv.SiteExportHelper;
import com.foros.action.site.csv.SiteFieldCsv;
import com.foros.action.site.csv.SiteParserException;
import com.foros.model.Status;
import com.foros.model.account.PublisherAccount;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.SizeType;
import com.foros.model.site.PassbackType;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.model.site.TagPricing;
import com.foros.session.ServiceLocatorMock;
import com.foros.session.UploadContext;
import com.foros.session.site.SiteUploadUtil;
import com.foros.session.site.TagsService;
import com.foros.util.EqualsUtil;
import com.foros.util.StringUtil;
import com.foros.validation.constraint.violation.ConstraintViolation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import group.Unit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.commons.beanutils.PropertyUtils;
import org.easymock.EasyMock;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class SiteCsvProcessorTest extends AbstractUnitTest {

    @Rule
    public EasyMockRule mockRule = new EasyMockRule(this);

    @Mock
    private TagsService tagsService;

    @Before
    public void setUpExpectations() throws Exception {
        EasyMock.expect(tagsService.find(EasyMock.anyLong())).andReturn(new Tag()).anyTimes();
        ServiceLocatorMock.getInstance().injectService(TagsService.class, tagsService);
        EasyMock.replay(tagsService);
    }

    @Test
    @Category(Unit.class)
    public void invalidPricing() throws Exception {
        // parse input sites
        List<Site> sites = parse("invalidPricing.csv", false);
        // check for expected validation error
        Site site = sites.get(0);
        UploadContext result = SiteUploadUtil.getUploadContext(getTag(site, 0));

        hasErrorMessage(result, StringUtil.getLocalizedString("errors.cost"));
    }

    @Test
    @Category(Unit.class)
    public void testInvalidPricingFraction() throws Exception {
        // parse input sites
        List<Site> sites = parse("invalidPricingFraction.csv", false);

        // check for expected validation error
        Site site = sites.get(0);
        UploadContext result = SiteUploadUtil.getUploadContext(getTag(site, 0));

        hasErrorMessage(result, StringUtil.getLocalizedString("errors.cost"));
    }

    @Test
    @Category(Unit.class)
    public void missingTagPassbackType() throws Exception {
        List<Site> sites = parse("missingTagPassbackType.csv", false);
        Site site = sites.get(0);
        UploadContext result = SiteUploadUtil.getUploadContext(getTag(site, 0));
        hasErrorMessage(result, StringUtil.getLocalizedString("errors.field.invalid"));
    }

    @Test
    @Category(Unit.class)
    public void duplicatedSiteTagPair() throws Exception {
        List<Site> sites = parse("duplicatedSiteIdTagId.csv", false);
        Site site = sites.get(0);
        UploadContext result = SiteUploadUtil.getUploadContext(getTag(site, 2));

        hasErrorMessage(result, StringUtil.getLocalizedString("errors.duplicated.siteTag.pair"));
    }

    @Test
    @Category(Unit.class)
    public void duplicatedTagPrice() throws Exception {
        List<Site> sites = parse("duplicatedTagPrice.csv", false);
        Site site = sites.get(0);
        UploadContext result = SiteUploadUtil.getUploadContext(getTag(site, 1));

        hasErrorMessage(result, StringUtil.getLocalizedString("errors.duplicated.tagPricing"));
    }

    @Test
    @Category(Unit.class)
    public void invalidStatus() throws Exception {
        List<Site> sites = parse("invalidStatus.csv", false);
        UploadContext result = SiteUploadUtil.getUploadContext(sites.get(0));

        hasErrorMessage(result, StringUtil.getLocalizedString("errors.site.status.invalid", "Pending"));
    }

    @Test
    @Category(Unit.class)
    public void maxRowsExceed() throws Exception {
        testInvalid("tooManyRows.csv", "site.error.tooManyRows");
    }

    @Test
    @Category(Unit.class)
    public void maxRows() throws Exception {
        testValid(new SiteCsvProcessor(Locale.UK, 200, false), "manyRows.csv");
    }

    @Test
    @Category(Unit.class)
    public void lastSiteOverride() throws Exception {
        List<Site> sites = testValid(new SiteCsvProcessor(Locale.UK, 200, false), "overridedSite.csv");
        assertEquals("http://some.site.com3", sites.get(0).getSiteUrl());
        assertEquals(Status.DELETED, sites.get(0).getStatus());
        assertEquals("SiteName_2", sites.get(0).getName());
    }

    @Test
    @Category(Unit.class)
    public void validParse() throws Exception {
        List<Site> sites = testValid(new SiteCsvProcessor(Locale.UK, 200, false), "updateSite.csv");
        assertEquals("size", 1, sites.size());

        Site site = sites.get(0);
        assertEquals("SiteName_1", PropertyUtils.getProperty(site, "name"));
        assertEquals(1L, PropertyUtils.getProperty(site, "id"));
        assertEquals(Status.ACTIVE, PropertyUtils.getProperty(site, "status"));
        assertEquals("http://some.site.com", PropertyUtils.getProperty(site, "siteUrl"));

        List<Tag> tags = new ArrayList<>(site.getTags());
        Tag firstTag = tags.get(0);
        Tag secondTag = tags.get(1);
        Tag thirdTag = tags.get(2);
        Tag fourthTag = tags.get(3);

        List<TagPricing> firstTagPricings = new ArrayList<>(firstTag.getTagPricings());
        TagPricing firstTagPricing = firstTagPricings.get(0);
        TagPricing secondTagPricing = firstTagPricings.get(1);
        TagPricing thirdTagPricing = firstTagPricings.get(2);
        TagPricing fourthTagPricing = fourthTag.getTagPricings().get(0);

        assertEquals("tags.size", 4, tags.size());
        assertEquals(3L, PropertyUtils.getProperty(firstTag, "id"));
        assertEquals("newTag-tag1", PropertyUtils.getProperty(firstTag, "name"));
        assertEquals("Banner", PropertyUtils.getProperty(firstTag, "sizeType.defaultName"));
        assertEquals("728x90t_225", firstTag.getSizes().iterator().next().getProtocolName());

        assertEquals(null, PropertyUtils.getProperty(firstTagPricing, "country"));
        assertBigDecimal("siteRate.rate", new BigDecimal(0, MathContext.DECIMAL32), firstTagPricing);

        assertEquals("US", PropertyUtils.getProperty(secondTagPricing, "country.countryCode"));
        assertBigDecimal("siteRate.rate", new BigDecimal(2.1, MathContext.DECIMAL32), secondTagPricing);

        assertEquals("AU", PropertyUtils.getProperty(thirdTagPricing, "country.countryCode"));
        assertBigDecimal("siteRate.rate", new BigDecimal(0.11, MathContext.DECIMAL32), thirdTagPricing);

        assertEquals("http://fo.com", PropertyUtils.getProperty(secondTag, "passback"));
        assertEquals("<img>http://fo.com</img>", PropertyUtils.getProperty(thirdTag, "passbackHtml"));

        assertEquals(null, PropertyUtils.getProperty(fourthTagPricing, "country"));
        assertBigDecimal("siteRate.rate", new BigDecimal(1.23, MathContext.DECIMAL32), fourthTagPricing);
    }

    @Test
    @Category(Unit.class)
    public void parseAnotherLocale() throws Exception {
        List<Site> sites = testValid(new SiteCsvProcessor(new Locale("ru", "RU", ""), 2, false), "updateSiteRu.csv");
        Site site = sites.get(0);

        List<Tag> tags = new ArrayList<>(site.getTags());
        Tag firstTag = tags.get(0);

        List<TagPricing> firstTagPricings = new ArrayList<>(firstTag.getTagPricings());
        TagPricing firstTagPricing = firstTagPricings.get(0);
        TagPricing secondTagPricing = firstTagPricings.get(1);
        TagPricing thirdTagPricing = firstTagPricings.get(2);

        assertEquals(null, PropertyUtils.getProperty(firstTagPricing, "country"));
        assertBigDecimal("siteRate.rate", new BigDecimal(0), firstTagPricing);

        assertEquals("US", PropertyUtils.getProperty(secondTagPricing, "country.countryCode"));
        assertBigDecimal("siteRate.rate", new BigDecimal(2.1), secondTagPricing);

        assertEquals("AU", PropertyUtils.getProperty(thirdTagPricing, "country.countryCode"));
        assertBigDecimal("siteRate.rate", new BigDecimal(0.11), thirdTagPricing);
    }

    @Test
    @Category(Unit.class)
    public void createAnyStatus() throws Exception {
        List<Site> sites = testValid(new SiteCsvProcessor(Locale.UK, 200, false), "createSite.csv");
        assertEquals("size", 1, sites.size());
        Site site = sites.get(0);
        assertEquals(Status.ACTIVE, PropertyUtils.getProperty(site, "status"));
    }

    @Test
    @Category(Unit.class)
    public void readAndSave() throws Exception {
        List<Site> sites = testValid(new SiteCsvProcessor(Locale.UK, 200, false), "readAndSave.csv");
        File outputFile = new File(getTargetFolder(), "siteOutput.csv");
        outputFile.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(outputFile);
        SiteExportHelper.serialize(fos, sites, SiteFieldCsv.EXTERNAL_EXPORT_METADATA);

        assertCRLFRecordEnd(outputFile);

        FileInputStream fis = new FileInputStream(outputFile);
        List<Site> readList = new SiteCsvProcessor(Locale.UK, 200, false).parse(fis);

        assertTrue(sites.size() == readList.size());
        Iterator<Site> siteIterator1 = sites.iterator();
        Iterator<Site> siteIterator2 = readList.iterator();
        while (siteIterator1.hasNext()) {
            Site site1 = siteIterator1.next();
            Site site2 = siteIterator2.next();
            assertEquals(site1.getId(), site2.getId());
            assertEquals(site1.getName(), site2.getName());
            assertEquals(site1.getStatus(), site2.getStatus());
            assertEquals(site1.getSiteUrl(), site2.getSiteUrl());
            Set<Tag> tags1 = site1.getTags();
            Set<Tag> tags2 = site2.getTags();
            assertTrue(tags1.size() == tags2.size());
            Iterator<Tag> tagIterator1 = tags1.iterator();
            Iterator<Tag> tagIterator2 = tags2.iterator();
            while (tagIterator1.hasNext()) {
                Tag tag1 = tagIterator1.next();
                Tag tag2 = tagIterator2.next();
                assertEquals(tag1.getSite(), site1);
                assertEquals(tag2.getSite(), site2);
                assertEquals(tag1.getId(), tag2.getId());
                assertEquals(tag1.getName(), tag2.getName());
                assertEquals(tag1.getSizes().size(), tag2.getSizes().size());
                assertEquals(tag1.getTagPricings(), tag2.getTagPricings());
                assertEquals(tag1.isAllowExpandable(), tag2.isAllowExpandable());
            }
        }
    }

    @Test
    @Category(Unit.class)
    public void saveReadDeletedTags() throws Exception {
        Site site = new Site(1L, "Test name");
        site.setSiteUrl("http://some.com");
        site.setStatus(Status.ACTIVE);

        Tag tag = new Tag(2L, "Tag name");
        tag.setSizeType(new SizeType("sizeType"));
        CreativeSize size = new CreativeSize();
        size.setProtocolName("Test protocol name");
        tag.getSizes().add(size);
        tag.setTagPricings(new LinkedList<TagPricing>());
        tag.setStatus(Status.DELETED);
        tag.setSite(site);
        tag.setPassbackType(PassbackType.HTML_CODE);

        site.setTags(new LinkedHashSet<Tag>());
        site.getTags().add(tag);

        PublisherAccount account = new PublisherAccount(23L, "accountName");
        site.setAccount(account);

        File outputFile = new File(getTargetFolder(), "siteOutput.csv");
        outputFile.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(outputFile);
        SiteExportHelper.serialize(fos, Collections.singletonList(site), SiteFieldCsv.EXTERNAL_EXPORT_METADATA);

        SiteCsvProcessor siteCsvProcessor = new SiteCsvProcessor(Locale.UK, 200, false);
        FileInputStream fis = new FileInputStream(outputFile);
        List<Site> sites = siteCsvProcessor.parse(fis);

        assertEquals("Site size", 1, sites.size());
        Site parsedSite = sites.get(0);

        assertEquals(site.getName(), PropertyUtils.getProperty(parsedSite, "name"));
        assertEquals(site.getId(), PropertyUtils.getProperty(parsedSite, "id"));
        assertEquals(site.getSiteUrl(), PropertyUtils.getProperty(parsedSite, "siteUrl"));
        assertEquals(site.getStatus(), PropertyUtils.getProperty(parsedSite, "status"));
    }

    @Test
    @Category(Unit.class)
    public void invalidFormat() throws Exception {
        List<Site> sites = parse("wrongFormat.csv", false);
        UploadContext result = SiteUploadUtil.getUploadContext(sites.get(2));

        hasErrorMessage(result, StringUtil.getLocalizedString("errors.invalid.rowFormat", new HashSet<>(Arrays.asList(
                SiteFieldCsv.EXTERNAL_EXPORT_METADATA.getColumns().size(),
                SiteFieldCsv.EXTERNAL_REVIEW_METADATA.getColumns().size())), 4));
    }

    private static void assertBigDecimal(String name, BigDecimal value, Object sourceObject) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        assertTrue("Expected " + value + ", but was " + PropertyUtils.getProperty(sourceObject, name), EqualsUtil.equalsBigDecimal(value, (BigDecimal) PropertyUtils.getProperty(sourceObject, name)));
    }

    private void testInvalid(String file, String expectedErrorMessage) throws Exception {
        SiteCsvProcessor siteCsvProcessor = new SiteCsvProcessor(Locale.UK, 200, false);
        try (InputStream is = openFile(getFile(file, false))) {
            siteCsvProcessor.parse(is);
            fail("Must fail as invalid input provided");
        } catch (SiteParserException spe) {
            assertEquals("Error message", expectedErrorMessage, spe.getKey());
        }
    }

    private List<Site> testValid(SiteCsvProcessor siteCsvProcessor, String file) throws Exception {
        InputStream is = openFile(getFile(file, true));
        is = replaceNativeEOLWithDOS(is);
        try {
            return siteCsvProcessor.parse(is);
        } finally {
            is.close();
        }
    }

    private void assertCRLFRecordEnd(File outputFile) throws IOException {
        char[] buffer = new char[512];
        int count;
        Reader reader = new FileReader(outputFile);
        StringBuilder stringBuffer = new StringBuilder();
        while((count = reader.read(buffer)) != -1) {
            stringBuffer.append(buffer, 0, count);
        }

        // count an amount of record ends.
        String resultString = stringBuffer.toString();
        int endLineCounter = 0;
        while (resultString.lastIndexOf("\r\n") != -1) {
            resultString = resultString.substring(0, resultString.lastIndexOf("\r\n"));
            endLineCounter++;
        }

        assertEquals("End line symbols", 6 + 1, endLineCounter); // +1 for EOL in tag passback
    }

    private String getFile(String file, boolean valid) {
        if (valid) {
            return "valid/" + file;
        } else {
            return "invalid/" + file;
        }
    }

    private InputStream replaceNativeEOLWithDOS(InputStream in) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            int b;
            boolean isLastR = true;
            while ((b = in.read()) != -1) {
                if (b == '\n' && !isLastR) {
                    outputStream.write('\r');
                }

                outputStream.write(b);

                isLastR = b == '\r';
            }
        } finally {
            in.close();
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private InputStream openFile(String fileName) throws IOException {
        URL fileLocation = getClass().getResource("csv/" + fileName);
        if (fileLocation == null) {
            fail("File " + fileName + " not found");
        }
        return getClass().getResource("csv/" + fileName).openStream();
    }

    private List<Site> parse(String filename, boolean validCase) throws IOException, SiteParserException {
        SiteCsvProcessor siteCsvProcessor = new SiteCsvProcessor(Locale.UK, 200, false);
        return siteCsvProcessor.parse(openFile(getFile(filename, validCase)));
    }

    private void hasErrorMessage(UploadContext result, String expectedMessage) {
        if (result == null || !result.hasErrors() || result.getErrors().isEmpty()) {
            fail("Empty result");
        }

        if (expectedMessage == null) {
            fail("Empty expected message");
        }

        for (ConstraintViolation cv: result.getErrors()) {
            if (cv.getMessage().equals(expectedMessage)) {
                return;
            }
        }

        fail("No matching message found: " + expectedMessage);
    }

    private Tag getTag(Site site, int index) {
        Set<Tag> tags = site.getTags();
        return tags.toArray(new Tag[tags.size()])[index];
    }
}
