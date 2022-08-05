package com.foros.session.creative;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.ApproveStatus;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.CreativeCategoryTypeEntity;
import com.foros.model.creative.RTBCategory;
import com.foros.model.creative.RTBConnector;
import com.foros.model.security.ActionType;
import com.foros.model.site.Site;
import com.foros.model.site.SiteCreativeCategoryExclusion;
import com.foros.model.site.SiteCreativeCategoryExclusionPK;
import com.foros.model.site.Tag;
import com.foros.model.site.TagsCreativeCategoryExclusion;
import com.foros.model.site.TagsCreativeCategoryExclusionPK;
import com.foros.model.template.CreativeTemplate;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.BusinessException;
import com.foros.session.BusinessServiceBean;
import com.foros.session.bulk.Result;
import com.foros.session.cache.CacheService;
import com.foros.session.query.PartialList;
import com.foros.session.query.QueryExecutorService;
import com.foros.session.query.creativeCategory.CreativeCategoryQuery;
import com.foros.session.security.AuditService;
import com.foros.session.template.DeleteUnlinkedVisualCategoriesWork;
import com.foros.util.CollectionMerger;
import com.foros.util.CollectionUtils;
import com.foros.util.StringUtil;
import com.foros.util.VersionCollisionException;
import com.foros.util.command.executor.HibernateWorkExecutorService;
import com.foros.util.mapper.Mapper;
import com.foros.util.mapper.Pair;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.Query;
import org.apache.commons.lang.ObjectUtils;

