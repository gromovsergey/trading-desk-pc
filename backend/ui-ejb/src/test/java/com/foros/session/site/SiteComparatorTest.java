package com.foros.session.site;

import com.foros.model.Country;
import com.foros.model.Status;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.SizeType;
import com.foros.model.site.PassbackType;
import com.foros.model.site.Site;
import com.foros.model.site.SiteRate;
import com.foros.model.site.SiteRateType;
import com.foros.model.site.Tag;
import com.foros.model.site.TagPricing;
import com.foros.util.UrlUtil;

import group.Unit;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author alexey_chernenko
 */
@Category(Unit.class)
public class SiteComparatorTest extends Assert {
    Site existing;
    Tag existingTag;

    @Before public void setUp() throws Exception {
        existing = createSite(1L, "SomeName", "http://some.where.com", Status.ACTIVE);
        existingTag = createTag(2L, "TagName", "1x1", "http://implression.com", "<table></table>");
        TagPricing pricing = createTagPricing(3L, "US", 4L, new BigDecimal(123.12345), Status.ACTIVE);
        existingTag.getTagPricings().add(pricing);
        pricing = createTagPricing(5L, null, 6L, new BigDecimal(100.001), Status.ACTIVE);
        existingTag.getTagPricings().add(pricing);
        existing.getTags().add(existingTag);
    }

    @Test
    public void testComparePlaneSites() throws Exception {
        Site current = createSite(1L, "SomeName", "http://some.where.com", Status.ACTIVE);
        current.setName("Another Name");
        current.setSiteUrl("http://some.where.com");
        current.setStatus(Status.ACTIVE);

        assertFalse("Must not be equal", SiteComparator.equals(existing, current));
    }

    @Test
    public void testSiteEquals() throws Exception {
        Site current = createSite(1L, "SomeName", "http://some.where.com", Status.ACTIVE);
        assertTrue("Must be equal", SiteComparator.equals(existing, current));
    }

    @Test
    public void testTagEquals() throws Exception {
        Tag currentTag = createTag(2L, "TagName", "1x1", "http://implression.com", "<table></table>");
        TagPricing pricing = createTagPricing(3L, "US", 4L, new BigDecimal(123.12345), Status.ACTIVE);
        currentTag.getTagPricings().add(pricing);
        pricing = createTagPricing(5L, null, 6L, new BigDecimal(100.001), Status.ACTIVE);
        currentTag.getTagPricings().add(pricing);
        assertTrue("Must be equal", SiteComparator.equals(existingTag, currentTag));
    }


    @Test
    public void testTagModified() throws Exception {
        Tag currentTag = createTag(2L, "TagName", "2x2", "http://implression.com", "<table>Hello</table>");
        assertFalse("Must not be equal", SiteComparator.equals(existingTag, currentTag));
    }

    @Test
    public void testTagPricingChanged() throws Exception {
        Tag currentTag = createTag(2L, "TagName", "1x1", "http://implression.com", "<table></table>");
        TagPricing pricing = createTagPricing(null, "US", null, new BigDecimal(123.12345), null);
        currentTag.getTagPricings().add(pricing);
        pricing = createTagPricing(8L, null, null, new BigDecimal(100.002), null); // <--- Modified
        currentTag.getTagPricings().add(pricing);

        assertFalse("Must not be equal", SiteComparator.equals(existingTag, currentTag));
    }

    @Test
    public void testTagPricingAdded() throws Exception {
        Tag currentTag = createTag(2L, "TagName", "1x1", "http://implression.com", "<table></table>");

        TagPricing pricing = createTagPricing(null, "US", null, new BigDecimal(123.12345), null);
        pricing.setTags(currentTag);
        currentTag.getTagPricings().add(pricing);

        pricing = createTagPricing(null, null, null, new BigDecimal(100.002), null);
        pricing.setTags(currentTag);
        currentTag.getTagPricings().add(pricing);

        pricing = createTagPricing(null, "UK", null, new BigDecimal(100.002), null);
        pricing.setTags(currentTag);
        currentTag.getTagPricings().add(pricing);

        assertFalse("Must not be equal", SiteComparator.equals(existingTag, currentTag));
    }

    @Test
    public void testNullSites() throws Exception {
        Site current = createSite(1L, "SomeName", "http://some.where.com", Status.ACTIVE);
        assertFalse("Must not be equal", SiteComparator.equals(existing, null));
        assertFalse("Must not be equal", SiteComparator.equals(null, current));
    }

    @Test
    public void testEquality() throws Exception {
        Site current = createSite(1L, "SomeName", "http://some.where.com", Status.ACTIVE);
        assertTrue("Must be equal", SiteComparator.equals(existing, existing));
        assertTrue("Must be equal", SiteComparator.equals(current, current));
    }

