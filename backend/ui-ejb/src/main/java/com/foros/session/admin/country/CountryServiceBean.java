package com.foros.session.admin.country;

import com.foros.cache.application.CountryCO;
import com.foros.changes.CaptureChangesInterceptor;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.AddressField;
import com.foros.model.Country;
import com.foros.model.Identifiable;
import com.foros.model.Timezone;
import com.foros.model.account.AccountFinancialSettings;
import com.foros.model.account.ExternalAccount;
import com.foros.model.channel.Channel;
import com.foros.model.currency.Currency;
import com.foros.model.security.ActionType;
import com.foros.model.security.Language;
import com.foros.model.site.CategoryTO;
import com.foros.model.site.ContentCategory;
import com.foros.model.site.Site;
import com.foros.model.site.SiteCategory;
import com.foros.model.site.Tag;
import com.foros.model.template.CreativeToken;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.birt.BirtReportService;
import com.foros.session.cache.CacheService;
import com.foros.session.fileman.FileManagerException;
import com.foros.session.fileman.PathProviderService;
import com.foros.session.security.AuditService;
import com.foros.util.IdentifiableCollectionTransformer;
import com.foros.util.PersistenceUtils;
import com.foros.util.SQLUtil;
import com.foros.util.Schema;
import com.foros.util.UrlUtil;
import com.foros.util.templates.Template;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;