@Stateless(name = "CreativeCategoryService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class CreativeCategoryServiceBean extends BusinessServiceBean<CreativeCategory> implements CreativeCategoryService {

    private static final String TEXT_ADS = "text ads";

    @EJB
    private AuditService auditService;

    @EJB
    private HibernateWorkExecutorService workExecutorService;

    @EJB
    private QueryExecutorService queryExecutorService;

    @EJB
    private CacheService cacheService;

    public CreativeCategoryServiceBean() {
        super(CreativeCategory.class);
    }

    @Override
    public void refreshAll() {
        for (CreativeCategory c : findAll(false)) {
            em.refresh(em.getReference(CreativeCategory.class, c.getId()));
        }
    }

    @Override
    public CreativeCategory findById(Long id) {
        return super.findById(id);
    }

    @Override
    public List<CreativeCategory> findByIds(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        return em.createQuery("select cc from CreativeCategory cc where cc.id in :categoryIds")
                .setParameter("categoryIds", ids)
                .getResultList();
    }

    @Override
    public List<CreativeCategory> findByType(CreativeCategoryType type, boolean showHold) {
        List<CreativeCategory> result;
        if (showHold) {
            Query q = em.createNamedQuery("CreativeCategory.findByType");
            q.setParameter("type", type);

            result = q.getResultList();
        } else {
            Query q = em.createNamedQuery("CreativeCategory.findByTypeStatus");
            q.setParameter("qaStatus", 'A');
            q.setParameter("type", type);

            result = q.getResultList();
        }
        return result;
    }

    @Override
    public CreativeCategory findByTypeName(CreativeCategoryType type, String name) {
        Query q = em.createNamedQuery("CreativeCategory.findByTypeName");
        q.setParameter("type", type);
        q.setParameter("name", name);
        List<CreativeCategory> resultList = (List<CreativeCategory>) q.getResultList();
        if (resultList.size() == 0) {
            return null;
        }
        return (CreativeCategory) resultList.get(0);
    }

    @Override
    public CreativeCategoryTypeEntity findCreativeCategoryType(CreativeCategoryType type) {
        CreativeCategoryTypeEntity cct = em.find(CreativeCategoryTypeEntity.class, Long.valueOf(type.ordinal()));
        Timestamp maxVersion = new Timestamp(0);
        for (CreativeCategory category : cct.getCategories()) {
            maxVersion = (Timestamp) ObjectUtils.max(maxVersion, category.getVersion());
        }
        cct.setVersion(maxVersion);
        return cct;
    }

    @Override
    @Restrict(restriction = "CreativeCategory.view")
    public List<CreativeCategory> findAll(boolean showHold) {
        if (showHold) {
            return super.findAll();
        }

        Query q = em.createNamedQuery("CreativeCategory.findByStatus");
        q.setParameter("qaStatus", 'A');

        @SuppressWarnings("unchecked")
        List<CreativeCategory> result = q.getResultList();

        return result;
    }

    @Override
    @Restrict(restriction = "CreativeCategory.update")
    @Validate(validation = "CreativeCategory.update", parameters = "#editTO")
    @Interceptors(CaptureChangesInterceptor.class)
    public void update(final CreativeCategoryEditTO editTO) {
        checkSystemCategories();
        CreativeCategoryTypeEntity cct = findCreativeCategoryType(editTO.getType());

        if (!cct.getVersion().equals(editTO.getVersion()))
            throw new VersionCollisionException();

        auditService.audit(cct, ActionType.UPDATE);

        Collection<CreativeCategory> persisted = cct.getCategories();
        Map<Long, CreativeCategory> persistedById = new HashMap<>();
        for (CreativeCategory creativeCategory : persisted) {
            persistedById.put(creativeCategory.getId(), creativeCategory);
        }

        Collection<CreativeCategory> updated = new ArrayList<CreativeCategory>();
        List<RTBConnector> connectors = getRTBConnectors();

        for (CreativeCategoryTO creativeCategoryTO : editTO.getCategories()) {
            CreativeCategory creativeCategory = new CreativeCategory(
                creativeCategoryTO.getId(), creativeCategoryTO.getName());
            Set<RTBCategory> rtbCategories = prepareRTBCategories(persistedById, connectors, creativeCategoryTO.getRtbCategories(), creativeCategory);
            creativeCategory.setRtbCategories(rtbCategories);
            updated.add(creativeCategory);
        }

        final List<CreativeCategory> systemCategories = getSystemCreativeCategories();
        new CreativeCategoryCollectionMerger(persisted, updated, systemCategories, editTO.getType()).merge();
    }

    private Set<RTBCategory> prepareRTBCategories(Map<Long, CreativeCategory> persistedById, List<RTBConnector> connectors, List<String> categories, CreativeCategory creativeCategory) {
        CreativeCategory persistedCreativeCategory = persistedById.get(creativeCategory.getId());
        Map<Long, RTBCategory> rtbCategoriesByConnectorId = new HashMap<>();
        if (persistedCreativeCategory != null) {
            for (RTBCategory category : persistedCreativeCategory.getRtbCategories()) {
                rtbCategoriesByConnectorId.put(category.getRtbConnector().getId(), category);
            }
        }

        for (int i = 0; i < categories.size(); i++) {
            RTBConnector rtbConnector = connectors.get(i);
            RTBCategory rtbCategory = rtbCategoriesByConnectorId.get(rtbConnector.getId());
            if (!StringUtil.isPropertyEmpty(categories.get(i))) {
                if (rtbCategory == null) {
                    rtbCategory = new RTBCategory();
                    rtbCategory.setRtbConnector(rtbConnector);
                    rtbCategory.setCreativeCategory(persistedCreativeCategory != null ? persistedCreativeCategory : creativeCategory);
                    rtbCategoriesByConnectorId.put(rtbConnector.getId(), rtbCategory);
                }
                rtbCategory.setName(categories.get(i));
            } else {
                rtbCategoriesByConnectorId.remove(rtbConnector.getId());
            }
        }
        return new LinkedHashSet<>(rtbCategoriesByConnectorId.values());
    }

    private class CreativeCategoryCollectionMerger extends CollectionMerger<CreativeCategory> {
        private Collection<CreativeCategory> systemCategories;
        private CreativeCategoryType type;

        public CreativeCategoryCollectionMerger(
                Collection<CreativeCategory> persisted,
                Collection<CreativeCategory> updated,
                Collection<CreativeCategory> systemCategories,
                CreativeCategoryType type) {
            super(persisted, updated);
            this.systemCategories = systemCategories;
            this.type = type;
        }

        @Override
        protected Object getId(CreativeCategory cc, int index) {
            return StringUtil.trimProperty(cc.getDefaultName());
        }

        @Override
        protected boolean add(CreativeCategory updated) {
            updated.setType(type);
            updated.setQaStatus(ApproveStatus.APPROVED.getLetter());
            return true;
        }

        @Override
        protected void update(CreativeCategory persistent, CreativeCategory updated) {
            persistent.setQaStatus(ApproveStatus.APPROVED.getLetter());
            persistent.setDefaultName(updated.getDefaultName());
            persistent.getRtbCategories().clear();
            persistent.getRtbCategories().addAll(updated.getRtbCategories());
        }

        @Override
        protected boolean delete(CreativeCategory persistent) {
            for (CreativeCategory system: systemCategories) {
                if (system.equals(persistent)) {
                    return false;
                }
            }
            if (persistent.getQaStatus() == ApproveStatus.APPROVED.getLetter()) {
                deleteCategory(persistent.getId());
                return true;
            }
            return false;
        }

        private Map<Long, CreativeCategory> toEntityIdSet(Collection<CreativeCategory> collection) {
            Map<Long, CreativeCategory> map = new HashMap<Long, CreativeCategory>();
            for (CreativeCategory t : collection) {
                if (t.getId() != null) {
                    map.put(t.getId(), t);
                }
            }
            return map;
        }

        @Override
        public void merge() {
            Map<Object, CreativeCategory> persistedIds = toIdSet(persisted);
            Map<Object, CreativeCategory> updatedIds = toIdSet(updated);
            Map<Long, CreativeCategory> realUpdatedIds = toEntityIdSet(updated);

            int index = 0;
            for (Iterator<CreativeCategory> it = persisted.iterator(); it.hasNext();) {
                CreativeCategory tp = it.next();

                if (realUpdatedIds.containsKey(tp.getId())) {
                    update(tp, realUpdatedIds.get(tp.getId()));
                    index++;
                    continue;
                }

                CreativeCategory tu = updatedIds.get(getId(tp, index));
                if (tu == null) {
                    // delete
                    if (delete(tp)) {
                        it.remove();
                    }
                } else {
                    // update
                    update(tp, tu);
                }
                index++;
            }

            index = 0;
            for (CreativeCategory tu : updated) {
                if (tu.getId() != null) {
                    continue;
                }
                CreativeCategory tp = persistedIds.get(getId(tu, index));
                if (tp == null) {
                    // add
                    if (add(tu)) {
                        persisted.add(tu);
                    }
                }
                index++;
            }
        }
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    public void deleteCategory(Long categoryId) {
        CreativeCategory existingCategory = findById(categoryId);
        for (CreativeCategory system: getSystemCreativeCategories()) {
            if (system.equals(existingCategory)) {
                throw new BusinessException("Cannot delete system category '" + system.getDefaultName() + "'");
            }
        }

        List<Creative> managedCreatives = em.createNamedQuery("Creative.findByCategory").setParameter("category",
                existingCategory).getResultList();
        for (Creative managedCreative : managedCreatives) {
            managedCreative.getCategories().remove(existingCategory);
        }

        List<CreativeTemplate> managedTemplates = em.createNamedQuery("CreativeTemplate.findByCategory").setParameter("category",
                existingCategory).getResultList();
        for (CreativeTemplate managedTemplate : managedTemplates) {
            managedTemplate.getCategories().remove(existingCategory);
        }

        List<Site> managedSites = em.createNamedQuery("SiteCreativeCategoryExclusion.findSiteByCategory").setParameter("category",
                existingCategory).getResultList();
        for (Site managedSite : managedSites) {
            SiteCreativeCategoryExclusion managedCategoryExclusion = em.getReference(SiteCreativeCategoryExclusion.class,
                    new SiteCreativeCategoryExclusionPK(existingCategory.getId(), managedSite.getId()));
            managedSite.getCategoryExclusions().remove(managedCategoryExclusion);
            em.remove(managedCategoryExclusion);
        }

        List<Tag> managedTags = em.createNamedQuery("TagsCreativeCategoryExclusion.findTagByCategory").setParameter("category",
               existingCategory).getResultList();
        for (Tag managedTag : managedTags) {
            TagsCreativeCategoryExclusion managedCategoryExclusion = em.getReference(TagsCreativeCategoryExclusion.class,
                    new TagsCreativeCategoryExclusionPK(existingCategory.getId(), managedTag.getId()));

            managedTag.getTagsExclusions().remove(managedCategoryExclusion);
            em.remove(managedCategoryExclusion);
        }

        em.remove(existingCategory);
    }

    private void checkSystemCategories() {
        for (CreativeCategory cc: getSystemCreativeCategories()) {
            CreativeCategory existing = findByTypeName(cc.getType(), cc.getDefaultName());
            if (existing == null) {
                em.persist(cc);
                auditService.audit(cc, ActionType.CREATE);
            } else {
                if (existing.getQaStatus() != 'A') {
                    em.merge(existing);
                    auditService.audit(existing, ActionType.APPROVE);
                }
            }
        }
    }

    @Override
    public CreativeCategoryEditTO getForEdit(CreativeCategoryType type) {
        CreativeCategoryTypeEntity cct = findCreativeCategoryType(type);
        List<CreativeCategoryTO> categoryNames = new ArrayList<CreativeCategoryTO>(cct.getCategories().size());
        List<RTBConnector> rtbConnectors = getRTBConnectors();
        for (CreativeCategory category : cct.getCategories()) {
            if (category.getQaStatus() != ApproveStatus.HOLD.getLetter()) {
                CreativeCategoryTO creativeCategoryTO = new CreativeCategoryTO();
                creativeCategoryTO.setName(category.getDefaultName());
                creativeCategoryTO.setId(category.getId());
                Map<Long, String> rtbCategoriesByconnectorId = CollectionUtils.map(new Mapper<RTBCategory, Long, String>() {
                    @Override
                    public Pair<Long, String> item(RTBCategory value) {
                        return new Pair<Long, String>(value.getRtbConnector().getId(), value.getName());
                    }
                }, category.getRtbCategories());

                List<String> categories = new ArrayList<>();
                for (RTBConnector connector : rtbConnectors) {
                    String name = rtbCategoriesByconnectorId.get(connector.getId());
                    categories.add(name == null ? "" : name);
                }

                creativeCategoryTO.setRtbCategories(categories);
                categoryNames.add(creativeCategoryTO);
            }
        }
        Collections.sort(categoryNames);
        CreativeCategoryEditTO editTO = new CreativeCategoryEditTO();
        editTO.setType(type);
        editTO.setVersion(cct.getVersion());
        editTO.setCategories(categoryNames);
        return editTO;
    }

    private static List<CreativeCategory> getSystemCreativeCategories() {
        CreativeCategory textAds = new CreativeCategory(null, TEXT_ADS);
        textAds.setType(CreativeCategoryType.VISUAL);
        return Arrays.asList(textAds);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void removeUnlinkedVisualCategories(Long templateId) {
        workExecutorService.execute(new DeleteUnlinkedVisualCategoriesWork(templateId));
        evictCache();
    }

    private void evictCache() {
        // evictNonTransactional Creative Visual Categories
        cacheService.evictCollection(Creative.class, "categories");
        cacheService.evictRegion(CreativeCategory.class);
    }

    @Override
    public List<RTBConnector> getRTBConnectors() {
        return em.createQuery("select rtb from RTBConnector rtb order by rtb.id").getResultList();
    }

    @Override
    @Restrict(restriction = "CreativeCategory.view")
    public Result<CreativeCategory> get(CreativeCategorySelector selector) {
        CreativeCategoryQuery query = new CreativeCategoryQuery()
                .categories(selector.getIds())
                .type(selector.getType())
                .addDefaultOrder();
        PartialList<CreativeCategory> partialList = query.executor(queryExecutorService)
                .partialList(selector.getPaging());
        return new Result<>(partialList);
    }
}
