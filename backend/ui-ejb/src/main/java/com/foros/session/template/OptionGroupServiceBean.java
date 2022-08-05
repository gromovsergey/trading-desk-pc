package com.foros.session.template;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.creative.CreativeOptGroupState;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.ActionType;
import com.foros.model.site.TagOptGroupState;
import com.foros.model.site.WDTagOptGroupState;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.template.Option;
import com.foros.model.template.OptionEnumValue;
import com.foros.model.template.OptionFileType;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionGroupType;
import com.foros.model.template.Template;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.BusinessException;
import com.foros.session.BusinessServiceBean;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.creative.CreativePreviewService;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.fileman.PathProviderService;
import com.foros.session.security.AuditService;
import com.foros.session.site.WDTagPreviewService;
import com.foros.tx.TransactionSupportService;
import com.foros.util.EntityUtils;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Stateless(name = "OptionGroupService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class OptionGroupServiceBean extends BusinessServiceBean<OptionGroup> implements OptionGroupService {
    @EJB
    private AuditService auditService;

    @EJB
    private CreativePreviewService creativePreviewService;

    @EJB
    private CreativeSizeService creativeSizeService;

    @EJB
    private TemplateService templateService;

    @EJB
    private WDTagPreviewService wdTagPreviewService;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @EJB
    private PathProviderService pathProviderService;

    @EJB
    private TransactionSupportService transactionSupportService;

    private HtmlOptionHelper htmlOptionHelper = new HtmlOptionHelper() {
        @Override
        protected EntityManager getEM() {
            return em;
        }

        @Override
        protected LoggingJdbcTemplate getJdbcTemplate() {
            return jdbcTemplate;
        }
    };

    private HtmlOptionFileHelper htmlFileHelper = new HtmlOptionFileHelper() {
        @Override
        protected TransactionSupportService getTransactionSupportService() {
            return transactionSupportService;
        }

        @Override
        protected PathProviderService getPathProviderService() {
            return pathProviderService;
        }
    };

    public OptionGroupServiceBean() {
        super(OptionGroup.class);
    }

    @Override
    @Restrict(restriction = "OptionGroup.update", parameters = "#optionGroup")
    @Validate(validation = "OptionGroup.update", parameters = "#optionGroup")
    @Interceptors({CaptureChangesInterceptor.class})
    public OptionGroup update(OptionGroup optionGroup) {
        optionGroup.retainChanges("defaultName", "defaultLabel", "availability", "collapsability");
        OptionGroup existing = super.update(optionGroup);
        Set<OptionGroup> advertiserOptionGroups = null;
        Set<OptionGroup> publisherOptionGroups = null;
        Set<OptionGroup> hiddenOptionGroups;
        if ((existing.getCreativeSize() != null) && (existing.getCreativeSize().getId() != null)) {
            CreativeSize creativeSize = existing.getCreativeSize();
            advertiserOptionGroups = creativeSize.getAdvertiserOptionGroups();
            publisherOptionGroups = creativeSize.getPublisherOptionGroups();
            hiddenOptionGroups = creativeSize.getHiddenOptionGroups();
            creativeSize.setOptionGroups(normalizeSortOrder(advertiserOptionGroups, publisherOptionGroups, hiddenOptionGroups, optionGroup, existing));
            auditService.audit(creativeSize, ActionType.UPDATE);
            creativePreviewService.deletePreview(creativeSize);
        } else if ((existing.getTemplate() != null) && (existing.getTemplate().getId() != null)) {
            Template template = existing.getTemplate();
            advertiserOptionGroups = template.getAdvertiserOptionGroups();
            publisherOptionGroups = template.getPublisherOptionGroups();
            hiddenOptionGroups = template.getHiddenOptionGroups();
            template.setOptionGroups(normalizeSortOrder(advertiserOptionGroups, publisherOptionGroups, hiddenOptionGroups, optionGroup, existing));
            auditService.audit(template, ActionType.UPDATE);
            deleteTemplatePreview(template);
        }
        if (advertiserOptionGroups == null && publisherOptionGroups == null) {
            throw new BusinessException("invalid optionGroup");
        }


        return optionGroup;
    }

    @Override
    @Restrict(restriction = "OptionGroup.create", parameters = "#optionGroup")
    @Validate(validation = "OptionGroup.create", parameters = "#optionGroup")
    @Interceptors({CaptureChangesInterceptor.class})
    public void create(OptionGroup optionGroup) {
        Set<OptionGroup> advertiserOptionGroups;
        Set<OptionGroup> publisherOptionGroups;
        Set<OptionGroup> hiddenOptionGroups;
        if ((optionGroup.getCreativeSize() != null) && (optionGroup.getCreativeSize().getId() != null)) {
            CreativeSize creativeSize = creativeSizeService.findById((optionGroup.getCreativeSize().getId()));
            advertiserOptionGroups = creativeSize.getAdvertiserOptionGroups();
            publisherOptionGroups = creativeSize.getPublisherOptionGroups();
            hiddenOptionGroups = creativeSize.getHiddenOptionGroups();
            creativeSize.setOptionGroups(normalizeSortOrder(advertiserOptionGroups, publisherOptionGroups, hiddenOptionGroups, optionGroup, null));
            auditService.audit(creativeSize, ActionType.UPDATE);
            creativePreviewService.deletePreview(creativeSize);
        } else if ((optionGroup.getTemplate() != null) && (optionGroup.getTemplate().getId() != null)) {
            Template template = templateService.findById((optionGroup.getTemplate().getId()));
            advertiserOptionGroups = template.getAdvertiserOptionGroups();
            publisherOptionGroups = template.getPublisherOptionGroups();
            hiddenOptionGroups = template.getHiddenOptionGroups();
            template.setOptionGroups(normalizeSortOrder(advertiserOptionGroups, publisherOptionGroups, hiddenOptionGroups, optionGroup, null));
            auditService.audit(template, ActionType.UPDATE);
            deleteTemplatePreview(template);
        }
        super.create(optionGroup);
    }

    private Set<OptionGroup> normalizeSortOrder(Set<OptionGroup> advertiserOptionGroups, Set<OptionGroup> publisherOptionGroups,
            Set<OptionGroup> hiddenOptionGroups, OptionGroup optionGroup, OptionGroup existing) {
        LinkedList<OptionGroup> result = new LinkedList<OptionGroup>();
        result.addAll(normalizeSortOrder(OptionGroupType.Advertiser, advertiserOptionGroups, optionGroup, existing));
        result.addAll(normalizeSortOrder(OptionGroupType.Publisher, publisherOptionGroups, optionGroup, existing));
        result.addAll(normalizeSortOrder(OptionGroupType.Hidden, hiddenOptionGroups, optionGroup, existing));
        return new HashSet<OptionGroup>(result);
    }

    private List<OptionGroup> normalizeSortOrder(OptionGroupType type, Set<OptionGroup> optionGroups, OptionGroup optionGroup, OptionGroup existing) {
        LinkedList<OptionGroup> result = new LinkedList<OptionGroup>(optionGroups);
        if (type != optionGroup.getType()) {
            return result;
        }

        int position = (optionGroup.getSortOrder() == null) ? 0 : optionGroup.getSortOrder().intValue() - 1;
        if (existing != null) {
            result.remove(existing);
            result.add(position, existing);
        } else {
            result.add(position, optionGroup);
        }

        normalizeSortOrders(result);

        return result;
    }

    private void normalizeSortOrders(List<OptionGroup> optionGroups) {
        int sortOrder = 1;
        for (OptionGroup og : optionGroups) {
            og.setSortOrder(sortOrder++);
        }
    }

    @Override
    public OptionGroup findById(Long id) {
        return super.findById(id);
    }

    @Override
    @Restrict(restriction="OptionGroup.view", parameters = "find('OptionGroup', #id)")
    public OptionGroup view(Long id) {
        return findById(id);
    }

    @Override
    @Restrict(restriction = "OptionGroup.delete", parameters = "find('OptionGroup', #id)")
    @Interceptors({CaptureChangesInterceptor.class})
    public void remove(Long id) {
        OptionGroup optionGroup = findById(id);
        Set<HtmlOptionHelper.OptionLink> existingLinks = htmlOptionHelper.findLinks(optionGroup);
        if (optionGroup.getCreativeSize() != null) {
            CreativeSize creativeSize = optionGroup.getCreativeSize();
            Set<OptionGroup> optionGroups = creativeSize.getOptionGroups();
            optionGroups.remove(optionGroup);
            removeCreativeSizeOptGroupStates(id);
            em.remove(optionGroup);
            auditService.audit(creativeSize, ActionType.UPDATE);
            creativePreviewService.deletePreview(creativeSize);
            htmlOptionHelper.processSize(creativeSize, existingLinks);
        } else if (optionGroup.getTemplate() != null) {
            Template template = optionGroup.getTemplate();
            Set<OptionGroup> optionGroups = template.getOptionGroups();
            optionGroups.remove(optionGroup);
            removeOptGroupStates(template, id);
            em.remove(optionGroup);
            auditService.audit(template, ActionType.UPDATE);
            deleteTemplatePreview(template);
            htmlOptionHelper.processTemplate(template, existingLinks);
        }

        if (optionGroup.getType() == OptionGroupType.Advertiser) {
            htmlFileHelper.removeOptionGroupFilesOnCommit(optionGroup);
        }

        OptionHelper.evictCache(optionGroup);
    }

    @Override
    public Set<OptionGroup> copyGroups(Set<OptionGroup> optionGroups) {
        Set<OptionGroup> res = new HashSet<OptionGroup>();
        for (OptionGroup optionGroup : optionGroups) {
            OptionGroup newOptionGroup = EntityUtils.clone(optionGroup);
            res.add(newOptionGroup);
            auditService.audit(newOptionGroup, ActionType.CREATE);
            for (Option option : newOptionGroup.getOptions()) {
                option.setOptionGroup(newOptionGroup);
                auditService.audit(option, ActionType.CREATE);
                for (OptionEnumValue value : option.getValues()) {
                    value.setOption(option);
                }
                for (OptionFileType fileType : option.getFileTypes()) {
                    fileType.setOption(option);
                }
            }
        }
        return res;
    }

    private void removeOptGroupStates(Template template, Long optionGroupId) {
        if (template instanceof DiscoverTemplate) {
            for (WDTagOptGroupState ogs : findWDTagOptGroupStates(optionGroupId)) {
                em.remove(ogs);
            }
        }

        if (template instanceof CreativeTemplate) {
            removeCreativeSizeOptGroupStates(optionGroupId);
        }
    }

    private void removeCreativeSizeOptGroupStates(Long optionGroupId) {
        for (TagOptGroupState ogs : findTagOptGroupStates(optionGroupId)) {
            em.remove(ogs);
        }

        for (CreativeOptGroupState ogs : findCreativeOptGroupStates(optionGroupId)) {
            ogs.getCreative().getGroupStates().remove(ogs);
            em.remove(ogs);
        }
    }

    @SuppressWarnings("unchecked")
    private List<CreativeOptGroupState> findCreativeOptGroupStates(Long optGroupStateId) {
        Query q = em.createNamedQuery("CreativeOptGroupState.findAllByOptionGroupId");
        q.setParameter("og_id", optGroupStateId);

        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    private List<WDTagOptGroupState> findWDTagOptGroupStates(Long optGroupStateId) {
        Query q = em.createNamedQuery("WDTagOptGroupState.findAllByOptionGroupId");
        q.setParameter("og_id", optGroupStateId);

        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    private List<TagOptGroupState> findTagOptGroupStates(Long optGroupStateId) {
        Query q = em.createNamedQuery("TagOptGroupState.findAllByOptionGroupId");
        q.setParameter("og_id", optGroupStateId);

        return q.getResultList();
    }

    private void deleteTemplatePreview(Template template) {
        if (template instanceof CreativeTemplate) {
            creativePreviewService.deletePreview((CreativeTemplate) template);
        } else {
            wdTagPreviewService.deletePreview((DiscoverTemplate) template);
        }
    }
}
