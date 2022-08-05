package com.foros.session.site;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.ApproveStatus;
import com.foros.model.Country;
import com.foros.model.FrequencyCap;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.MarketplaceType;
import com.foros.model.account.PublisherAccount;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.RateType;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.SizeType;
import com.foros.model.security.AccountType;
import com.foros.model.security.ActionType;
import com.foros.model.security.OwnedEntity;
import com.foros.model.security.User;
import com.foros.model.site.CategoryExclusionApproval;
import com.foros.model.site.PassbackType;
import com.foros.model.site.Site;
import com.foros.model.site.SiteCategory;
import com.foros.model.site.SiteCreativeCategoryExclusion;
import com.foros.model.site.SiteCreativeCategoryExclusionPK;
import com.foros.model.site.SiteRate;
import com.foros.model.site.SiteRateType;
import com.foros.model.site.Tag;
import com.foros.model.site.TagPricing;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.principal.SecurityContext;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.TooManyRowsException;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.session.admin.walledGarden.WalledGardenService;
import com.foros.session.bulk.Result;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.frequencyCap.FrequencyCapMerger;
import com.foros.session.query.PartialList;
import com.foros.session.query.QueryExecutorService;
import com.foros.session.query.site.SiteQueryImpl;
import com.foros.session.security.AuditService;
import com.foros.session.security.UserService;
import com.foros.session.status.ApprovalService;
import com.foros.session.status.DisplayStatusService;
import com.foros.util.CollectionUtils;
import com.foros.util.ConditionStringBuilder;
import com.foros.util.EntityUtils;
import com.foros.util.PersistenceUtils;
import com.foros.util.bean.Filter;
import com.foros.util.comparator.StatusNameTOComparator;
import com.foros.util.jpa.JpaQueryWrapper;
import com.foros.util.jpa.QueryWrapper;
import com.foros.util.mapper.Converter;
import com.foros.util.posgress.PGArray;
import com.foros.util.posgress.PGRow;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.ValidationService;
import com.foros.validation.annotation.Validate;
import com.foros.validation.constraint.violation.ConstraintViolation;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.FlushMode;
import org.springframework.jdbc.core.RowCallbackHandler;

@Stateless(name = "SiteService")
@Interceptors({ RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class })
public class SiteServiceBean implements SiteService {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private AuditService auditService;

    @EJB
    private ApprovalService approvalService;

    @EJB
    private TagsService tagsService;

    @EJB
    private UserService userService;

    @EJB
    protected DisplayStatusService displayStatusService;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private ValidationService validationService;

    @EJB
    private WalledGardenService wgService;

    @EJB
    private CreativeSizeService creativeSizeService;

    @EJB
    private QueryExecutorService executorService;

    private FrequencyCapMerger<Site> frequencyCapMerger = new FrequencyCapMerger<Site>() {
        @Override
        protected EntityManager getEm() {
            return em;
        }
    };

    @EJB
    private PublisherEntityRestrictions publisherEntityRestrictions;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    private static final Logger logger = Logger.getLogger(SiteServiceBean.class.getName());

    public SiteServiceBean() {
    }

