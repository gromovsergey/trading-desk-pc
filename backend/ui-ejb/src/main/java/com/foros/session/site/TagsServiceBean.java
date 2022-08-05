package com.foros.session.site;

import static com.foros.config.ConfigParameters.DEFAULT_ADSERVING_DOMAIN;
import static com.foros.config.ConfigParameters.DEFAULT_STATIC_DOMAIN;
import static com.foros.model.Status.ACTIVE;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.config.ConfigService;
import com.foros.model.Country;
import com.foros.model.Status;
import com.foros.model.account.MarketplaceType;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.EffectiveSizeTO;
import com.foros.model.creative.SizeType;
import com.foros.model.security.ActionType;
import com.foros.model.site.ContentCategory;
import com.foros.model.site.PassbackType;
import com.foros.model.site.Site;
import com.foros.model.site.SiteRate;
import com.foros.model.site.Tag;
import com.foros.model.site.TagEffectiveSizes;
import com.foros.model.site.TagOptGroupState;
import com.foros.model.site.TagOptGroupStatePK;
import com.foros.model.site.TagOptionValue;
import com.foros.model.site.TagPricing;
import com.foros.model.site.TagsCreativeCategoryExclusion;
import com.foros.model.site.TagsCreativeCategoryExclusionPK;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.CreativeToken;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionGroupState;
import com.foros.model.template.OptionGroupType;
import com.foros.model.template.OptionType;
import com.foros.model.template.OptionValueUtils;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.RestrictionService;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.session.bulk.Result;
import com.foros.session.cache.AutoFlushInterceptor;
import com.foros.session.creative.CreativePreviewService;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.creative.SizeTypeService;
import com.foros.session.fileman.FileSystem;
import com.foros.session.fileman.FileUtils;
import com.foros.session.fileman.PathProviderService;
import com.foros.session.query.PartialList;
import com.foros.session.query.QueryExecutorService;
import com.foros.session.query.site.TagQueryImpl;
import com.foros.session.security.AuditService;
import com.foros.session.security.UserService;
import com.foros.session.status.DisplayStatusService;
import com.foros.session.template.OptionGroupStateHelper;
import com.foros.session.template.TemplateService;
import com.foros.tx.TransactionCallback;
import com.foros.tx.TransactionSupportService;
import com.foros.util.CollectionMerger;
import com.foros.util.JpaChildCollectionMerger;
import com.foros.util.JpaCollectionMerger;
import com.foros.util.NumberUtil;
import com.foros.util.PersistenceUtils;
import com.foros.util.Schema;
import com.foros.util.StringUtil;
import com.foros.util.UrlUtil;
import com.foros.util.comparator.IdNameComparator;
import com.foros.util.preview.PreviewDimensionsSetter;
import com.foros.util.preview.PreviewModel;
import com.foros.util.preview.TagOptionValueSource;
import com.foros.util.templates.Template;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.ValidationService;
import com.foros.validation.annotation.Validate;
import com.foros.validation.code.BusinessErrors;
import com.foros.validation.constraint.violation.ConstraintViolationException;
import com.foros.validation.strategy.ValidationStrategies;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;

@Stateless(name = "TagsService")
@Interceptors({ RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class })
public class TagsServiceBean implements TagsService {

    private static final String PATH_SEPARATOR = "/";

    private static final String VERSION_HTML_FORMAT = "yyyyMMdd-HHmmssSS";

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private QueryExecutorService executorService;

    @EJB
    private AuditService auditService;

    @EJB
    private PathProviderService pathProviderService;

    @EJB
    private ConfigService configService;

    @EJB
    private DisplayStatusService displayStatusService;

    @EJB
    private ValidationService validationService;

    @EJB
    private RestrictionService restrictionService;

    @EJB
    private TransactionSupportService transactionSupportService;

    @EJB
    private TagsPreviewService tagsPreviewService;

    @EJB
    private TemplateService templateService;

    @EJB
    private UserService userService;

    @EJB
    private CreativeSizeService creativeSizeService;

    @EJB
    private SizeTypeService sizeTypeService;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private CreativePreviewService creativePreviewService;

    public TagsServiceBean() {
    }

