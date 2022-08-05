package com.foros.test.factory;

import com.foros.model.AddressField;
import com.foros.model.Country;
import com.foros.model.LocalizableName;
import com.foros.model.Timezone;
import com.foros.model.ctra.CTRAlgorithmData;
import com.foros.model.currency.Currency;
import com.foros.model.security.Language;
import com.foros.model.site.ContentCategory;
import com.foros.model.site.SiteCategory;
import com.foros.session.admin.country.CountryService;
import com.foros.session.admin.country.CountryService.PredefinedAddressField;
import com.foros.session.admin.country.ctra.CTRAlgorithmService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityNotFoundException;

@Stateless
@LocalBean
public class CountryTestFactory extends TestFactory<Country> {
    @EJB
    private CountryService countryService;

    @EJB
    private CurrencyTestFactory currencyTestFactory;

    @EJB
    private CTRAlgorithmService ctrAlgorithmService;

    @Override
    public Country create() {
        return create("XX");
    }

    public Country create(String countryCode) {
        Country country = new Country(countryCode);
        country.setAddressFields(generateAddressFields());
        country.setCurrency(currencyTestFactory.findOrCreatePersistent("RUB"));
        country.setTimezone(new Timezone(1L, "GMT"));
        country.setDefaultAgencyCommission(BigDecimal.ZERO);
        country.setDefaultPaymentTerms(20L);
        country.setMaxUrlTriggerShare(BigDecimal.ONE);
        country.setMinUrlTriggerThreshold(0L);
        country.setMinRequiredTagVisibility(0L);
        country.setCountryId(-1L);
        return country;
    }

    public Country createCopy(Country src) {
        Country country = new Country(src.getCountryCode());
        country.setAddressFields(new LinkedList<AddressField>(src.getAddressFields()));
        country.setCurrency(src.getCurrency());
        country.setTimezone(src.getTimezone());
        country.setVatEnabled(src.isVatEnabled());
        country.setDefaultVATRate(src.getDefaultVATRate());
        country.setDefaultPaymentTerms(src.getDefaultPaymentTerms());
        country.setContentCategories(new LinkedHashSet<ContentCategory>(src.getContentCategories()));
        country.setSiteCategories(new LinkedHashSet<SiteCategory>(src.getSiteCategories()));
        return country;
    }

    private List<AddressField> generateAddressFields() {
        List<AddressField> addressFields = new ArrayList<AddressField>(CountryService.PredefinedAddressField.values().length);
        for (CountryService.PredefinedAddressField predefined : CountryService.PredefinedAddressField.values()) {
            AddressField af = new AddressField();
            af.setOFFieldName(predefined.getName());
            af.setName(new LocalizableName("defName", "resKey"));
            af.setFlags(0l);
            af.setOrderNumber(0);
            addressFields.add(af);
        }
        return addressFields;
    }

    @Override
    public void persist(Country country) {
        List<AddressField> transferredAddressFields = country.getAddressFields();
        if (transferredAddressFields.size() != PredefinedAddressField.values().length) {
            throw new RuntimeException("count of address fields must be equal:" + PredefinedAddressField.values().length + "!");
        }
        if (country.isChanged("language")) {
            Language.valueOfCode(country.getLanguage());
        }
        if (country.getCurrency() == null) {
            throw new IllegalArgumentException("Currency is null");
        }
        country.setCurrency(entityManager.getReference(Currency.class, country.getCurrency().getId()));

        if (country.getTimezone() == null) {
            throw new IllegalArgumentException("Timezone is null");
        }
        country.setTimezone(entityManager.getReference(Timezone.class, country.getTimezone().getId()));
        entityManager.persist(country);

        for (AddressField af : transferredAddressFields) {
            checkAddressFieldOFName(af);
            if (af.getOFFieldName().equals(PredefinedAddressField.COUNTRY.getName())) {
                af.getName().setDefaultName(PredefinedAddressField.COUNTRY.getName());
                af.setEnabled(true);
                af.setMandatory(true);
            }
            if (af.getOFFieldName().equals(PredefinedAddressField.LINE1.getName())) {
                af.setEnabled(true);
                af.setMandatory(true);
            }
            af.setCountry(country);
            entityManager.persist(af);
        }
        entityManager.flush();
    }

    private void checkAddressFieldOFName(AddressField af) {
        for (PredefinedAddressField predefinedField : PredefinedAddressField.values()) {
            if (predefinedField.getName().equals(af.getOFFieldName())) {
                return;
            }
        }
        throw new RuntimeException("Wrong Oracle Finance Name for address field:" + af.getOFFieldName() + "!");
    }