    private void prePersist(Site site) {
        PublisherAccount siteAccount = em.find(PublisherAccount.class, site.getAccount().getId());
        site.setAccount(siteAccount);

        AccountType accountType = siteAccount.getAccountType();

        if (accountType.isAdvExclusionFlag()) {
            if (site.isChanged("categoryExclusions") && site.getCategoryExclusions() != null) {
                Set<SiteCreativeCategoryExclusion> newCategoryExclusions = new LinkedHashSet<>();

                for (SiteCreativeCategoryExclusion categoryExclusion : site.getCategoryExclusions()) {
                    if (CategoryExclusionApproval.ACCEPT != categoryExclusion.getApproval()) {

                        SiteCreativeCategoryExclusion existingCategoryExclusion = null;
                        if (categoryExclusion.getId() != null) {
                            existingCategoryExclusion = em.find(SiteCreativeCategoryExclusion.class, categoryExclusion.getId());
                        }

                        if (existingCategoryExclusion != null) {
                            existingCategoryExclusion.setApproval(categoryExclusion.getApproval());
                            newCategoryExclusions.add(existingCategoryExclusion);
                        } else {
                            categoryExclusion.setSite(site);
                            if (categoryExclusion.getCreativeCategory().getId() != null) {
                                CreativeCategory existingCreativeCategory = em.find(CreativeCategory.class, categoryExclusion.getCreativeCategory().getId());
                                categoryExclusion.setCreativeCategory(existingCreativeCategory);
                            }
                            newCategoryExclusions.add(categoryExclusion);
                        }

                    }
                }

                site.setCategoryExclusions(newCategoryExclusions);
            }
        } else {
            site.setCategoryExclusions(new LinkedHashSet<SiteCreativeCategoryExclusion>());
        }

        if (accountType.isFreqCapsFlag()) {
            // Remove empty freq caps
            FrequencyCap cap = site.getFrequencyCap();
            if (cap != null && cap.isEmpty()) {
                site.setFrequencyCap(null);
            }
        } else {
            site.setFrequencyCap(null);
            site.setNoAdsTimeout(0L);
        }

        if (site.isChanged("siteCategory") && site.getSiteCategory() != null) {
            site.setSiteCategory(em.find(SiteCategory.class, site.getSiteCategory().getId()));
        }
    }

    @Override
    @Restrict(restriction = "PublisherEntity.create", parameters = "find('Account', #site.account.id)")
    @Validate(validation = "Site.create", parameters = "#site")
    @Interceptors(CaptureChangesInterceptor.class)
    public Long create(Site site) {
        site.setStatus(Status.ACTIVE);
        if (publisherEntityRestrictions.canApprove()) {
            site.setQaStatus(ApproveStatus.APPROVED);
            site.setDisplayStatus(Site.NO_ACTIVE_TAGS);
        } else {
            site.setDisplayStatus(Site.PENDING_FOROS);
        }

        prePersist(site);
        if (site.getNoAdsTimeout() == null) {
            site.setNoAdsTimeout(0L);
        }
        Set<SiteCreativeCategoryExclusion> categories = site.getCategoryExclusions();
        site.setCategoryExclusions(new LinkedHashSet<SiteCreativeCategoryExclusion>());

        em.persist(site);

        if (!categories.isEmpty()) {
            em.flush();
            for (SiteCreativeCategoryExclusion category : categories) {
                if (category.getCreativeCategory().getId() == null) {
                    category.setSite(site);
                    if (category.getCreativeCategory().getType() == CreativeCategoryType.TAG) {
                        if (SecurityContext.isInternal()) {
                            category.getCreativeCategory().setQaStatus('A');
                        }
                    }
                    em.persist(category.getCreativeCategory());
                    auditService.audit(category.getCreativeCategory(), ActionType.CREATE);
                }

                SiteCreativeCategoryExclusionPK pk = new SiteCreativeCategoryExclusionPK(category.getCreativeCategory().getId(), site.getId());

                category.setSiteCreativeCategoryExclusionPK(pk);
                category.setSite(site);
                em.persist(category);
            }
        }
        auditService.audit(site, ActionType.CREATE);

        displayStatusService.update(site);

        site.getAccount().getSites().add(site);

        if (currentUserService.isSiteLevelRestricted()) {
            User currentUser = userService.getMyUser();
            Set<Site> sites = currentUser.getSites();
            sites.add(site);
            currentUser.setSites(sites);
        }

        return site.getId();
    }