    private void prePersist(Tag tag, Tag existingTag) {
        // site
        tag.setSite(em.getReference(Site.class, tag.getSite().getId()));
        // sizeType
        tag.setSizeType(em.getReference(SizeType.class, tag.getSizeType().getId()));

        if (tag.getSizeType().isMultiSize() && tag.isAllSizesFlag()) {
            tag.getSizes().clear();
        } else {
            // creative size
            if (tag.isChanged("sizes")) {
                Set<CreativeSize> selectedSizes = tag.getSizes();
                tag.setSizes(new HashSet<CreativeSize>(selectedSizes.size()));
                for (CreativeSize size : selectedSizes) {
                    tag.getSizes().add(em.getReference(CreativeSize.class, size.getId()));
                }
            }
        }

        // content categories
        if (tag.isChanged("contentCategories")) {
            if (existingTag != null && !restrictionService.isPermitted("PublisherEntity.advanced") && !existingTag.getContentCategories().isEmpty()) {
                tag.setContentCategories(Collections.<ContentCategory> emptySet());
                tag.unregisterChange("contentCategories");
            } else {
                Set<ContentCategory> selectedCategories = tag.getContentCategories();

                tag.setContentCategories(new HashSet<ContentCategory>(selectedCategories.size()));

                for (ContentCategory category : selectedCategories) {
                    tag.getContentCategories().add(em.find(ContentCategory.class, category.getId()));
                }
            }
        }

        for (TagPricing pricing : tag.getTagPricings()) {
            if (pricing.getCountry() != null && pricing.getCountry().getCountryCode() != null) {
                pricing.setCountry(em.getReference(Country.class, pricing.getCountry().getCountryCode()));
            }

            pricing.setTags(tag);
            TagPricing existingPricing = existingTag == null ? null : findTagPricing(existingTag, pricing);

            if (existingPricing != null) {
                pricing.setId(existingPricing.getId());
                pricing.setVersion(existingPricing.getVersion());
                if (!SiteComparator.equals(existingPricing.getSiteRate(), pricing.getSiteRate())) {
                    pricing.getSiteRate().setEffectiveDate(new Date(System.currentTimeMillis()));
                } else {
                    pricing.setSiteRate(existingPricing.getSiteRate());
                }
            } else {
                pricing.getSiteRate().setEffectiveDate(new Date(System.currentTimeMillis()));
            }

            if (pricing.getSiteRate() != null) {
                pricing.getSiteRate().setTagPricing(pricing);
            }
        }
    }

    @Override
    @Restrict(restriction = "PublisherEntity.create", parameters = "find('Site', #tag.site.id)")
    @Validate(validation = "Tag.create", parameters = "#tag")
    @Interceptors(CaptureChangesInterceptor.class)
    public Long create(Tag tag) {
        prePersist(tag, null);
        tag.setStatus(Status.ACTIVE);

        if (tag.isInventoryEstimationFlag()) {
            tag.setPassbackType(PassbackType.HTML_URL);
        }

        for (TagPricing tp : tag.getTagPricings()) {
            tp.getAllSiteRates().add(tp.getSiteRate());
        }

        em.persist(tag);

        if (StringUtil.isPropertyNotEmpty(tag.getPassbackHtml())) {
            String tagFilePath = createTagFile(tag);
            tag.setPassback(tagFilePath);
        }

        updatePassbackCode(tag);

        auditService.audit(tag, ActionType.CREATE);

        Long id = tag.getId();
        if (tag.isTagLevelExclusionFlag()) {
            Set<TagsCreativeCategoryExclusion> updatedCategoryExclusions = new HashSet<TagsCreativeCategoryExclusion>();
            for (TagsCreativeCategoryExclusion exclusion : tag.getTagsExclusions()) {
                if (exclusion.getTagsCreativeCategoryExclusionPK() == null) {
                    TagsCreativeCategoryExclusionPK pk = new TagsCreativeCategoryExclusionPK(exclusion.getCreativeCategory().getId(), id);
                    exclusion.setTagsCreativeCategoryExclusionPK(pk);
                }
                updatedCategoryExclusions.add(em.merge(exclusion));
            }
            tag.setTagsExclusions(updatedCategoryExclusions);
        }

        tag.getSite().getTags().add(tag);

        //em.flush();

        List<CreativeTemplate> templates = templateService.findTemplatesWithPublisherOptions(tag);
        prepareGroupStates(tag, templates);

        displayStatusService.update(tag.getSite());

        return id;
    }

    private void updatePassbackCode(Tag tag) {
        if (PassbackType.HTML_CODE.equals(tag.getPassbackType()) || PassbackType.JS_CODE.equals(tag.getPassbackType())) {
            tag.setPassbackCode(tag.getPassbackHtml());
        } else {
            tag.setPassbackCode(null);
        }
    }

    private void prepareGroupStates(Tag tag, List<CreativeTemplate> templates) {
        Map<TagOptGroupStatePK, TagOptGroupState> map = new HashMap<>();
        for (TagOptGroupState optGroupState : tag.getGroupStates()) {
            map.put(optGroupState.getId(), optGroupState);
        }

        for (CreativeTemplate creativeTemplate : templates) {
            fillGroupState(tag, map, creativeTemplate.getPublisherOptionGroups());
        }

        for (CreativeSize size : tag.getEffectiveSizes()) {
            fillGroupState(tag, map, size.getPublisherOptionGroups());
        }
    }