    public void update(Country country) {
        countryService.update(country, null);
    }

    @Override
    public Country createPersistent() {
        Country country = create();
        persist(country);
        return country;
    }

    private Country createPersistent(String countryCode) {
        Country country = create(countryCode);
        persist(country);
        return country;
    }

    public Country findOrCreatePersistent(String countryCode) {
        Country country;
        try {
            country = findAny(Country.class, new QueryParam("countryCode", countryCode));
        } catch (IllegalStateException e) {
            country = createPersistent(countryCode);
        }
        return country;
    }

    public void persistCtrAlgorithm(String countryCode) {
        /**
          Commented as sometimes there are locks in the database

          SELECT blockeda.pid AS blocked_pid, blockeda.query as blocked_query,
            blockinga.pid AS blocking_pid, blockinga.query as blocking_query
          FROM pg_catalog.pg_locks blockedl
            JOIN pg_stat_activity blockeda ON blockedl.pid = blockeda.pid
            JOIN pg_catalog.pg_locks blockingl ON(blockingl.transactionid=blockedl.transactionid AND blockedl.pid != blockingl.pid)
            JOIN pg_stat_activity blockinga ON blockingl.pid = blockinga.pid
          WHERE NOT blockedl.granted AND blockinga.datname='unittest_ui_11' ;

          -[ RECORD 1 ]--+---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
          blocked_pid    | 29239
          blocked_query  | insert com.foros.model.ctra.CTRAlgorithmData: insert into CTRALGORITHM (VERSION, CAMPAIGN_TOW_LEVEL, CCGKEYWORD_KW_CTR_LEVEL, CCGKEYWORD_KW_TOW_LEVEL, CCGKEYWORD_TG_CTR_LEVEL, CCGKEYWORD_TG_TOW_LEVEL, CLICKS_INTERVAL1_DAYS, CLICKS_INTERVAL1_WEIGHT, CLICKS_INTERVAL2_DAYS, CLICKS_INTERVAL2_WEIGHT, CLICKS_INTERVAL3_WEIGHT, CPA_RANDOM_IMPS, CPC_RANDOM_IMPS, IMPS_INTERVAL1_DAYS, IMPS_INTERVAL1_WEIGHT, IMPS_INTERVAL2_DAYS, IMPS_INTERVAL2_WEIGHT, IMPS_INTERVAL3_WEIGHT, KEYWORD_CTR_LEVEL, KEYWORD_TOW_LEVEL, KWTG_CTR_DEFAULT, PUB_CTR_DEFAULT, PUB_CTR_LEVEL, SITE_CTR_LEVEL, SYS_CTR_LEVEL, SYS_KWTG_CTR_LEVEL, SYS_TOW_LEVEL, TAG_CTR_LEVEL, TG_TOW_LEVEL, TOW_RAW, COUNTRY_CODE) values ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, $16, $17, $18, $19, $20, $21, $22, $23, $24, $25, $26, $27, $28, $29, $30, $31)
          blocking_pid   | 29235
          blocking_query | load com.foros.model.campaign.Campaign: select campaign0_.CAMPAIGN_ID as CAMPAIGN1_5_1_, campaign0_.VERSION as VERSION5_1_, campaign0_.STATUS as STATUS5_1_, campaign0_.DISPLAY_STATUS_ID as DISPLAY4_5_1_, campaign0_.ACCOUNT_ID as ACCOUNT17_5_1_, campaign0_.BILL_TO_USER_ID as BILL18_5_1_, campaign0_.BUDGET_MANUAL as BUDGET5_5_1_, campaign0_.CAMPAIGN_TYPE as CAMPAIGN6_5_1_, campaign0_.COMMISSION as COMMISSION5_1_, campaign0_.DAILY_BUDGET as DAILY8_5_1_, campaign0_.DATE_END as DATE9_5_1_, campaign0_.DATE_START as DATE10_5_1_, campaign0_.DELIVERY_PACING as DELIVERY11_5_1_, campaign0_.FLAGS as FLAGS5_1_, campaign0_.FREQ_CAP_ID as FREQ19_5_1_, campaign0_.MARKETPLACE as MARKETP13_5_1_, campaign0_.MAX_PUB_SHARE as MAX14_5_1_, campaign0_.NAME as NAME5_1_, campaign0_.SALES_MANAGER_ID as SALES20_5_1_, campaign0_.SOLD_TO_USER_ID as SOLD21_5_1_, campaign0_.BUDGET as BUDGET5_1_, campaignsc1_.CAMPAIGN_ID as CAMPAIGN4_5_3_, campaignsc1_.SCHEDULE_ID as SCHEDULE1_3_, campaignsc1_.SCHEDULE_ID as SCHEDULE1_9_0_, campaignsc1_.CAMPAIGN_ID as CAMPAIGN4_9_0_, campaignsc1_.TIME_FROM as TIME2_9_0_, campaignsc1_.TIME_TO as TIME3_9_0_ from CAMPAIGN campaign0_ left outer join CAMPAIGNSCHEDULE campaignsc1_ on campaign0_.CAMPAIGN_ID=campaignsc1_.CAMPAIGN_ID where campaign0_.CAMPAIGN_ID=$1

          To be investigated
        */

    /*    try {
            ctrAlgorithmService.find(countryCode);
        } catch (EntityNotFoundException ex) {
            CTRAlgorithmData ctrAlgorithmData = new CTRAlgorithmData();
            ctrAlgorithmData.setCountryCode(countryCode);
            ctrAlgorithmData.setCpaRandomImps(2000);
            ctrAlgorithmData.setCpcRandomImps(2000);

            ctrAlgorithmData.setClicksInterval1Days(1);
            ctrAlgorithmData.setClicksInterval1Weight(BigDecimal.ONE);
            ctrAlgorithmData.setClicksInterval2Days(1);
            ctrAlgorithmData.setClicksInterval2Weight(BigDecimal.ONE);
            ctrAlgorithmData.setClicksInterval3Weight(BigDecimal.ONE);

            ctrAlgorithmData.setImpsInterval1Days(1);
            ctrAlgorithmData.setImpsInterval1Weight(BigDecimal.ONE);
            ctrAlgorithmData.setImpsInterval2Days(1);
            ctrAlgorithmData.setImpsInterval2Weight(BigDecimal.ONE);
            ctrAlgorithmData.setImpsInterval3Weight(BigDecimal.ONE);

            ctrAlgorithmData.setPubCTRDefault(BigDecimal.ONE);
            ctrAlgorithmData.setSysCTRLevel(1);
            ctrAlgorithmData.setPubCTRLevel(1);
            ctrAlgorithmData.setSiteCTRLevel(1);
            ctrAlgorithmData.setTagCTRLevel(1);

            ctrAlgorithmData.setKwtgCTRDefault(BigDecimal.ONE);
            ctrAlgorithmData.setSysKwtgCTRLevel(1);
            ctrAlgorithmData.setKeywordCTRLevel(1);
            ctrAlgorithmData.setCcgkeywordKwCTRLevel(1);
            ctrAlgorithmData.setCcgkeywordTgCTRLevel(1);

            ctrAlgorithmData.setTowRaw(BigDecimal.ONE);
            ctrAlgorithmData.setSysTOWLevel(1);
            ctrAlgorithmData.setCampaignTOWLevel(1);
            ctrAlgorithmData.setTgTOWLevel(1);
            ctrAlgorithmData.setKeywordTOWLevel(1);

            ctrAlgorithmData.setCcgkeywordKwTOWLevel(1);
            ctrAlgorithmData.setCcgkeywordTgTOWLevel(1);

            ctrAlgorithmService.save(ctrAlgorithmData, new ArrayList<Long>(), new ArrayList<Long>());
        }
        */
    }