    @Override
    @Restrict(restriction = "PublisherEntity.update", parameters = "find('Site', #site.id)")
    @Validate(validation = "Site.update", parameters = "#site")
    @Interceptors(CaptureChangesInterceptor.class)
    public void update(Site site) {
        site.unregisterChange("id", "account", "tags");
        Site existingSite = find(site.getId());

        prePersist(site);

        boolean isChanged = !ObjectUtils.equals(site.getSiteUrl(), existingSite.getSiteUrl());

        if (isChanged) {
            approvalService.makePendingOnChange(existingSite);
        }

        Set<SiteCreativeCategoryExclusion> existingExclusions = existingSite.getCategoryExclusions();
        Set<SiteCreativeCategoryExclusion> newExclusions = site.getCategoryExclusions();

        if (existingExclusions != null) {
            List<SiteCreativeCategoryExclusion> deletedExclusions =
                    new ArrayList<>(existingExclusions.size());

            for (SiteCreativeCategoryExclusion existingCategoryExclusion : existingExclusions) {
                if (newExclusions == null || !newExclusions.contains(existingCategoryExclusion)) {
                    em.remove(existingCategoryExclusion);
                    deletedExclusions.add(existingCategoryExclusion);
                }
            }
            existingExclusions.removeAll(deletedExclusions);
        }

        if (newExclusions != null) {
            for (SiteCreativeCategoryExclusion categoryExclusion : newExclusions) {
                if (categoryExclusion.getSiteCreativeCategoryExclusionPK() == null) {
                    categoryExclusion.setSite(site);
                    if (categoryExclusion.getCreativeCategory().getType() == CreativeCategoryType.TAG) {
                        if (SecurityContext.isInternal()) {
                            categoryExclusion.getCreativeCategory().setQaStatus('A');
                        }
                    }
                    em.persist(categoryExclusion.getCreativeCategory());
                    auditService.audit(categoryExclusion.getCreativeCategory(), ActionType.CREATE);

                    SiteCreativeCategoryExclusionPK pk =
                            new SiteCreativeCategoryExclusionPK(categoryExclusion.getCreativeCategory().getId(), site.getId());
                    categoryExclusion.setSiteCreativeCategoryExclusionPK(pk);
                    em.persist(categoryExclusion);

                    continue;
                }

                if (categoryExclusion.getCreativeCategory().getType() == CreativeCategoryType.TAG) {
                    CreativeCategory category = categoryExclusion.getCreativeCategory();
                    CreativeCategory managedCategory = em.getReference(CreativeCategory.class, category.getId());
                    if (SecurityContext.isInternal()) {
                        managedCategory.setQaStatus('A');
                    }
                }
            }
        }

        auditService.audit(existingSite, ActionType.UPDATE);

        frequencyCapMerger.merge(site, existingSite);

        em.merge(site);
        displayStatusService.update(existingSite);
        em.flush();
    }

    @Override
    @Restrict(restriction = "PublisherEntity.delete", parameters = "find('Site', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void delete(Long id) {
        Site site = find(id);
        site.setStatus(Status.DELETED);

        auditService.audit(site, ActionType.UPDATE);
        displayStatusService.update(site);
    }

    @Override
    @Restrict(restriction = "PublisherEntity.undelete", parameters = "find('Site', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void undelete(Long id) {
        Site site = find(id);
        site.setStatus(Status.ACTIVE);

        auditService.audit(site, ActionType.UPDATE);
        displayStatusService.update(site);
    }

    @Override
    @Restrict(restriction = "PublisherEntity.approve", parameters = "find('Site', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void approve(Long id) {
        approvalService.approve(find(id));
    }

    @Override
    @Restrict(restriction = "PublisherEntity.decline", parameters = "find('Site', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void decline(Long id, String dsc) {
        approvalService.decline(find(id), dsc);
    }

    @Override
    @Restrict(restriction = "PublisherEntity.view", parameters = "find('Site', #id)")
    public Site view(Long id) {
        Site site = em.find(Site.class, id);
        if (site == null) {
            throw new EntityNotFoundException("Site with id=" + id + " not found");
        }
        return site;
    }

    @Override
    public Site find(Long id) {
        if (id == null) {
            throw new EntityNotFoundException("Site with id = null not found");
        }
        Site site = em.find(Site.class, id);
        if (site == null) {
            throw new EntityNotFoundException("Site with id=" + id + " not found");
        }
        return site;
    }

    @Override
    @Restrict(restriction = "Entity.view", parameters = "find('Account', #accountId)")
    public List<EntityTO> getIndex(Long accountId) {
        return formatSqlForSearch(accountId).setParameter("accountId", accountId).getResultList();
    }