    private void fillGroupState(Tag tag, Map<TagOptGroupStatePK, TagOptGroupState> map, Set<OptionGroup> optionGroups) {
        for (OptionGroup optionGroup : optionGroups) {
            TagOptGroupStatePK groupStatePK = new TagOptGroupStatePK(optionGroup.getId(), tag.getId());
            if (!map.containsKey(groupStatePK)) {
                TagOptGroupState groupState = new TagOptGroupState();
                groupState.setId(groupStatePK);

                switch (optionGroup.getCollapsability()) {
                case EXPANDED_BY_DEFAULT:
                case NOT_COLLAPSIBLE:
                    groupState.setCollapsed(false);
                    break;
                case COLLAPSED_BY_DEFAULT:
                    groupState.setCollapsed(true);
                    break;
                }

                switch (optionGroup.getAvailability()) {
                case ALWAYS_ENABLED:
                case ENABLED_BY_DEFAULT:
                    groupState.setEnabled(true);
                    break;
                case DISABLED_BY_DEFAULT:
                    groupState.setEnabled(false);
                    break;
                }
                groupState.setTag(tag);
                em.persist(groupState);
                tag.getGroupStates().add(groupState);

            }
        }
    }

    @Override
    @Restrict(restriction = "PublisherEntity.updateOptions", parameters = "find('Tag', #tag.id)")
    @Validate(validation = "Tag.updateOptions", parameters = "#tag")
    @Interceptors({ CaptureChangesInterceptor.class })
    public void updateOptions(Tag tag) {
        tag.retainChanges("options", "groupStates");
        Tag existingTag = em.find(Tag.class, tag.getId());
        prePersistOptions(tag, existingTag);
        prePersistGroupStates(tag, existingTag);
        tag = em.merge(tag);
        auditService.audit(tag, ActionType.UPDATE);
        displayStatusService.update(tag.getSite());
    }

    @Override
    @Restrict(restriction = "PublisherEntity.update", parameters = "find('Tag', #tag.id)")
    @Validate(validation = "Tag.update", parameters = "#tag")
    @Interceptors({ CaptureChangesInterceptor.class, AutoFlushInterceptor.class })
    public void update(Tag tag) {
        Tag existingTag = em.find(Tag.class, tag.getId());

        tag.unregisterChange("id");
        if (tag.isInventoryEstimationFlag()) {
            tag.unregisterChange("tagPricings", "passback", "passbackHtml");
        }

        prePersist(tag, existingTag);
        if (!tag.isInventoryEstimationFlag()) {
            prePersistAdvertisingData(tag, existingTag);
        }
        if (!tag.isTagLevelExclusionFlag() && !tag.getTagsExclusions().isEmpty()) {
            tag.getTagsExclusions().clear();
            tag.registerChange("tagsExclusions");
        }
        prePersistTagsExclusions(tag, existingTag);
        prePersistContentCategories(tag, existingTag);
        updatePassbackCode(tag);
        boolean optionsReviewNeeded = tag.isChanged("tagsExclusions");
        new CollectionMerger<CreativeSize>(existingTag.getSizes(), tag.getSizes()).merge();
        tag.unregisterChange("sizes");
        tag = em.merge(tag);
        if (optionsReviewNeeded || existingTag.isChanged("sizes")) {
            em.flush();
            reviewOptions(tag);
        }
        auditService.audit(tag, ActionType.UPDATE);
    }

    private void reviewOptions(Tag tag) {
        List<CreativeTemplate> templates = templateService.findTemplatesWithPublisherOptions(tag);
        Collection<CreativeSize> sizes = tag.getEffectiveSizes();
        Map<String, CreativeSize> sizesByProtocolName = new HashMap<>(sizes.size());
        for (CreativeSize creativeSize : sizes) {
            sizesByProtocolName.put(creativeSize.getProtocolName(), creativeSize);
        }

        for (Iterator<TagOptionValue> it = tag.getOptions().iterator(); it.hasNext();) {
            TagOptionValue optionValue = it.next();
            OptionGroup optionGroup = optionValue.getOption().getOptionGroup();
            //noinspection SuspiciousMethodCalls
            boolean templatesGroup = templates.contains(optionGroup.getTemplate());

            boolean sizeGroup = optionGroup.getCreativeSize() != null && sizesByProtocolName.containsKey(optionGroup.getCreativeSize().getProtocolName());
            if (!templatesGroup && !sizeGroup) {
                it.remove();
                em.remove(optionValue);
            }
        }

        final class OGT implements Transformer {
            @Override
            public Object transform(Object input) {
                return ((OptionGroup) input).getId();
            }
        }
        Set<OptionGroup> publisherOptionGroups = new HashSet<>();
        for (CreativeSize size : sizes) {
            publisherOptionGroups = size.getPublisherOptionGroups();
        }
        @SuppressWarnings("unchecked")
        Collection<Long> allowedGroups = CollectionUtils.collect(publisherOptionGroups, new OGT());
        for (CreativeTemplate template : templates) {
            CollectionUtils.collect(template.getPublisherOptionGroups(), new OGT(), allowedGroups);
        }
        for (TagOptGroupState groupState : tag.getGroupStates()) {
            if (!allowedGroups.contains(groupState.getGroupId())) {
                em.remove(groupState);
            }
        }

        prepareGroupStates(tag, templates);
    }