    public SiteCategory createSiteCategory(Country country) {
        SiteCategory category = new SiteCategory();

        category.setName(getTestEntityRandomName());
        category.setCountry(country);

        return category;
    }

    public SiteCategory createSiteCategoryPersistent(Country country) {
        if (country.getSiteCategories().isEmpty()) {
            SiteCategory category = createSiteCategory(country);
            country.getSiteCategories().add(category);
            countryService.update(country, null);
            entityManager.flush();
            for (SiteCategory sc : countryService.findSiteCategories(country)) {
                if (sc.getName().equals(category.getName())) {
                    return sc;
                }
            }
        } else {
            return country.getSiteCategories().toArray(new SiteCategory[country.getSiteCategories().size()])[0];
        }
        throw new RuntimeException("Failed to create site category");
    }

    public ContentCategory createContentCategory(Country country) {
        ContentCategory cc = new ContentCategory();

        cc.setName(getTestEntityRandomName());
        cc.setCountry(country);

        return cc;
    }

    public ContentCategory createContentCategoryPersistent(Country country) {
        ContentCategory category = createContentCategory(country);
        country.getContentCategories().add(category);
        countryService.update(country, null);
        for (ContentCategory cc: countryService.findContentCategories(country)) {
            if (cc.getName().equals(category.getName())) {
                return cc;
            }
        }
        throw new RuntimeException("Failed to create content category");
    }

    public Country find(String code) {
        return countryService.find(code);
    }
}