    @Test
    public void testTagPassbackChanges() {
        //Existing tag has valid passback url and new tag has passback html
        Tag exitingTag = createTag(1L, "SomeName", "CS", null, "https://some.site.com");
        Tag currentTag = createTag(1L, "SomeName", "CS", null, "<HTML></HTML>");
        assertFalse("Must not be equal", SiteComparator.equals(exitingTag, currentTag));

        //Existing tag has passback html and new tag has valid passback url
        exitingTag = createTag(1L, "SomeName", "CS", null, "<HTML></HTML>");
        currentTag = createTag(1L, "SomeName", "CS", null, "https://some.site.com");
        assertFalse("Must not be equal", SiteComparator.equals(exitingTag, currentTag));

        //Existing tag has no passback url/html and new tag has no passback url/html
        exitingTag = createTag(1L, "SomeName", "CS", null, null);
        currentTag = createTag(1L, "SomeName", "CS", null, null);
        assertTrue("Must be equal", SiteComparator.equals(exitingTag, currentTag));

        //Existing tag has valid passback url and new tag has null passback url/html
        exitingTag = createTag(1L, "SomeName", "CS", null, "http://some.site.com");
        currentTag = createTag(1L, "SomeName", "CS", null, null);
        assertFalse("Must not be equal", SiteComparator.equals(exitingTag, currentTag));

        //Existing tag has passback html and new tag has null passback url/html
        exitingTag = createTag(1L, "SomeName", "CS", null, "<HTML></HTML>");
        currentTag = createTag(1L, "SomeName", "CS", null, null);
        assertFalse("Must not be equal", SiteComparator.equals(exitingTag, currentTag));

        //Existing tag has null passback url/html and new tag has valid passback url
        exitingTag = createTag(1L, "SomeName", "CS", null, null);
        currentTag = createTag(1L, "SomeName", "CS", null, "http://some.where.com?action");
        assertFalse("Must not be equal", SiteComparator.equals(exitingTag, currentTag));

        //Existing tag has null passback url/html and new tag has passback html
        exitingTag = createTag(1L, "SomeName", "CS", null, null);
        currentTag = createTag(1L, "SomeName", "CS", null, "<HTML></HTML>");
        assertFalse("Must not be equal", SiteComparator.equals(exitingTag, currentTag));

        //Existing tag has passback html and new tag has passback html
        exitingTag = createTag(1L, "SomeName", "CS", null, "<xHTML></xHTML>");
        currentTag = createTag(1L, "SomeName", "CS", null, "<HTML></HTML>");
        assertFalse("Must not be equal", SiteComparator.equals(exitingTag, currentTag));

        //Existing tag has valid passback url and new tag has valid passback url
        exitingTag = createTag(1L, "SomeName", "CS", null, "https://some.site.com");
        currentTag = createTag(1L, "SomeName", "CS", null, "http://some.site.com");
        assertFalse("Must not be equal", SiteComparator.equals(exitingTag, currentTag));
    }

    private Site createSite(Long id, String siteName, String siteUrl, Status status) {
        Site site = new Site(id);
        site.setName(siteName);
        site.setSiteUrl(siteUrl);
        if (status != null) {
            site.setStatus(status);
        }
        site.setTags(new LinkedHashSet<Tag>());
        return site;
    }

    private Tag createTag(Long tagId, String tagName, String creativeSizeProtocolName, String trackingPixel, String defaultHtml) {
        Tag tag = new Tag(tagId);
        tag.setName(tagName);
        tag.setSizeType(new SizeType("sizeType"));
        if (creativeSizeProtocolName != null) {
            CreativeSize size = new CreativeSize();
            size.setProtocolName(creativeSizeProtocolName);
            tag.getSizes().add(size);
        }

        if(UrlUtil.isSchemaUrl(defaultHtml)) {
            //its a passback url
            tag.setPassbackType(PassbackType.HTML_URL);
            tag.setPassback(defaultHtml);
        } else {
            tag.setPassbackType(PassbackType.HTML_CODE);
            //its a passback html
            tag.setPassbackHtml(defaultHtml);
        }
        tag.setStatus(Status.ACTIVE);
        tag.setTagPricings(new LinkedList<TagPricing>());
        return tag;
    }

    private TagPricing createTagPricing(Long id, String countryCode, Long rateId, BigDecimal value, Status status) {
        TagPricing tagPricing = new TagPricing(id);
        tagPricing.setSiteRate(new SiteRate(rateId));
        tagPricing.getSiteRate().setRate(value);
        tagPricing.getSiteRate().setRateType(SiteRateType.CPM);
        if (countryCode != null) {
            tagPricing.setCountry(new Country(countryCode));
        }
        if (status != null) {
            tagPricing.setStatus(status);
        }
        return tagPricing;
    }


}
