package com.foros.session.site;

import com.foros.AbstractValidationsTest;
import com.foros.model.Country;
import com.foros.model.account.Account;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.RateType;
import com.foros.model.creative.CreativeSize;
import com.foros.model.currency.Currency;
import com.foros.model.security.AccountType;
import com.foros.model.site.ContentCategory;
import com.foros.model.site.SiteRate;
import com.foros.model.site.SiteRateType;
import com.foros.model.site.Tag;
import com.foros.model.site.TagOptionValue;
import com.foros.model.site.TagPricing;
import com.foros.session.account.AccountService;
import com.foros.session.admin.accountType.AccountTypeService;
import com.foros.session.admin.currency.CurrencyService;
import com.foros.test.factory.CountryTestFactory;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.SiteTestFactory;
import com.foros.test.factory.TagsTestFactory;
import com.foros.util.RandomUtil;

import group.Db;
import group.Validation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class TagValidationsTest extends AbstractValidationsTest {
    private static final String VALIDATE_CREATE = "Tag.create";
    private static final String VALIDATE_UPDATE = "Tag.update";
    private static final String VALIDATE_UPDATE_OPTIONS = "Tag.updateOptions";
    @Autowired
    private TagsTestFactory tagsTF;
    @Autowired
    private SiteTestFactory siteTF;
    @Autowired
    private CreativeSizeTestFactory sizeTF;
    @Autowired
    private CountryTestFactory countryTF;
    @Autowired
    private TagsService tagsService;
    @Autowired
    private AccountTypeService accountTypeService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private CurrencyService currencyService;

    private Tag tag;

    @Test
    public void testValidateCreate() {
        validate(VALIDATE_CREATE, tag);
        assertCheck(0);

        validateForCreativeSize(tag, VALIDATE_CREATE);

        validateForContentCategories(tag, VALIDATE_CREATE);

        validateForInventoryEstimation(tag, VALIDATE_CREATE);

        validateForPricing(tag, VALIDATE_CREATE);

        // check other violations
        tag.setName(RandomUtil.getRandomString(101));

        validate(VALIDATE_CREATE, tag);
        assertCheck(1, "name");

        // todo: add test to create a tag with id set.
    }

    @Test
    public void testValidateUpdate() {
        tagsService.create(tag);
        validate(VALIDATE_UPDATE, tag);
        assertCheck(0);

        validateForCreativeSize(tag, VALIDATE_UPDATE);

        validateForContentCategories(tag, VALIDATE_UPDATE);

        validateForInventoryEstimation(tag, VALIDATE_UPDATE);

        validateForPricing(tag, VALIDATE_UPDATE);

        validate(VALIDATE_UPDATE, tag);

        assertCheck(0);
    }

    @Test
    public void testValidateUpdateOptions() {
        tagsService.create(tag);
        validate(VALIDATE_UPDATE_OPTIONS, tag);
        assertCheck(0);

        tag.getOptions().add(new TagOptionValue(0L, 0L));
        validate(VALIDATE_UPDATE_OPTIONS, tag);
        assertCheck(1, "options");
    }

    @Override @org.junit.Before public void setUp() throws Exception {
        super.setUp();
        tag = tagsTF.create(siteTF.createPersistent(), tagsTF.createTagPricing(null, BigDecimal.ZERO));
        // add content categories
        Country country = tag.getAccount().getCountry();
        Set<ContentCategory> categories = new HashSet<ContentCategory>();

        categories.add(countryTF.createContentCategoryPersistent(country));
        categories.add(countryTF.createContentCategoryPersistent(country));
        tag.setContentCategories(categories);
    }

    private void validateForPricing(Tag tag, String validateName) {
        validateTagPricingsRequired(tag, validateName);
        validateCPM(tag, validateName);
        validateTagPricingsUniquiness(tag, validateName);
        validateCCGTypeToRateTypeConstraint(tag, validateName);
    }

    private void validateTagPricingsRequired(Tag tag, String validateName) {
        List<TagPricing> tagPricings = tag.getTagPricings();

        tag.setInventoryEstimationFlag(false);
        tag.setTagPricings(new ArrayList<TagPricing>());

        validate(validateName, tag);
        assertEquals(1, violations.size());
        assertHasViolation("pricing");

        tag.setTagPricings(tagPricings);
    }

    private void validateCCGTypeToRateTypeConstraint(Tag tag, String validateName) {
        List<TagPricing> tagPricings = tag.getTagPricings();

        TagPricing tp1 = new TagPricing();
        TagPricing defaultTp = new TagPricing();
        SiteRate rate = new SiteRate();
        rate.setRate(BigDecimal.valueOf(0));
        rate.setRateType(SiteRateType.CPM);
        defaultTp.setSiteRate(rate);
        tp1.setSiteRate(rate);
        Country c = tag.getAccount().getCountry();
        tp1.setCountry(c);
        tp1.setCcgRateType(RateType.CPA);
        tp1.setCcgType(CCGType.TEXT);
        tag.setTagPricings(new ArrayList<TagPricing>(Arrays.asList(defaultTp, tp1)));

        validate(validateName, tag);
        assertEquals(1, violations.size());
        assertHasViolation("pricings[1].rateType");

        tag.setTagPricings(tagPricings);
    }

    private void validateTagPricingsUniquiness(Tag tag, String validateName) {
        List<TagPricing> tagPricings = tag.getTagPricings();

        // Test tag pricings uniqueness
        TagPricing tp1 = new TagPricing();
        TagPricing tp2 = new TagPricing();
        TagPricing tp3 = new TagPricing();
        TagPricing defaultTp = new TagPricing();
        SiteRate rate = new SiteRate();
        rate.setRate(BigDecimal.valueOf(0));
        rate.setRateType(SiteRateType.CPM);
        defaultTp.setSiteRate(rate);
        tp1.setSiteRate(rate);
        tp2.setSiteRate(rate);
        tp3.setSiteRate(rate);
        Country c = tag.getAccount().getCountry();
        tp1.setCountry(c);
        tp2.setCountry(c);
        tp3.setCountry(c);
        tp1.setCcgRateType(RateType.CPA);
        tp2.setCcgRateType(RateType.CPA);
        tp3.setCcgRateType(RateType.CPA);
        tp1.setCcgType(CCGType.DISPLAY);
        tp2.setCcgType(CCGType.DISPLAY);
        tp3.setCcgType(CCGType.DISPLAY);
        tag.setTagPricings(new ArrayList<TagPricing>(Arrays.asList(defaultTp, tp1, tp2, tp3)));

        validate(validateName, tag);
        assertEquals(2, violations.size());
        assertHasViolation("pricings[2].unique");
        assertHasViolation("pricings[3].unique");

        // Default pricing duplicated

        TagPricing defaultTp2 = new TagPricing();
        defaultTp2.setSiteRate(rate);

        tag.setTagPricings(new ArrayList<TagPricing>(Arrays.asList(defaultTp, defaultTp2)));

        validate(validateName, tag);
        assertEquals(1, violations.size());
        assertHasViolation("pricings[1].unique");

        tag.setTagPricings(tagPricings);
    }


    private void validateCPM(Tag tag, String validateName) {

        Account account = accountService.find(tag.getAccount().getId());
        Currency currency = currencyService.findById(account.getCurrency().getId());

        int prevFractionDigits = currency.getFractionDigits();

        // fractionDigits = 2
        currency.setFractionDigits(2);
        entityManager.merge(currency);

        TagPricing pricing = new TagPricing();
        SiteRate siteRate = new SiteRate();

        siteRate.setRate(new BigDecimal("1.23"));
        siteRate.setRateType(SiteRateType.CPM);
        siteRate.setTagPricing(pricing);
        pricing.setSiteRate(siteRate);

        tag.getTagPricings().clear();
        tag.getTagPricings().add(pricing);
        commitChanges();
        clearContext();

        validate(validateName, tag);

        assertEquals(0, violations.size());

        siteRate.setRate(new BigDecimal("1.234"));
        siteRate.setRateType(SiteRateType.CPM);
        tag.getTagPricings().clear();
        tag.getTagPricings().add(pricing);
        validate(validateName, tag);

        assertEquals(1, violations.size());
        assertHasViolation("pricings[0].rate");

        // fractionDigits = 0
        currency.setFractionDigits(0);
        entityManager.merge(currency);

        siteRate.setRate(new BigDecimal("1"));
        siteRate.setRateType(SiteRateType.CPM);
        pricing.setSiteRate(siteRate);

        tag.getTagPricings().clear();
        tag.getTagPricings().add(pricing);

        validate(validateName, tag);

        assertEquals(0, violations.size());

        siteRate.setRate(new BigDecimal("1.234"));
        siteRate.setRateType(SiteRateType.CPM);
        tag.getTagPricings().clear();
        tag.getTagPricings().add(pricing);
        validate(validateName, tag);

        assertEquals(1, violations.size());
        assertHasViolation("pricings[0].rate");

        // cpm = null
        siteRate.setRate(null);
        siteRate.setRateType(SiteRateType.CPM);
        pricing.setSiteRate(siteRate);

        tag.getTagPricings().clear();
        tag.getTagPricings().add(pricing);

        validate(validateName, tag);

        assertEquals(1, violations.size());
        assertHasViolation("pricings[0].rate");

        // Revenue Share
        siteRate.setRatePercent(BigDecimal.ZERO);
        siteRate.setRateType(SiteRateType.RS);
        pricing.setSiteRate(siteRate);

        tag.getTagPricings().clear();
        tag.getTagPricings().add(pricing);

        validate(validateName, tag);

        assertEquals(0, violations.size());

        siteRate.setRatePercent(BigDecimal.valueOf(100.01));
        siteRate.setRateType(SiteRateType.RS);
        pricing.setSiteRate(siteRate);

        tag.getTagPricings().clear();
        tag.getTagPricings().add(pricing);

        validate(validateName, tag);

        assertEquals(1, violations.size());
        assertHasViolation("pricings[0].rate");

        siteRate.setRatePercent(BigDecimal.valueOf(0.001));
        siteRate.setRateType(SiteRateType.RS);
        pricing.setSiteRate(siteRate);

        tag.getTagPricings().clear();
        tag.getTagPricings().add(pricing);

        validate(validateName, tag);

        assertEquals(1, violations.size());
        assertHasViolation("pricings[0].rate");

        siteRate.setRatePercent(null);
        siteRate.setRateType(SiteRateType.RS);
        pricing.setSiteRate(siteRate);

        tag.getTagPricings().clear();
        tag.getTagPricings().add(pricing);

        validate(validateName, tag);

        assertEquals(1, violations.size());
        assertHasViolation("pricings[0].rate");

        siteRate.setRate(BigDecimal.ONE);
        siteRate.setRateType(null);
        pricing.setSiteRate(siteRate);

        tag.getTagPricings().clear();
        tag.getTagPricings().add(pricing);

        validate(validateName, tag);

        assertEquals(1, violations.size());
        assertHasViolation("pricings[0].rateType");

        // switch currency fraction digits back
        currency.setFractionDigits(prevFractionDigits);
        entityManager.merge(currency);

        // set valid pricing
        siteRate.setRate(new BigDecimal("1.23"));
        siteRate.setRateType(SiteRateType.CPM);
        tag.getTagPricings().clear();
        tag.getTagPricings().add(pricing);
        validate(validateName, tag);
    }

    private void validateForContentCategories(Tag tag, String validateName) {
        // specify no content categories
        Set<ContentCategory> oldCategories = tag.getContentCategories();
        tag.setContentCategories(new LinkedHashSet<ContentCategory>());
        validate(validateName, tag);
        assertEquals(1, violations.size());
        assertHasViolation("contentCategories");

        // use deleted content category
        // remove one of the used categories
        Set<ContentCategory> newCC = new HashSet<ContentCategory>();
        ContentCategory toBeRemoved = (ContentCategory)oldCategories.toArray()[0];
        Country country = tag.getAccount().getCountry();

        newCC.addAll(country.getContentCategories());
        newCC.remove(toBeRemoved);
        Country updatedCountry = countryTF.createCopy(tag.getAccount().getCountry());
        updatedCountry.setVersion(country.getVersion());
        updatedCountry.setContentCategories(newCC);
        countryTF.update(updatedCountry);
        validate(validateName, tag);
        assertEquals(1, violations.size());
        assertHasViolation("contentCategories");

        // return to original content categories
        oldCategories.remove(toBeRemoved);
        tag.setContentCategories(oldCategories);
    }



    private void validateForCreativeSize(Tag tag, String validateName) {
        // use unsupported size
        CreativeSize oldSize = tag.getSizes().iterator().next();
        tag.getSizes().add(sizeTF.createPersistent());
        validate(validateName, tag);
        assertCheck(2, "sizes");

        tag.getSizes().clear();
        validate(validateName, tag);
        assertCheck(1, "sizes");
        // return to original size
        tag.getSizes().add(oldSize);
    }

    private void assertCheck(int violationCount, String ... paths) {
        assertEquals(violationCount, violations.size());
        assertHasViolation(paths);
    }

    private void validateForInventoryEstimation(Tag tag, String validateName) {
        AccountType at = tag.getSite().getAccount().getAccountType();
        at.setPublisherInventoryEstimationFlag(false);
        accountTypeService.update(at);

        tag.setInventoryEstimationFlag(true);
        validate(validateName, tag);
        assertCheck(1, "adservingMode");
        tag.setInventoryEstimationFlag(false);
    }
}