@Stateless(name = "CountryService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class CountryServiceBean implements CountryService {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private PathProviderService pathProviderService;

    @EJB
    private CacheService cacheService;

    @EJB
    private AuditService auditService;

    @EJB
    private ConfigService configService;

    @EJB
    private BirtReportService reportService;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @Override
    public Collection<CountryCO> getIndex() {
        //noinspection unchecked
        return em.createNamedQuery("Country.findAll").getResultList();
    }

    @Override
    public Collection<CountryCO> getIndex(Collection<Long> accountIds) {
        Query query = em.createQuery("SELECT NEW com.foros.cache.application.CountryCO" +
                "(c.countryCode, c.sortOrder, cu.id, cu.currencyCode, t.id, t.key, c.countryId)" +
                " FROM Country c LEFT JOIN c.currency cu LEFT JOIN c.timezone t" +
                " WHERE (c IN (SELECT a.country FROM Account a WHERE a.id IN (:accountIds)))");
        query.setParameter("accountIds", accountIds);
        //noinspection unchecked
        return query.getResultList();
    }

    private void prePersist(Country entity) {
        checkLanguage(entity);
        entity.setCurrency(em.getReference(Currency.class, entity.getCurrency().getId()));
        entity.setTimezone(em.getReference(Timezone.class, entity.getTimezone().getId()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ContentCategory> findContentCategories(Set<ContentCategory> contentCategories) {
        Collection ids = new IdentifiableCollectionTransformer().transform(contentCategories);
        return em.createQuery("SELECT c FROM ContentCategory c WHERE " + SQLUtil.formatINClause("c.id", ids))
                .getResultList();
    }

    @Override
    @Restrict(restriction = "Country.update")
    @Validate(validation = "Country.update", parameters = "#country")
    @Interceptors({CaptureChangesInterceptor.class})
    public Country update(Country country, File invoiceRptFile) {
        List<AddressField> transferredAddressFields = country.getAddressFields();
        prePersist(country);
        Country persistentCountry = find(country.getCountryCode());

        boolean checkMinUrlTriggerThreshold = country.isChanged("minUrlTriggerThreshold") &&
                !country.getMinUrlTriggerThreshold().equals(persistentCountry.getMinUrlTriggerThreshold());

        // check for object dependency on deleted categories
        Collection<ContentCategory> contentCategoriesToRemove = CollectionUtils.subtract(persistentCountry.getContentCategories(), country.getContentCategories());
        checkRemoveContentCategory(contentCategoriesToRemove, country);
        Collection<SiteCategory> siteCategoriesToRemove = CollectionUtils.subtract(persistentCountry.getSiteCategories(), country.getSiteCategories());
        checkRemoveSiteCategory(siteCategoriesToRemove, country);
        //remove deleted content categories from all existing tags
        IdentifiableCollectionTransformer<Identifiable> idTransformer = new IdentifiableCollectionTransformer<Identifiable>();
        if (!contentCategoriesToRemove.isEmpty()) {
            jdbcTemplate.execute(
                    "delete from tagcontentcategory where content_category_id = any(?)",
                    jdbcTemplate.createArray("int", idTransformer.transform(contentCategoriesToRemove))
            );
        }
        cacheService.evictRegion(Tag.class);

        //remove deleted site categories from all existing sites
        if (siteCategoriesToRemove.isEmpty()) {
            jdbcTemplate.execute(
                    "update site set site_category_id=null where site_category_id = any(?)",
                    jdbcTemplate.createArray("int", idTransformer.transform(siteCategoriesToRemove))
            );
        }
        cacheService.evictRegion(Site.class);

        prePersistSiteCategory(persistentCountry, country);
        prePersistContentCategory(persistentCountry, country);

        handleVATOptionsUpdate(persistentCountry, country);

        country = em.merge(country);

        if (updateAddressFields(transferredAddressFields)) {
            PersistenceUtils.performHibernateLock(em, country);
        }

        auditService.audit(country, ActionType.UPDATE);
        em.flush();

        if (checkMinUrlTriggerThreshold) {
            jdbcTemplate.execute("select trigger.bulk_check_url_trigger_threshold(?)", country.getCountryCode());
            cacheService.evictRegion(Channel.class);
        }

        if (invoiceRptFile != null) {
            reportService.updateInvoiceReport(
                    country.getCountryCode(), createInputStream(invoiceRptFile));
        }

        return country;
    }

    private FileInputStream createInputStream(File invoiceRptFile) throws FileManagerException {
        try {
            return new FileInputStream(invoiceRptFile);
        } catch (FileNotFoundException e) {
            throw new FileManagerException(e);
        }
    }

    private void prePersistSiteCategory(Country existingCountry, Country newCountry) {
        Set<SiteCategory> newSiteCategories = newCountry.getSiteCategories();
        List<SiteCategory> existingSiteCategories = new ArrayList<SiteCategory>(existingCountry.getSiteCategories());

        for (SiteCategory siteCategory : newSiteCategories.toArray(new SiteCategory[newSiteCategories.size()])) {
            if (siteCategory.getId() == null && existingSiteCategories.contains(siteCategory)) {
                // remove if its the case of removeAdd same category
                newSiteCategories.remove(siteCategory);
                newSiteCategories.add(existingSiteCategories.get(existingSiteCategories.indexOf(siteCategory)));
            }
        }
    }

    private void prePersistContentCategory(Country existingCountry, Country newCountry) {
        Set<ContentCategory> newContentCategories = newCountry.getContentCategories();
        List<ContentCategory> existingContentCategories = new ArrayList<ContentCategory>(existingCountry.getContentCategories());

        for (ContentCategory contentCategory : newContentCategories.toArray(new ContentCategory[newContentCategories.size()])) {
            if (contentCategory.getId() == null && existingContentCategories.contains(contentCategory)) {
                // remove if its the case of removeAdd same category
                newContentCategories.remove(contentCategory);
                newContentCategories.add(existingContentCategories.get(existingContentCategories.indexOf(contentCategory)));
            }
        }
    }

    private void handleVATOptionsUpdate(Country persistentCountry, Country freshCountry) {
        boolean vatFlagUpdated = freshCountry.isVatEnabled() != persistentCountry.isVatEnabled();
        boolean vatWasEnabled = vatFlagUpdated && freshCountry.isVatEnabled();
        boolean vatWasDisabled = vatFlagUpdated && !freshCountry.isVatEnabled();
        boolean vatRateUpdated = !vatFlagUpdated && freshCountry.isVatEnabled() && !freshCountry.getDefaultVATRate().equals(persistentCountry.getDefaultVATRate());
        long accountsUpdated = 0;
        if (vatWasEnabled) {
            accountsUpdated = onVatEnabled(persistentCountry, freshCountry.getDefaultVATRate());
        } else if (vatWasDisabled) {
            accountsUpdated = onVatDisabled(persistentCountry);
        } else if (vatRateUpdated) {
            accountsUpdated = onVatRateUpdated(persistentCountry, freshCountry.getDefaultVATRate());
        }
        if (accountsUpdated > 0) {
            cacheService.evictRegionNonTransactional(AccountFinancialSettings.class);
            cacheService.evictRegionNonTransactional(ExternalAccount.class);
        }
    }

    private long onVatDisabled(Country country) {
        return jdbcTemplate.queryForObject(
                "select * from country.handle_vat_flag_update(?::varchar,?::numeric)",
                Long.class,
                country.getCountryCode(), BigDecimal.ZERO
        );
    }

    private long onVatEnabled(Country country, BigDecimal defaultVATRate) {
        return jdbcTemplate.queryForObject(
                "select * from country.handle_vat_flag_update(?::varchar,?::numeric)",
                Long.class,
                country.getCountryCode(), defaultVATRate
        );
    }

    private long onVatRateUpdated(Country country, BigDecimal newDefaultVATRate) {
        return jdbcTemplate.queryForObject(
                "select * from country.handle_vat_rate_update(?::varchar,?::numeric,?::numeric)",
                Long.class,
                country.getCountryCode(), country.getDefaultVATRate(), newDefaultVATRate
        );
    }

    private boolean updateAddressFields(Collection<AddressField> addressFields) {
        boolean isUpdated = false;

        for (AddressField transferField : addressFields) {
            AddressField existingField = em.find(AddressField.class, transferField.getId());

            isUpdated |= addressFieldChanged(existingField, transferField);

            if (!existingField.getOFFieldName().equals(PredefinedAddressField.COUNTRY.getName())) {
                existingField.setName(transferField.getName());
                if (!existingField.getOFFieldName().equals(PredefinedAddressField.LINE1.getName())) {
                    existingField.setFlags(transferField.getFlags());
                }
            }
            existingField.setOrderNumber(transferField.getOrderNumber());
            em.merge(existingField);
        }

        return isUpdated;
    }

    private boolean addressFieldChanged(AddressField oldField, AddressField newField) {
        if (oldField.getOFFieldName().equals(PredefinedAddressField.COUNTRY.getName())) {
            return oldField.getOrderNumber() != newField.getOrderNumber();
        }

        return oldField.getFlags() != newField.getFlags() ||
               !oldField.getName().equals(newField.getName()) ||
               oldField.getOrderNumber() != newField.getOrderNumber();
    }

    @Override
    public Country find(String countryCode) {
        Country res = em.find(Country.class, countryCode);
        if (res == null) {
            throw new EntityNotFoundException("Country with code = " + countryCode + " not found");
        }
        res.getAddressFields().size();
        res.getContentCategories().size();
        res.getSiteCategories().size();
        return res;
    }

    @Override
    public void refresh(String countryCode) {
        em.refresh(em.find(Country.class, countryCode));
    }

    @Override
    public List<AddressField> getAddressFields(String countryCode) {
        Country country = find(countryCode);
        country.getAddressFields().size();
        return country.getAddressFields();
    }

    @Override
    @Restrict(restriction = "Country.update")
    public Country findForEdit(String countryCode) {
        return find(countryCode);
    }

    @Override
    public Country findByCountryId(Long id) {
        Country country;
        if (id == null) {
            throw new EntityNotFoundException("Country with id = null not found");
        }

        Query q = em.createNamedQuery("Country.findByCountryId");
        q.setParameter("countryId", id);
        try {
            country = (Country) q.getSingleResult();
        } catch (NoResultException e) {
            country = null;
        }

        if (country == null) {
            throw new EntityNotFoundException("Country with id = " + id + " not found");
        }

        return country;
    }

    @Override
    @Restrict(restriction = "Country.view")
    public Collection<CountryCO> search() {
        return getIndex();
    }

    @Override
    public List<CategoryTO> findForEditContentCategories(Country country) {
        String sql = "select cc.content_category_id, cc.name, cc.version, count(tc.tag_id) " +
                " from contentcategory cc left join tagcontentcategory tc on tc.content_category_id = cc.content_category_id " +
                " left join tags t on t.tag_id = tc.tag_id and t.status !='D' " + " where cc.country_code = :country " +
                " group by cc.content_category_id, cc.name, cc.version  order by upper(cc.name)";
        Query query = em.createNativeQuery(sql)
                .setParameter("country", country.getCountryCode());
        //noinspection unchecked
        List<Object[]> resultRows = query.getResultList();

        return buildCategoryTOList(resultRows);
    }

    @Override
    public List<ContentCategory> findContentCategories(Country country) {
        //noinspection unchecked
        return em.createNamedQuery("ContentCategory.findByCountry")
                .setParameter("country", country)
                .getResultList();
    }

    @Override
    public List<CategoryTO> findForEditSiteCategories(Country country) {
        String sql = "select sc.site_category_id, sc.name, sc.version, count(site_id) " +
                " from sitecategory sc left join site s on s.site_category_id = sc.site_category_id and s.status !='D' " +
                " where sc.country_code=:country group by sc.site_category_id, sc.name, sc.version order by upper(sc.name)";
        Query query = em.createNativeQuery(sql)
                .setParameter("country", country.getCountryCode());
        //noinspection unchecked
        List<Object[]> resultRows = query.getResultList();

        return buildCategoryTOList(resultRows);
    }

    @Override
    public List<SiteCategory> findSiteCategories(Country country) {
        //noinspection unchecked
        return em.createNamedQuery("SiteCategory.findByCountry")
                .setParameter("country", country)
                .getResultList();
    }

    @Override
    public ContentCategory findContentCategory(Long id) {
        return em.find(ContentCategory.class, id);
    }

    @Override
    public String getConversionTrackingPixelCode(Country country, Long accountId, Long actionId) {
        String templateText = configService.get(ConfigParameters.CONVERSION_TRACKING_PIXEL_CODE);
        return getPixelTemplate(templateText, country, accountId, actionId);
    }

    @Override
    public String getImagePixel(Country country, Long accountId, Long actionId) {
        String templateText = configService.get(ConfigParameters.CONVERSION_IMAGE_PIXEL);
        return getPixelTemplate(templateText, country, accountId, actionId);
    }

    @Override
    public String getConversionTrackingNoAudiencePixelCode(Country country, Long accountId, Long actionId){
        String templateText = configService.get(ConfigParameters.CONVERSION_TRACKING_NO_AUDIENCE_PIXEL_CODE);
        return getPixelTemplate(templateText, country, accountId, actionId);
    }

    private String getPixelTemplate(String templateText, Country country, Long accountId, Long actionId) {
        Template template = new Template(templateText);
        template.add(CreativeToken.CONVERSION_ID.getName(), actionId.toString());

        String conversionTagDomain = country.getConversionTagDomain();
        if (conversionTagDomain == null) {
            conversionTagDomain = "";
        }
        String conversionDomain = Schema.DEFAULT.getValue() + UrlUtil.stripSchema(conversionTagDomain);
        template.add(CreativeToken.CONVERSION_DOMAIN.getName(), conversionDomain);

        template.add(CreativeToken.ADVID.getName(), accountId.toString());
        return template.generate();
    }

    private void checkRemoveSiteCategory(Collection<SiteCategory> categories, Country country) {
        if (categories.isEmpty()) {
            return;
        }

        Query countQuery = em.createQuery(
                "SELECT COUNT(sc) FROM Site s join s.siteCategory AS sc WHERE sc.country = :country AND sc IN (:sc) AND s.status != 'D' ")
                .setParameter("country", country).setParameter("sc", categories);

        if ((Long)countQuery.getSingleResult() == 0L) {
            return;
        }

        // there categories having dependent objects, throw exception
        throw ConstraintViolationException.newBuilder("Country.category.inuse")
                .withParameters("{Country.site.category}")
                .build();
    }

    private void checkRemoveContentCategory(Collection<ContentCategory> toRemove, Country country) {
        if (toRemove.isEmpty()) {
            return;
        }

        Query countQuerry = em.createQuery(
                "SELECT count(tcc) FROM Tag t join t.contentCategories AS tcc WHERE  tcc.country = :country AND tcc IN (:tcc)  AND t.status != 'D' ")
                .setParameter("country", country).setParameter("tcc", toRemove);

        if ((Long)countQuerry.getSingleResult() == 0L) {
            return;
        }

        // there categories having dependent objects, throw exception
        throw ConstraintViolationException.newBuilder("Country.category.inuse")
                .withParameters("{Country.content.category}")
                .build();
    }

    private void checkLanguage(Country country) {
        if (country.isChanged("language")) {
            Language.valueOfCode(country.getLanguage());
        }
    }

    private List<CategoryTO> buildCategoryTOList(List<Object[]> objectList) {
        List<CategoryTO> result = new ArrayList<>();
        // build objects
        for (Object[] row : objectList) {
            Long id = ((Number)row[0]).longValue();
            String name = (String)row[1];
            Timestamp version = (Timestamp)row[2];
            int linkCount = ((Number)row[3]).intValue();

            result.add(new CategoryTO(id, name, version, (linkCount > 0)));
        }

        return result;
    }
}