    @Override
    public Collection<CreativeSize> getAccountSizes(Long accountId) {
        return em.find(Account.class, accountId).getAccountType().getCreativeSizes();
    }

    private TypedQuery<EntityTO> formatSqlForSearch(Long accountId) {
        StringBuilder query = new StringBuilder("SELECT ");

        query.append("NEW com.foros.session.EntityTO(s.id, s.name, s.status )");
        query.append(" FROM Site s WHERE s.name = s.name ");
        if (accountId != null) {
            query.append(" and s.account.id=:accountId");
        }

        User currentUser = userService.getMyUser();
        if (currentUser.getRole().isAdvertiserAccountManager() ||
                currentUser.getRole().isPublisherAccountManager() ||
                currentUser.getRole().isISPAccountManager()) {
            query.append(" and s.account.accountManager.id = ").append(currentUser.getId());
        }
        if (!currentUser.isDeletedObjectsVisible()) {
            query.append(" and s.status <> '").append(Status.DELETED.getLetter()).append("'");
        }
        if (currentUser.isSiteLevelAccessFlag()) {
            query.append(" and exists (from User u where u.id=").append(currentUser.getId())
                .append(" and s in elements(u.sites))");
        }
        query.append(" ORDER BY s.name");

        TypedQuery<EntityTO> q = em.createQuery(query.toString(), EntityTO.class);

        if (accountId != null) {
            q.setParameter("accountId", accountId);
        }

        return q;
    }

    @Override
    @Restrict(restriction = "PublisherEntity.view", parameters = "find('PublisherAccount', #accountId)")
    public List<EntityTO> search(Long accountId) {
        return formatSqlForSearch(accountId).getResultList();
    }

    @Override
    public List<EntityTO> searchByName(String name, int maxResults) {
        String sql = "SELECT NEW com.foros.session.EntityTO(s.id, s.name, s.status) FROM Site s " +
                "WHERE UPPER(s.name) LIKE UPPER(:name) ESCAPE '\\' ORDER BY s.name ";

        QueryWrapper<EntityTO> query = new JpaQueryWrapper<EntityTO>(em, sql)
            .setLikeParameter("name", name)
            .setMaxResults(maxResults);

        return query.getResultList();
    }

    /**
     * Find all accounts which have sites associated with them
     * @return List of found accounts
     */
    @Override
    public List<EntityTO> findUsedAccounts() {
        StringBuilder qs = new StringBuilder("SELECT DISTINCT NEW com.foros.session.EntityTO(a.id, a.name, a.status) FROM Site s JOIN s.account a ");
        qs.append(" where a.name = a.name ");

        User currentUser = userService.getMyUser();
        if (currentUser.getRole().isPublisherAccountManager()) {
            qs.append(" and a.accountManager.id = ").append(currentUser.getId());
        }

        if (currentUserService.isInternalWithRestrictedAccess()) {
            qs.append(" and a.internalAccount.id in (:accountIds)");
        }
        if (!userService.getMyUser().isDeletedObjectsVisible()) {
            qs.append(" and a.status <> 'D'");
        }

        qs.append(" order by a.name");

        TypedQuery<EntityTO> query = em.createQuery(qs.toString(), EntityTO.class);

        if (currentUserService.isInternalWithRestrictedAccess()) {
            query.setParameter("accountIds", currentUserService.getAccessAccountIds());
        }

        List<EntityTO> result = query.getResultList();
        Collections.sort(result, new StatusNameTOComparator<>());
        return result;
    }

    @Override
    @Restrict(restriction = "PublisherEntity.view", parameters = "find('Site', #id)")
    public Site viewSiteFetched(Long id) {
        Site site = find(id);
        TypedQuery<Site> query = em.createQuery(createTagDownloadQuery(site, true), Site.class);
        query.setParameter("site", site);
        List<Site> sites = query.getResultList();
        if (!sites.isEmpty()) {
            return sites.get(0);
        } else {
            return null;
        }
    }