    private void prePersistAdvertisingData(final Tag tag, final Tag existingTag) {

        if (tag.isChanged("passbackHtml") || tag.getPassbackType() != existingTag.getPassbackType()) {
            if (StringUtil.isPropertyNotEmpty(tag.getPassbackHtml())) {
                String tagFileName = createTagFile(tag);
                tag.setPassback(tagFileName);
            } else if (StringUtil.isPropertyEmpty(tag.getPassback())) {
                tag.setPassback(null);
            }
        }

        prePersistTagPricings(tag, existingTag);
    }

    private void prePersistTagPricings(Tag tag, Tag existingTag) {
        if (!tag.isChanged("tagPricings")) {
            return;
        }

        for (TagPricing tagPricing : tag.getTagPricings()) {
            tagPricing.setStatus(ACTIVE);
        }

        final boolean versionIsChanged = !existingTag.getVersion().equals(tag.getVersion());
        (new JpaChildCollectionMerger<Tag, TagPricing>(tag, "tagPricings", existingTag.getTagPricings(),
                tag.getTagPricings()) {
            @Override
            protected EntityManager getEM() {
                return em;
            }

            @Override
            protected Object getId(TagPricing tagPricing, int index) {
                return Arrays.asList(tagPricing.getCcgType(), tagPricing.getCcgRateType(), tagPricing.getCountry());
            }

            @Override
            protected boolean add(TagPricing updated) {
                updated.setId(null);
                SiteRate siteRate = updated.getSiteRate();
                updated.setSiteRate(null);
                getEM().persist(updated);
                getEM().persist(siteRate);
                updated.setSiteRate(siteRate);
                return true;
            }

            @Override
            protected void update(TagPricing persistent, TagPricing updated) {
                updated.setId(persistent.getId());
                // When CPM updated from UI
                if (updated.getSiteRate().getId() == null) {
                    getEM().persist(updated.getSiteRate());
                }
                updated.unregisterChange("tags");
                updated = getEM().merge(updated);
                // Lets fix changes in tag, because version for TagPricing is
                // always actual, please see prePersist() for TagPricing
                if (updated.isChanged()) {
                    if (versionIsChanged) {
                        throw new OptimisticLockException(persistent);
                    }
                    getParent().registerChange("tagPricings");
                }
            }

            @Override
            protected boolean delete(TagPricing persistent) {
                // TagPricing must be updated with status DELETED
                persistent.setStatus(Status.DELETED);
                return false;
            }
        }).merge();

    }

    private void prePersistContentCategories(final Tag tag, final Tag existingTag) {
        if (tag.isChanged("contentCategories")) {
            new CollectionMerger<ContentCategory>(existingTag.getContentCategories(), tag.getContentCategories()).merge();
            tag.unregisterChange("contentCategories");
        }
    }

    private void prePersistTagsExclusions(final Tag tag, final Tag existingTag) {
        if (!tag.isChanged("tagsExclusions")) {
            return;
        }

        tag.unregisterChange("tagsExclusions");
        (new JpaCollectionMerger<TagsCreativeCategoryExclusion>(existingTag.getTagsExclusions(), tag.getTagsExclusions()) {
            @Override
            protected EntityManager getEM() {
                return em;
            }

            @Override
            protected Object getId(TagsCreativeCategoryExclusion tagsCreativeCategoryExclusion, int index) {
                return tagsCreativeCategoryExclusion.getTagsCreativeCategoryExclusionPK();
            }

            @Override
            protected void update(TagsCreativeCategoryExclusion persistent, TagsCreativeCategoryExclusion updated) {
                updated.setTagsCreativeCategoryExclusionPK(persistent.getTagsCreativeCategoryExclusionPK());
                super.update(persistent, updated);
                if (persistent.isChanged()) {
                    tag.registerChange("tagsExclusions");
                }
            }
        }).merge();
    }

    private void prePersistOptions(final Tag tag, final Tag existingTag) {
        Map<Long, OptionGroupState> statesByOptionId = OptionGroupStateHelper.getGroupsStatesByOptionId(
                tag.getGroupStates(), templateService.findTemplatesWithPublisherOptions(tag), tag.getEffectiveSizes(),
                OptionGroupType.Publisher);

        Set<TagOptionValue> prepersistedOptions = new LinkedHashSet<TagOptionValue>();
        for (TagOptionValue optionValue : tag.getOptions()) {
            OptionGroupState state = statesByOptionId.get(optionValue.getOptionId());
            // Option belongs to disabled group, do not persist its value.
            if (state != null && !state.getEnabled()) {
                continue;
            }
            optionValue.setTag(tag);

            OptionValueUtils.prepareOptionValue(optionValue, tag.getAccount());

            // If user doesn't change the value from default, then we do not persist its value.
            optionValue.setOption(em.getReference(Option.class, optionValue.getOption().getId()));
            if (OptionValueUtils.isDefaultValue(optionValue)) {
                continue;
            }

            prepersistedOptions.add(optionValue);
        }
        tag.setOptions(prepersistedOptions);

        (new JpaCollectionMerger<TagOptionValue>(existingTag.getOptions(), tag.getOptions()) {
            @Override
            protected EntityManager getEM() {
                return em;
            }

            @Override
            protected Object getId(TagOptionValue tagOptionValue, int index) {
                return tagOptionValue.getId();
            }

            @Override
            protected boolean delete(TagOptionValue persistent) {
                if (persistent.getOption().isInternalUse() && !currentUserService.isInternal()) {
                    return false;
                }
                return super.delete(persistent);
            }
        }).merge();
    }

