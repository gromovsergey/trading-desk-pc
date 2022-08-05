package com.foros.session.admin.country;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.ApproveStatus;
import com.foros.model.AuditLogRecord;
import com.foros.model.Country;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.Channel;
import com.foros.model.channel.trigger.TriggerType;
import com.foros.model.security.ObjectType;
import com.foros.model.site.ContentCategory;
import com.foros.model.site.Site;
import com.foros.model.site.SiteCategory;
import com.foros.model.site.Tag;
import com.foros.service.mock.AdvertisingFinanceServiceMock;
import com.foros.session.account.AccountServiceBean;
import com.foros.session.security.auditLog.SearchAuditServiceBean;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.BehavioralChannelTestFactory;
import com.foros.test.factory.BehavioralParamsTestFactory;
import com.foros.test.factory.CountryTestFactory;
import com.foros.test.factory.SiteTestFactory;
import com.foros.test.factory.TagsTestFactory;
import com.foros.util.PersistenceUtils;
import com.foros.util.jpa.DetachedList;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import group.Db;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class CountryServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    public CountryService countryService;

    @Autowired
    public AccountServiceBean accountService;

    @Autowired
    public AdvertisingFinanceServiceMock financeService;

    @Autowired
    public SearchAuditServiceBean auditService;

    @Autowired
    public CountryTestFactory countryTestFactory;

    @Autowired
    private SiteTestFactory siteTF;

    @Autowired
    private TagsTestFactory tagTF;

    @Autowired
    private BehavioralChannelTestFactory behavioralChannelTF;

    @Autowired
    private BehavioralParamsTestFactory bparamsTF;

    @Autowired
    public AdvertiserAccountTestFactory advertiserAccountTestFactory;

    @Test
    public void testOnVATDisabled() {
        BigDecimal defaultVATRate = BigDecimal.valueOf(0.2);
        Country country = countryTestFactory.create();
        country.setVatEnabled(true);
        country.setDefaultVATRate(defaultVATRate);
        countryTestFactory.persist(country);

        AdvertiserAccount account = advertiserAccountTestFactory.create();
        account.setCountry(country);
        accountService.createExternalAccount(account);

        Country webFormCountry = countryTestFactory.createCopy(country);
        webFormCountry.setVatEnabled(false);
        webFormCountry.setVersion(country.getVersion());
        countryService.update(webFormCountry, null);
        commitChanges();

        BigDecimal updatedTaxRate = jdbcTemplate.queryForObject("select tax_rate from accountfinancialsettings where account_id = ?", BigDecimal.class, account.getId());
        assertTrue("Tax rate should be set to 0 on disabling VAT in country!", BigDecimal.ZERO.compareTo(updatedTaxRate) == 0);

        DetachedList<AuditLogRecord> records = auditService.getHistory(ObjectType.AdvertiserAccount, null, account.getId(), 0, 1);
        AuditLogRecord lastRecord = records.get(0);
        assertTrue("wrong last audit log record !\nRecord src:\n" + lastRecord.getActionDescription() +
                "\n", lastRecord.getActionDescription().contains("<property changeType=\"UPDATE\" name=\"taxRate\">0</property>"));
    }

    @Test
    public void testOnVATEnabled() {
        Country country = countryTestFactory.create();
        country.setVatEnabled(false);
        country.setDefaultVATRate(BigDecimal.ZERO);
        countryTestFactory.persist(country);

        AdvertiserAccount account = advertiserAccountTestFactory.create();
        account.setCountry(country);
        accountService.createExternalAccount(account);

        BigDecimal defaultVATRate = BigDecimal.ONE;
        Country webFormCountry = countryTestFactory.createCopy(country);
        webFormCountry.setVatEnabled(true);
        webFormCountry.setDefaultVATRate(defaultVATRate);
        webFormCountry.setVersion(country.getVersion());
        countryService.update(webFormCountry, null);
        commitChanges();

        BigDecimal updatedTaxRate = jdbcTemplate.queryForObject("select tax_rate from accountfinancialsettings where account_id = ?", BigDecimal.class, account.getId());
        assertTrue("Tax rate should be updated to default on enabling VAT in country!", defaultVATRate.compareTo(updatedTaxRate) == 0);

        AuditLogRecord lastRecord = auditService.getHistory(ObjectType.AdvertiserAccount, null, account.getId(), 0, 1).get(0);
        assertTrue("Wrong last audit log record !\nRecord src:\n" + lastRecord.getActionDescription() + "\n",
            lastRecord.getActionDescription().contains("<property changeType=\"UPDATE\" name=\"taxRate\">" + defaultVATRate + "</property>"));
    }

    @Test
    public void testOnVATUpdated() {
        BigDecimal oldDefaultVATRate = BigDecimal.ZERO;
        Country country = countryTestFactory.create();
        country.setVatEnabled(true);
        country.setDefaultVATRate(oldDefaultVATRate);
        countryTestFactory.persist(country);

        AdvertiserAccount account1 = advertiserAccountTestFactory.create();
        account1.setCountry(country);
        accountService.createExternalAccount(account1);

        AdvertiserAccount account2 = advertiserAccountTestFactory.create();
        account2.setCountry(country);
        accountService.createExternalAccount(account2);
        BigDecimal manualDefaultVATRate = BigDecimal.valueOf(0.5);
        account2.getFinancialSettings().setTaxRate(manualDefaultVATRate);
        financeService.updateFinance(account2.getFinancialSettings());

        commitChanges();

        BigDecimal newDefaultVATRate = BigDecimal.ONE;
        Country webFormCountry = countryTestFactory.createCopy(country);
        webFormCountry.setDefaultVATRate(newDefaultVATRate);
        webFormCountry.setVersion(country.getVersion());
        countryService.update(webFormCountry, null);

        commitChanges();

        BigDecimal taxRateAccount1 = jdbcTemplate.queryForObject("select tax_rate from accountfinancialsettings where account_id = ?", BigDecimal.class, account1.getId());
        assertTrue("Tax rate should be updated to default in accounts with old default tax rate on updating VAT rate in country!", newDefaultVATRate.compareTo(taxRateAccount1) == 0);

        AuditLogRecord lastRecord = auditService.getHistory(ObjectType.AdvertiserAccount, null, account1.getId(), 0, 1).get(0);
        assertTrue("wrong last audit log record for account1!\nRecord src:\n" + lastRecord.getActionDescription() +
                "\n", lastRecord.getActionDescription().contains("<property changeType=\"UPDATE\" name=\"taxRate\">" + newDefaultVATRate + "</property>"));

        BigDecimal taxRateAccount2 = jdbcTemplate.queryForObject("select tax_rate from accountfinancialsettings where account_id = ?", BigDecimal.class, account2.getId());
        assertTrue("Tax rate should not be updated to default in accounts with manually changed tax rate on updating VAT rate in country!", manualDefaultVATRate.compareTo(taxRateAccount2) == 0);
    }

    @Test
    public void testFindSiteAndContentCategories() {
        Country country = countryTestFactory.create();
        Set<ContentCategory> contentCategories = new HashSet<ContentCategory>();
        Set<SiteCategory> categories = new HashSet<SiteCategory>();

        categories.add(countryTestFactory.createSiteCategory(country));
        country.setSiteCategories(categories);

        contentCategories.add(countryTestFactory.createContentCategory(country));
        country.setContentCategories(contentCategories);

        countryTestFactory.persist(country);
        getEntityManager().clear();

        List<ContentCategory> ccList = countryService.findContentCategories(country);
        List<SiteCategory> scList = countryService.findSiteCategories(country);

        assertEquals(1, ccList.size());
        assertEquals(1, scList.size());
    }

    @Test
    public void testRemoveCategories() {
        Country country = createCountryWithSiteContentCategories();

        // get existing country
        Country existingCountry = countryService.find(country.getCountryCode());
        Country modifiedCountry = countryTestFactory.createCopy(existingCountry);
        Set<ContentCategory> newCClist = new HashSet<ContentCategory>();

        newCClist.addAll(existingCountry.getContentCategories());
        newCClist.remove(newCClist.toArray()[0]);

        Set<SiteCategory> newSCList = new HashSet<SiteCategory>();
        newSCList.addAll(existingCountry.getSiteCategories());
        newSCList.remove(newSCList.toArray()[1]);

        modifiedCountry.setContentCategories(newCClist);
        modifiedCountry.setSiteCategories(newSCList);
        modifiedCountry.setCountryCode(existingCountry.getCountryCode());
        modifiedCountry.setVersion(existingCountry.getVersion());
        modifiedCountry.getChanges().remove("countryCode");
        modifiedCountry.getChanges().remove("version");

        countryService.update(modifiedCountry, null);

        assertEquals(1, countryService.findContentCategories(modifiedCountry).size());
        assertEquals(1, countryService.findSiteCategories(modifiedCountry).size());
    }

    @Test
    public void testRemoveLinkedSiteCategory() {
        Site site = siteTF.createPersistent();
        getEntityManager().flush();
        Country existingCountry = countryService.findForEdit(site.getAccount().getCountry().getCountryCode());
        Country toBeChangedCountry = countryTestFactory.createCopy(existingCountry);

        toBeChangedCountry.setSiteCategories(new HashSet<SiteCategory>());
        toBeChangedCountry.setVersion(existingCountry.getVersion());

        try {
            countryService.update(toBeChangedCountry, null);
            fail("Must not fail");
        } catch (ConstraintViolationException e) {
            // expected
        }
    }

    @Test
    public void testRemoveLinkedContentCategory() {
        Site site = siteTF.createPersistent();
        Tag tag = tagTF.createPersistent(site);
        Country existingCountry = countryService.findForEdit(tag.getSite().getAccount().getCountry().getCountryCode());

        Set<ContentCategory> contentCategories = new HashSet<ContentCategory>();
        contentCategories.add(countryTestFactory.createContentCategoryPersistent(site.getAccount().getCountry()));
        contentCategories.add(countryTestFactory.createContentCategoryPersistent(site.getAccount().getCountry()));
        tag.setContentCategories(contentCategories);

        Country toBeChangedCountry = countryTestFactory.createCopy(existingCountry);

        toBeChangedCountry.setContentCategories(new HashSet<ContentCategory>());
        toBeChangedCountry.setVersion(existingCountry.getVersion());

        try {
            countryService.update(toBeChangedCountry, null);
            fail("Must not fail");
        } catch (ConstraintViolationException e) {
            // expected
        }
    }

    @Test
    public void testAddRemoveSameCategory() {
        Country country = createCountryWithSiteContentCategories();

        Country existingCountry = getEntityManager().find(Country.class, (country.getCountryCode()));
        Country modifiedCountry = countryTestFactory.createCopy(existingCountry);

        // create a detached country entity
        modifiedCountry.setVersion(existingCountry.getVersion());
        modifiedCountry.getChanges().clear();

        // change content categories of the country
        Set<ContentCategory> newCCList = new HashSet<ContentCategory>();

        newCCList.addAll(existingCountry.getContentCategories());
        ContentCategory toBeRemoved = (ContentCategory)newCCList.toArray()[0];
        ContentCategory newCC = countryTestFactory.createContentCategory(country);

        newCC.setCountry(toBeRemoved.getCountry());
        newCC.setName(toBeRemoved.getName());
        newCCList.remove(toBeRemoved);
        newCCList.add(newCC);
        modifiedCountry.setContentCategories(newCCList);

        Logger hibernateLogger = Logger.getLogger("org.hibernate");
        Level defaultLevel = hibernateLogger.getLevel();
        hibernateLogger.setLevel(Level.OFF);
        try {
            countryService.update(modifiedCountry, null);
        }
        finally {
            hibernateLogger.setLevel(defaultLevel);
        }
        getEntityManager().flush();
    }

    @Test
    public void testUrlTriggerThresholdChange() {
        AdvertiserAccount account = advertiserAccountTestFactory.create();
        account.setInternational(true);
        advertiserAccountTestFactory.persist(account);

        Country country = countryTestFactory.createPersistent();

        behavioralChannelTF.initChannelTriggersHibernateHandler();
        BehavioralChannel channel0 = createChannelWithUrls(account, country, 0);
        BehavioralChannel channel1 = createChannelWithUrls(account, country, 1);
        BehavioralChannel channel100 = createChannelWithUrls(account, country, 100);

        behavioralChannelTF.executeChannelTriggersHibernateHandler();
        entityManager.flush();


        assertDeclined(channel0, false);
        assertDeclined(channel1, false);
        assertDeclined(channel100, false);

        entityManager.clear();

        country.setMinUrlTriggerThreshold(50L);
        country = countryService.update(country, null);
        PersistenceUtils.initialize(country.getAddressFields());
        entityManager.flush();
        entityManager.clear();

        assertDeclined(channel0, false);
        assertDeclined(channel1, true);
        assertDeclined(channel100, false);

        country.setMinUrlTriggerThreshold(101L);
        countryService.update(country, null);
        entityManager.flush();
        entityManager.clear();

        assertDeclined(channel0, false);
        assertDeclined(channel1, true);
        assertDeclined(channel100, true);
    }

    @Test
    public void testIncorrectLanguageCode() {
        Country country = countryTestFactory.createPersistent();
        country.setLanguage("99");
        try {
            countryService.update(country, null);
            fail("IllegalArgumentException should've been thrown");
        } catch (Exception e) {
            // it's ok
        }
    }

    private Country createCountryWithSiteContentCategories() {
        Country country = countryTestFactory.create();
        Set<ContentCategory> contentCategories = new HashSet<ContentCategory>();
        Set<SiteCategory> siteCategories = new HashSet<SiteCategory>();
        // create content categories and set to country
        contentCategories.add(countryTestFactory.createContentCategory(country));
        contentCategories.add(countryTestFactory.createContentCategory(country));
        country.setContentCategories(contentCategories);
        // create site categories and set to country
        siteCategories.add(countryTestFactory.createSiteCategory(country));
        siteCategories.add(countryTestFactory.createSiteCategory(country));
        country.setSiteCategories(siteCategories);

        countryTestFactory.persist(country);
        return country;
    }

    private BehavioralChannel createChannelWithUrls(AdvertiserAccount account, Country country, int count) {
        BehavioralChannel ch = behavioralChannelTF.create(account);
        String[] urls = new String[count];
        for (int i = 0; i < count; i++) {
            urls[i] = "domain" + i + ".com/" + i;
        }
        ch.getUrls().setPositive(urls);
        ch.setCountry(country);

        if (count > 0) {
            BehavioralParameters bp = bparamsTF.createBParam(TriggerType.URL);
            ch.setBehavioralParameters(Collections.singleton(bp));
        }

        behavioralChannelTF.persist(ch);
        return ch;
    }

    private void assertDeclined(Channel ch, boolean flag) {
        @SuppressWarnings({"unchecked"})
        Object[] res = (Object[]) entityManager
                .createQuery("select ch.qaStatus, ch.displayStatusId from Channel ch where ch.id = :id")
                .setParameter("id", ch.getId())
                .getSingleResult();
        Character c = (Character) res[0];
        Long displayStatusId = (Long) res[1];
        ApproveStatus status = ApproveStatus.valueOf(c);
        assertEquals(flag, status == ApproveStatus.DECLINED);
        assertEquals(flag, displayStatusId.equals(Channel.DECLINED.getId()));
    }
}