    @Override
    @Restrict(restriction = "PublisherEntity.view", parameters = "find('PublisherAccount', #accountId)")
    public List<Site> getByAccount(Long accountId, boolean excludeNoTagsSite) {
        Account account = em.getReference(PublisherAccount.class, accountId);
        TypedQuery<Site> query = em.createQuery(createTagDownloadQuery(account, excludeNoTagsSite), Site.class);
        query.setParameter("account", account);
        return query.getResultList();
    }

    private String createTagDownloadQuery(OwnedEntity obj, boolean excludeNoTagsSite) {
        ConditionStringBuilder queryBuilder = new ConditionStringBuilder();
        queryBuilder.append(" select distinct(s)")
            .append(" from Site s")
            .append(excludeNoTagsSite, " inner ", " left ")
            .append("  join fetch s.tags t")
            .append("  left join fetch t.tagPricings p")
            .append("  left join fetch t.sizes sz")
            .append("  left join fetch p.siteRate")
            .append(obj instanceof Account, " where s.account=:account ")
            .append(obj instanceof Site, " where s=:site ")
            .append(!userService.getMyUser().isDeletedObjectsVisible(),
                " and s.status <> 'D' " +
                        " and (t is null or t.status <> 'D') " +
                        " and (p is null or p.status <> 'D') "
            );
        if (currentUserService.isSiteLevelRestricted()) {
            queryBuilder.append(" and exists (from User u where u.id=" +
                    currentUserService.getUserId() + " and s in elements(u.sites)) ");
        }
        queryBuilder.append(" order by s.name ");
        return queryBuilder.toString();
    }

    /**
     * Checks for duplicated Sites in a list against list and database.
     *
     * @param sites Collection of Sites for duplicate check
     * @param accountId AccountId of sites for duplicate site check
     * @return Collection of duplicated Sites
     */
    @Override
    public Collection<Site> findDuplicated(final Collection<Site> sites, final Long accountId) {
        Collection<Site> duplicatedSites = new LinkedList<>(sites);

        CollectionUtils.filter(duplicatedSites, new Filter<Site>() {
            // Keeps names which comes with a set, they must be unique between each others also
            private Set<String> uniqueNames = new HashSet<>();

            // Keeps names to avoid duplicated entires in returning list.
            private Set<String> returnedDuplicates = new HashSet<>();

            @Override
            public boolean accept(Site site) {
                if (!uniqueNames.add(site.getName())) {
                    return returnedDuplicates.add(site.getName());
                } else if (isDuplicateSite(site)) {
                    return returnedDuplicates.add(site.getName());
                } else {
                    return false;
                }
            }
        });
        return duplicatedSites;
    }

    @Override
    @Restrict(restriction = "PublisherEntity.view", parameters = "find('Site', #siteId)")
    public List<SiteCreativeCategoryExclusion> getCategoryExclusions(Long siteId) {
        return em.createQuery("select elements(s.categoryExclusions) from Site s where s.id=:siteId", SiteCreativeCategoryExclusion.class)
            .setParameter("siteId", siteId)
            .getResultList();
    }

    @Override
    public void validateAll(Collection<Site> sites) {
        PersistenceUtils.getHibernateSession(em).setFlushMode(FlushMode.MANUAL);

        for (Site site : sites) {
            UploadContext siteUploadContext = SiteUploadUtil.getUploadContext(site);
            if (siteUploadContext.isFatal()) {
                continue;
            }

            // perform site validations
            Set<ConstraintViolation> constraintViolations = validationService.validate("Site.createOrUpdate", site).getConstraintViolations();
            SiteUploadUtil.setErrors(site, constraintViolations);

            if (site.getId() == null) {
                siteUploadContext.mergeStatus(UploadStatus.NEW);
            } else {
                Site existing = em.find(Site.class, site.getId());
                if (existing != null) {
                    site.setVersion(existing.getVersion());
                }
                siteUploadContext.mergeStatus(UploadStatus.UPDATE);
            }

            tagsService.validateAll(site.getTags());

        }

        PersistenceUtils.getHibernateSession(em).setFlushMode(FlushMode.AUTO);
    }