    private void prePersistGroupStates(final Tag tag, final Tag existingTag) {
        (new JpaCollectionMerger<TagOptGroupState>(existingTag.getGroupStates(), tag.getGroupStates()) {
            @Override
            protected EntityManager getEM() {
                return em;
            }

            @Override
            protected Object getId(TagOptGroupState tagOptGroupState, int index) {
                return tagOptGroupState.getId();
            }
        }).merge();
    }

    private void updateStatus(Tag tag) {
        displayStatusService.update(tag.getSite());
    }

    @Override
    @Restrict(restriction = "PublisherEntity.delete", parameters = "find('Tag', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void delete(Long id) {
        Tag tag = find(id);
        tag.setStatus(Status.DELETED);
        auditService.audit(tag, ActionType.UPDATE);
        updateStatus(tag);
    }

    @Override
    @Restrict(restriction = "PublisherEntity.undelete", parameters = "find('Tag', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void undelete(Long id) {
        Tag tag = find(id);
        tag.setStatus(Status.ACTIVE);
        auditService.audit(tag, ActionType.UPDATE);
        updateStatus(tag);
    }

    @Override
    @Restrict(restriction = "PublisherEntity.view", parameters = "find('Tag', #id)")
    public Tag view(Long id) {
        return find(id);
    }

    @Override
    public Tag find(Long id) {
        Tag tag = em.find(Tag.class, id);
        if (tag == null) {
            throw new EntityNotFoundException("Tag with id=" + id + " not found");
        }
        return tag;
    }

    @Override
    @Restrict(restriction = "PublisherEntity.view", parameters = "find('Tag', #id)")
    public Tag viewFetched(Long id) {
        Tag tag = find(id);
        tag.getTagPricings().size();
        tag.getTagsExclusions().size();
        tag.getContentCategories().size();
        tag.getOptions().size();
        tag.getGroupStates().size();
        if (tag.isAllSizesFlag()) {
            tag.getAccount().getAccountType().getCreativeSizes().size();
        }

        tag.getSizes().size();
        return tag;
    }

    @Override
    @Restrict(restriction = "PublisherEntity.update", parameters = "find('Tag', #id)")
    public Tag viewFetchedForEdit(Long id) {
        return viewFetched(id);
    }

    @Override
    public void refresh(Long id) {
        Tag tag = find(id);
        em.refresh(tag);
    }

    @Override
    @Restrict(restriction = "Entity.view", parameters = "find('Site', #siteId)")
    public List<EntityTO> getList(Long siteId) {
        StringBuilder ql = new StringBuilder("SELECT NEW com.foros.session.EntityTO(t.id, t.name, t.status)" +
                " FROM Tag t WHERE t.site.id = :siteId");
        if (!userService.getMyUser().isDeletedObjectsVisible()) {
            ql.append(" AND t.status <> 'D' ORDER BY upper(t.name)");
        } else {
            ql.append(" ORDER BY t.status, upper(t.name)");
        }
        return em.createQuery(ql.toString(), EntityTO.class).setParameter("siteId", siteId).getResultList();
    }

    @Override
    public List<Tag> findBySite(Long siteId) {
        StringBuilder ql = new StringBuilder("SELECT DISTINCT t FROM Tag t" +
                " LEFT JOIN FETCH t.tagPricings" +
                " WHERE t.site.id = :siteId");
        if (!userService.getMyUser().isDeletedObjectsVisible()) {
            ql.append(" AND t.status <> 'D'");
        }
        Query q = em.createQuery(ql.toString()).setParameter("siteId", siteId);
        //noinspection unchecked
        List<Tag> tags = (List<Tag>) q.getResultList();
        Collections.sort(tags, new IdNameComparator());
        for (Tag tag : tags) {
            // avoid duplicate tag pricing
            PersistenceUtils.initialize(tag.getSizes());
        }
        return tags;
    }

    @Override
    public List<CreativeSize> findSizesBySite(Long siteId) {
        Set<CreativeSize> siteSizes = new HashSet<>();
        for (Tag tag : findBySite(siteId)) {
            Collection<CreativeSize> tagSizes;
            if (tag.isAllSizesFlag()) {
                Long accountTypeId = tag.getAccount().getAccountType().getId();
                tagSizes = creativeSizeService.findByAccountTypeAndSizeType(accountTypeId, tag.getSizeType().getId());
            } else {
                tagSizes = tag.getSizes();
            }
            siteSizes.addAll(tagSizes);
        }
        return new ArrayList<>(siteSizes);
    }