    @Override
    public void createOrUpdateAll(List<Site> sites) {
        logger.info("Bulk started");
        PersistenceUtils.getHibernateSession(em).setFlushMode(FlushMode.MANUAL);

        Map<AccountType, Map<String, CreativeSize>> sizesByaccountType = new HashMap<>();
        for (Site site : sites) {
            PersistenceUtils.flushAndClear(em, new Filter<Integer>() {
                @Override
                public boolean accept(Integer entitiesSize) {
                    return entitiesSize > 1000;
                }
            });

            Long siteId = site.getId();
            Long accountId = site.getAccount().getId();
            MarketplaceType marketplaceType = wgService.isPublisherWalledGarden(accountId) ?
                    wgService.findByPublisher(accountId).getPublisherMarketplaceType() : MarketplaceType.NOT_SET;

            Map<String, CreativeSize> creativeSizes = fetchSupportCreativeSizes(sizesByaccountType, em.find(PublisherAccount.class, accountId));
            if (siteId == null) {
                // new site
                Set<Tag> tags = site.getTags();

                site.setTags(new HashSet<Tag>(0));
                site = find(create(site));

                tagsService.createOrUpdateAll(tags, site, marketplaceType, creativeSizes);
            } else {
                // existing site
                Site existingSite = find(siteId);
                Status newStatus = site.getStatus();

                if (existingSite.getStatus() != newStatus) {
                    // new site status is different than existing site status
                    if (newStatus == Status.DELETED) {
                        // existing site is active
                        delete(siteId);
                    } else if (newStatus == Status.ACTIVE) {
                        // existing site is deleted
                        undelete(siteId);
                    }
                } else {
                    // new and existing site status is same
                    if (!SiteComparator.equals(existingSite, site)) { // Plane comparison of sites ...
                        site.retainChanges("name", "siteUrl", "status");
                        EntityUtils.copy(existingSite, site);
                        update(existingSite);
                    }
                }

                tagsService.createOrUpdateAll(site.getTags(), existingSite, marketplaceType, creativeSizes);
            }
        }

        PersistenceUtils.getHibernateSession(em).setFlushMode(FlushMode.AUTO);

        logger.info("Bulk is done");
    }