    @Override
    public String getPassbackHtml(Tag tag) throws IOException {
        BufferedReader reader = null;
        try {
            StringBuilder passbackHtml = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(getTagsFS().readFile(tag.getPassback())));

            String line;
            while ((line = reader.readLine()) != null) {
                passbackHtml.append(line);
                passbackHtml.append('\n');
            }

            if (passbackHtml.length() > 0) {
                passbackHtml.delete(passbackHtml.length() - 1, passbackHtml.length());
            }
            return passbackHtml.toString();
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void fetchPassbackHtml(Collection<Tag> tags) {
        for (Tag tag : tags) {
            if (StringUtil.isPropertyNotEmpty(tag.getPassback()) && tag.getPassbackType() != PassbackType.HTML_URL) {
                try {
                    tag.setPassbackHtml(getPassbackHtml(tag));
                } catch (IOException e) {
                    tag.setPassbackHtml("");
                }
            }
        }
    }

    @Override
    public void fetchTagsHtml(Collection<Tag> tags) {
        for (Tag tag : tags) {
            String template = tag.isInventoryEstimationFlag() ? generateInventoryEstimationTagHtml(tag) : generateTagHtml(tag);
            tag.setProperty(TagsService.TAG_VIEW, template);
        }
    }

    @Override
    public String generateTagHtml(Tag tag) {
        Template template = new Template(tag.getSizeType().getTagTemplateFile());
        replaceAdserverUrlFromAdTagDomain(tag, template);
        replaceCommonTokens(template, tag);
        return template.generate();
    }

    @Override
    public String generateTagPreviewHtml(Tag tag) {
        Template template = new Template(tag.getSizeType().getTagTemplatePreviewFile());
        replaceAdserverUrlFromAdTagDomain(tag, template);
        replaceCommonTokens(template, tag);
        return template.generate();
    }

    @Override
    public String generateInventoryEstimationTagHtml(Tag tag) {
        Template template = new Template(tag.getSizeType().getTagTemplateIEstFile());
        replaceAdserverUrlFromAdTagDomain(tag, template);
        replaceCommonTokens(template, tag);
        return template.generate();
    }

    @Override
    public String generateIframeTagHtml(Tag tag) {
        Template template = new Template(tag.getSizeType().getTagTemplateIframeFile());
        replaceAdserverUrlFromAdservingDomain(tag, template);
        replaceCommonTokens(template, tag);
        return template.generate();
    }

    @Override
    public String generateBrowserPassbackTagHtml(Tag tag) {
        Template template = new Template(tag.getSizeType().getTagTemplateBrPbFile());
        replaceAdserverUrlFromAdTagDomain(tag, template);
        replaceCommonTokens(template, tag);
        return template.generate();
    }

    private void replaceAdserverUrlFromAdservingDomain(Tag tag, Template template) {
        String adserverDomain = tag.getAccount().getCountry().getAdservingDomainOrDefault(configService.get(DEFAULT_ADSERVING_DOMAIN));
        String adServerUrl = UrlUtil.appendSchema(Schema.DEFAULT, adserverDomain);
        template.add(CreativeToken.ADSERVER_URL.getName(), adServerUrl);
    }

    private void replaceAdserverUrlFromAdTagDomain(Tag tag, Template template) {
        template.add(CreativeToken.ADSERVER_URL.getName(), UrlUtil.appendSchema(Schema.DEFAULT, tag.getAccount().getCountry().getAdTagDomain()));
    }

    /** Tokens used in ALL templates */
    private void replaceCommonTokens(Template template, Tag tag) {
        tag = viewFetched(tag.getId());
        CreativeSize creativeSize = tag.getOnlySizeOrNull();

        // when multiple sizes per tag, pass empty values for ##SIZE##, ##WIDTH##, ##HEIGHT##
        Long width = getTagWidth(tag);
        Long height = getTagHeight(tag);

        template.add(CreativeToken.TAG_ID.getName(), tag.getId().toString());
        template.add(CreativeToken.SIZE.getName(), creativeSize == null ? "" : StringEscapeUtils.escapeJavaScript(creativeSize.getProtocolName()));
        template.add(CreativeToken.WIDTH.getName(), width == null ? "" : width.toString());
        template.add(CreativeToken.HEIGHT.getName(), height == null ? "" : height.toString());

        String passbackTypeStr = tag.getPassbackType().equals(PassbackType.JS_CODE) ? "js" : "html";
        template.add(CreativeToken.PASSBACK_TYPE.getName(), passbackTypeStr);

        String passbackUrl = "";
        if (StringUtil.isPropertyNotEmpty(tag.getPassback())) {
            if (tag.getPassbackType() == PassbackType.HTML_URL) {
                passbackUrl = tag.getPassback();
            } else {
                String staticDomain = tag.getAccount().getCountry().getStaticDomainOrDefault(configService.get(DEFAULT_STATIC_DOMAIN));
                passbackUrl = UrlUtil.concat(UrlUtil.appendSchema(Schema.DEFAULT, staticDomain), tag.getPassback());
            }
        }
        template.add(CreativeToken.PASSBACK_URL.getName(), passbackUrl);

        template.add(CreativeToken.PREVIEWPARAMS.getName(), tag.getSizeType().getTagTemplatePreviewFile());
        template.add(CreativeToken.INVENTORYMODEPARAMS.getName(), tag.getSizeType().getTagTemplateIEstFile());

        String adserverDomain = tag.getAccount().getCountry().getAdservingDomainOrDefault(configService.get(DEFAULT_ADSERVING_DOMAIN));
        template.add(CreativeToken.ADSERVER.getName(), adserverDomain);
    }

    /** Returns Creative Size Width if defined; in other case, looks for WIDTH token in Publisher Options */
    @Override
    public Long getTagWidth(Tag tag) {
        Tag existingTag = viewFetched(tag.getId());
        CreativeSize creativeSize = existingTag.getOnlySizeOrNull();
        if (creativeSize == null) {
            return null;
        }

        if (creativeSize.getWidth() != null) {
            return creativeSize.getWidth();
        }

        Option option = creativeSize.getOptionByToken(OptionGroupType.Publisher, CreativeToken.WIDTH.getName());
        if (option != null && OptionType.INTEGER.equals(option.getType()) && option.getDefaultValue() != null) {
            long value = NumberUtil.parseLong(option.getDefaultValue(), -1);
            if (value > 0) {
                return value;
            }
        }
        return null;
    }

    /** Returns Creative Size Height if defined; in other case, looks for HEIGHT token in Publisher Options */
    @Override
    public Long getTagHeight(Tag tag) {
        Tag existingTag = viewFetched(tag.getId());
        CreativeSize creativeSize = existingTag.getOnlySizeOrNull();
        if (creativeSize == null) {
            return null;
        }

        if (creativeSize.getHeight() != null) {
            return creativeSize.getHeight();
        }

        Option option = creativeSize.getOptionByToken(OptionGroupType.Publisher, CreativeToken.HEIGHT.getName());
        if (option != null && OptionType.INTEGER.equals(option.getType()) && option.getDefaultValue() != null) {
            long value = NumberUtil.parseLong(option.getDefaultValue(), -1);
            if (value > 0) {
                return value;
            }
        }
        return null;
    }

    private String createTagFile(final Tag tag) {
        String tagsFolder = FileUtils.generatePathById(tag.getId());
        if (tag.getPassbackType() == PassbackType.JS_CODE) {
            tagsFolder = tagsFolder + "." + tag.getPassbackType().getFileExtension();
        }

        final String path = tagsFolder;
        final String currentVersionFileName = createCurrentVersionFileName(tag.getPassbackType());
        final String currentVersionHtml = path + PATH_SEPARATOR + currentVersionFileName;

        transactionSupportService.onTransaction(new TransactionCallback() {
            @Override
            public void onBeforeCommit() {
                writeTo(tag.getPassbackHtml(), currentVersionHtml);
            }

            @Override
            public void onCommit() {
                // Cleaning up old files
                String[] fileNames = getTagsFS().list(path);
                if (!ArrayUtils.isEmpty(fileNames)) {
                    for (String fileName : fileNames) {
                        if (!currentVersionFileName.equals(fileName)) {
                            getTagsFS().delete(path + PATH_SEPARATOR + fileName);
                        }
                    }
                }
            }

            @Override
            public void onRollback() {
                getTagsFS().delete(currentVersionHtml);
            }
        });

        return currentVersionHtml;
    }

    private String createCurrentVersionFileName(PassbackType type) {
        DateFormat dateFormat = new SimpleDateFormat(VERSION_HTML_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(new Date()) + "." + type.getFileExtension();
    }

    private void writeTo(String passbackHtml, String path) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(getTagsFS().openFile(path)))) {
            writer.write(passbackHtml);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TagPricing findTagPricing(Tag tag, TagPricing pricing) {

        for (TagPricing tp : tag.getTagPricings()) {
            if (TagPricingUtil.isSameTagPricing(pricing, tp)) {
                return tp;
            }
        }

        return null;
    }

    @Override
    public void validateAll(Set<Tag> tags) {
        // perform tag validations
        for (Tag tag : tags) {
            Tag existing = null;
            if (tag.getId() != null) {
                existing = em.find(Tag.class, tag.getId());
                if (existing != null) {
                    boolean sizesFlag = tag.isAllSizesFlag();
                    tag.setFlags(existing.getFlags());
                    tag.setAllSizesFlag(sizesFlag);
                }
            }
            UploadContext tagUploadContext = SiteUploadUtil.getUploadContext(tag);
            ValidationContext context = validationService.validate(
                    ValidationStrategies.exclude(tagUploadContext.getWrongPaths()), "Tag.createOrUpdate", tag);
            SiteUploadUtil.setErrors(tag, context.getConstraintViolations());

            if (existing == null) {
                tagUploadContext.mergeStatus(UploadStatus.NEW);
            } else {
                tag.setVersion(existing.getVersion());
                tagUploadContext.mergeStatus(UploadStatus.UPDATE);
            }
        }
    }

    @Override
    public void createOrUpdateAll(Collection<Tag> tags, Site site, MarketplaceType marketplace, Map<String, CreativeSize> creativeSizes) {
        fetchPassbackHtml(site.getTags());

        Map<Long, Tag> existingTagsById = SiteComparator.convertTagsToMap(site.getTags());

        for (Tag tag : tags) {
            tag.setSite(site);

            Set<CreativeSize> sizes = tag.getSizes();
            tag.setSizes(new HashSet<CreativeSize>(sizes.size()));
            for (CreativeSize size : sizes) {
                tag.getSizes().add((creativeSizes.get(size.getProtocolName())));
            }
            SizeType sizeType = site.getAccount().getAccountType().findSizeTypeByName(tag.getSizeType().getDefaultName());
            tag.setSizeType(sizeType);

            if (tag.getId() == null) {
                // new tag
                fetchTagPricing(tag);
                tag.setMarketplaceType(marketplace);
                create(tag);
            } else {
                // existing tag
                Tag existingTag = existingTagsById.get(tag.getId());

                if (existingTag != null && !SiteComparator.equals(existingTag, tag)) {
                    // there exists valid tag in db
                    fetchTagPricing(tag);

                    MarketplaceType marketplaceType = existingTag.getMarketplaceType();
                    if (marketplaceType != null) {
                        tag.setMarketplaceType(marketplaceType);
                    }
                    update(tag);
                }
            }
        }
    }

    private FileSystem getTagsFS() {
        return pathProviderService.createFileSystem(pathProviderService.getTags());
    }

    private void fetchTagPricing(Tag tag) {
        if (!tag.isInventoryEstimationFlag()) {
            for (TagPricing pricing : tag.getTagPricings()) {
                pricing.setTags(tag);
                TagPricing existingPricing = null;
                if (tag.getId() != null) {
                    existingPricing = findTagPricing(tag, pricing);
                }

                if (existingPricing != null) {
                    pricing.setId(existingPricing.getId());
                    pricing.setVersion(existingPricing.getVersion());
                }

                pricing.setStatus(ACTIVE);
                if (pricing.getCountry() != null && pricing.getCountry().getCountryCode() != null) {
                    Country country = em.find(Country.class, pricing.getCountry().getCountryCode());
                    if (country == null) {
                        throw new EntityNotFoundException("Country:" + pricing.getCountry().getCountryCode());
                    }
                    pricing.setCountry(country);
                }
            }
        }
    }

    @Override
    public List<CreativeSize> findSizesWithPublisherOptions(Tag tag) {
        List<CreativeSize> sizes = new ArrayList<>(tag.getEffectiveSizes());
        for (Iterator<CreativeSize> iterator = sizes.iterator(); iterator.hasNext();) {
            CreativeSize size = iterator.next();

            if (size.getStatus() == Status.DELETED) {
                iterator.remove();
                continue;
            }

            boolean allGroupsAreEmpty = true;
            for (OptionGroup group : size.getPublisherOptionGroups()) {
                if (!group.getOptions().isEmpty()) {
                    allGroupsAreEmpty = false;
                    break;
                }
            }
            if (allGroupsAreEmpty) {
                iterator.remove();
            }
        }
        return sizes;
    }

    @Override
    @Restrict(restriction = "PublisherEntity.view")
    public Result<Tag> get(TagSelector selector) {
        PartialList<Tag> tags = new TagQueryImpl()
                .restrict()
                .tags(selector.getTagIds())
                .sites(selector.getSiteIds())
                .statuses(selector.getTagStatuses())
                .addDefaultOrder()
                .executor(executorService)
                .noCount()
                .partialList(selector.getPaging());
        return new Result<>(tags);
    }

    @Override
    @Restrict(restriction = "PublisherEntity.view")
    public TagEffectiveSizes getEffectiveSizes(Long tagId) {
        if (tagId == null) {
            throw new ConstraintViolationException(BusinessErrors.FIELD_IS_REQUIRED, "errors.api.emptyCriteria.tagEffectiveSizes");
        }
        Tag tag = find(tagId);
        PersistenceUtils.initializeCollection(tag.getSizes());
        Set<CreativeSize> effectiveTagSizes = tag.getSizes();
        List<EffectiveSizeTO> effectiveSizes = new ArrayList<>(effectiveTagSizes.size());

        PreviewDimensionsSetter dimensionsSetter = new PreviewDimensionsSetter<EffectiveSizeTO>() {
            @Override
            protected PreviewModel getPreviewModel(CreativeTemplate template,  CreativeSize size) {
                return creativePreviewService.buildPreviewModel(null, size);
            }

            @Override
            protected void setDimensions(Long width, Long height, EffectiveSizeTO target) {
                target.setWidth(width);
                target.setHeight(height);
            }
        };

        TagOptionValueSource optionValueSource = new TagOptionValueSource(tag.getAccount(), tag.getOptions());
        for (CreativeSize size : effectiveTagSizes) {
            EffectiveSizeTO effectiveSize = new EffectiveSizeTO(size.getId());
            dimensionsSetter.setWidthHeight(null, size, optionValueSource, effectiveSize);
            effectiveSizes.add(effectiveSize);
        }

        return new TagEffectiveSizes(tag, effectiveSizes);
    }
}