    @Override
    public List<Site> fetchSitesForCsvDownload(Collection<Long> accountIds, final int maxResults) {
        final List<Site> sites = new ArrayList<>();

        jdbcTemplate.withAuthContext().query("select * from entityqueries.sites_for_csv_download(?::int[])",
            new Object[] { jdbcTemplate.createArray("int", accountIds) },
            new RowCallbackHandler() {
                Map<Long, Site> siteMap = new LinkedHashMap<>();
                Map<Long, Tag> tagMap = new LinkedHashMap<>();
                Map<Long, TagPricing> tagPricingMap = new LinkedHashMap<>();
                int count;

                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    Site site = readSite(rs);

                    Tag tag = readTag(site, rs);
                    if (tag == null) {
                        return;
                    }

                    readTagPricing(tag, rs);
                }

                private void readProtocolNames(Tag tag, ResultSet rs) throws SQLException {
                    Array protocolNames = rs.getArray("protocol_names");
                    if (rs.wasNull()) {
                        return;
                    }
                    tag.getSizes().addAll(PGArray.read(protocolNames, new Converter<PGRow, CreativeSize>() {
                        @Override
                        public CreativeSize item(PGRow row) {
                            CreativeSize creativeSize = new CreativeSize();
                            creativeSize.setProtocolName(row.getString(0));
                            return creativeSize;
                        }
                    }));
                }

                private Site readSite(ResultSet rs) throws SQLException {
                    Long id = rs.getLong("site_id");
                    Site site = siteMap.get(id);
                    if (site != null) {
                        return site;
                    }

                    if (count > maxResults) {
                        throw new TooManyRowsException();
                    }

                    site = new Site(id);
                    site.setName(rs.getString("site_name"));
                    site.setStatus(Status.valueOf(rs.getString("site_status").charAt(0)));
                    site.setSiteUrl(rs.getString("site_url"));

                    PublisherAccount account = new PublisherAccount(rs.getLong("account_id"));
                    account.setName(rs.getString("account_name"));
                    site.setAccount(account);
                    siteMap.put(id, site);
                    sites.add(site);

                    count++;
                    return site;
                }

                private Tag readTag(Site site, ResultSet rs) throws SQLException {
                    Long id = rs.getLong("tag_id");
                    if (rs.wasNull()) {
                        return null;
                    }

                    Tag tag = tagMap.get(id);
                    if (tag != null) {
                        return tag;
                    }

                    tag = new Tag(id);
                    tag.setName(rs.getString("tag_name"));
                    tag.setPassback(rs.getString("passback"));
                    tag.setPassbackType(PassbackType.valueOf(rs.getString("passback_type")));
                    tag.setAllowExpandable("Y".equals(rs.getString("allow_expandable")));
                    tag.setAllSizesFlag("1".equals(rs.getString("all_size_flag")));
                    tag.setSizeType(new SizeType(rs.getString("size_type_name")));
                    if (!tag.isAllSizesFlag()) {
                        readProtocolNames(tag, rs);
                    }

                    tagMap.put(id, tag);
                    tag.setSite(site);
                    site.getTags().add(tag);

                    return tag;
                }

                private TagPricing readTagPricing(Tag tag, ResultSet rs) throws SQLException {
                    Long id = rs.getLong("tag_pricing_id");
                    if (rs.wasNull()) {
                        return null;
                    }

                    TagPricing tp = tagPricingMap.get(id);
                    if (tp != null) {
                        return tp;
                    }

                    tp = new TagPricing(id);
                    String countryCode = rs.getString("tp_country_code");
                    if (countryCode != null) {
                        tp.setCountry(new Country(countryCode));
                    }

                    String ccgType = rs.getString("tp_ccg_type");
                    if (ccgType != null) {
                        tp.setCcgType(CCGType.valueOf(ccgType.charAt(0)));
                    }

                    String ccgRateType = rs.getString("tp_ccg_rate_type");
                    if (ccgRateType != null) {
                        tp.setCcgRateType(RateType.valueOf(ccgRateType));
                    }

                    SiteRate sr = new SiteRate(rs.getLong("site_rate_id"));
                    sr.setRate(rs.getBigDecimal("tp_rate"));
                    sr.setRateType(SiteRateType.valueOf(rs.getString("tp_rate_type").trim()));
                    tp.setSiteRate(sr);

                    tagPricingMap.put(id, tp);
                    tag.getTagPricings().add(tp);

                    return tp;
                }
            });

        return sites;
    }

    @Override
    public boolean isDuplicateSite(Site site) {
        Query duplicateCheckQuery = site.getId() == null ? em.createNamedQuery("Site.countByName")
                : em.createNamedQuery("Site.countByIdName").setParameter("id", site.getId());

        duplicateCheckQuery.setParameter("name", site.getName());
        duplicateCheckQuery.setParameter("accountId", site.getAccount().getId());

        return ((Number) duplicateCheckQuery.getSingleResult()).intValue() != 0;
    }

    private Map<String, CreativeSize> fetchSupportCreativeSizes(Map<AccountType, Map<String, CreativeSize>> sizesByAccountType, PublisherAccount account) {
        AccountType accountType = account.getAccountType();
        Map<String, CreativeSize> result = sizesByAccountType.get(accountType);
        if (result == null) {
            result = new HashMap<>();
            for (CreativeSize creativeSize : accountType.getCreativeSizes()) {
                result.put(creativeSize.getProtocolName(), creativeSize);
            }
            sizesByAccountType.put(accountType, result);
        }

        return result;
    }

    @Override
    @Restrict(restriction = "PublisherEntity.view")
    public Result<Site> get(SiteSelector selector) {
        PartialList<Site> sites = new SiteQueryImpl()
                .restrict()
                .publishers(selector.getAccountIds())
                .sites(selector.getSiteIds())
                .statuses(selector.getSiteStatuses())
                .addDefaultOrder()
                .executor(executorService)
                .partialList(selector.getPaging());
        return new Result<>(sites);
    }

}
