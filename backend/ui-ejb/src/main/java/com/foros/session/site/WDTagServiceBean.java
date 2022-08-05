package com.foros.session.site;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.Status;
import com.foros.model.feed.Feed;
import com.foros.model.security.ActionType;
import com.foros.model.site.Site;
import com.foros.model.site.WDTag;
import com.foros.model.site.WDTagOptGroupState;
import com.foros.model.site.WDTagOptionValue;
import com.foros.model.site.WDTagOptionValuePK;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroupState;
import com.foros.model.template.OptionGroupType;
import com.foros.model.template.OptionValueUtils;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.security.AuditService;
import com.foros.session.security.UserService;
import com.foros.session.status.DisplayStatusService;
import com.foros.session.template.OptionGroupStateHelper;
import com.foros.util.EntityUtils;
import com.foros.util.JpaCollectionMerger;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless(name = "WDTagService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class WDTagServiceBean implements WDTagService {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private AuditService auditService;

    @EJB
    private WDTagPreviewService previewService;

    @EJB
    private DisplayStatusService displayStatusService;

    @EJB
    private UserService userService;

    @Override
    @Restrict(restriction = "PublisherEntity.view", parameters = "find('WDTag', #id)")
    public WDTag view(Long id) {
        return find(id);
    }

    @Override
    public WDTag find(Long id) {
        WDTag tag = em.find(WDTag.class, id);
        if (tag == null) {
            throw new EntityNotFoundException("WDTag with id=" + id + " not found");
        }

        return tag;
    }

    @Override
    public List<WDTagTO> findBySite(Long siteId) {
        Query q = userService.getMyUser().isDeletedObjectsVisible() ?
                em.createNamedQuery("WDTag.getIndexBySite") :
                em.createNamedQuery("WDTag.getNonDeletedIndexBySite");
        q.setParameter("siteId", siteId);
        return (List<WDTagTO>) q.getResultList();
    }

    private void prePersist(WDTag wdTag, WDTag existingWDTag) {
        wdTag.setSite(em.getReference(Site.class, wdTag.getSite().getId()));

        Timestamp templateVersion = wdTag.getTemplate().getVersion();
        wdTag.setTemplate(em.find(DiscoverTemplate.class, wdTag.getTemplate().getId()));


        EntityUtils.checkEntityVersion(wdTag.getTemplate(), templateVersion);

        if (wdTag.getOptions() != null) {
            prePersistOptions(wdTag);
        }

        prePersistFeeds(wdTag);

        if (StringUtil.isPropertyEmpty(wdTag.getPassbackUrl())){
            wdTag.setPassbackUrl(null);
        }
    }

    private void prePersistOptions(WDTag wdTag) {
        Map<Long, OptionGroupState> groupStates = OptionGroupStateHelper.getGroupsStatesByOptionId(wdTag.getGroupStates(), wdTag.getTemplate(), null, OptionGroupType.Publisher);
        Set<WDTagOptionValue> prepersistedOptions = new LinkedHashSet<WDTagOptionValue>();
        for (WDTagOptionValue optionValue : wdTag.getOptions()) {
            OptionGroupState state = groupStates.get(optionValue.getOptionId());
            if (state != null && !state.getEnabled()) {
                continue;
            }
            optionValue.setOption(em.getReference(Option.class, optionValue.getId().getOptionId()));

            if (wdTag.getId() != null) {
                optionValue.setTag(em.getReference(WDTag.class, wdTag.getId()));
                optionValue.setId(new WDTagOptionValuePK(wdTag.getId(), optionValue.getId().getOptionId()));
            } else {
                optionValue.setTag(wdTag);
            }

            OptionValueUtils.prepareOptionValue(optionValue, wdTag.getAccount());

            // If user doesn't change the value from default, then we do not persist its value.
            if (OptionValueUtils.isDefaultValue(optionValue)) {
                continue;
            }

            prepersistedOptions.add(optionValue);
        }

        wdTag.setOptions(prepersistedOptions);
    }

    @Override
    @Restrict(restriction = "PublisherEntity.createWDTag", parameters = "find('Site', #entity.site.id)")
    @Validate(validation = "WDTag.create", parameters = "#entity")
    @Interceptors(CaptureChangesInterceptor.class)
    public Long create(WDTag entity) {
        prePersist(entity, null);
        entity.setStatus(Status.ACTIVE);

        Set<WDTagOptionValue> options = entity.getOptions();
        entity.setOptions(null);

        em.persist(entity);

        auditService.audit(entity, ActionType.CREATE);

        entity.getSite().getWdTags().add(entity);
        displayStatusService.update(entity.getSite());

        for (WDTagOptionValue option : options) {
            option.setId(new WDTagOptionValuePK(entity.getId(), option.getId().getOptionId()));
            em.persist(option);
        }
        entity.setOptions(options);

        for (WDTagOptGroupState state: entity.getGroupStates()) {
            state.getId().setWdTagId(entity.getId());
        }

        try {
            previewService.generatePreview(entity);
            previewService.generateDiscoverCustomization(entity);
        } catch (Exception ignored) {
            // Ignore all exception during preview generation
        }

        return entity.getId();
    }

    @Override
    @Restrict(restriction = "PublisherEntity.update", parameters = "find('WDTag', #entity.id)")
    @Validate(validation = "WDTag.update", parameters = "#entity")
    @Interceptors(CaptureChangesInterceptor.class)
    public void update(WDTag entity) {
        entity.unregisterChange("id", "site");
        WDTag existingWDTag = find(entity.getId());
        prePersist(entity, existingWDTag);

        Set<WDTagOptionValue> newOptions = entity.getOptions();
        Set<WDTagOptionValue> existingOptions = existingWDTag.getOptions();
        (new JpaCollectionMerger<WDTagOptionValue>(existingOptions, newOptions) {
            @Override
            protected Object getId(WDTagOptionValue ov, int index) {
                return ov.getOption().getId();
            }

            @Override
            protected void update(WDTagOptionValue persistent, WDTagOptionValue updated) {
                updated.setId(persistent.getId());
                super.update(persistent, updated);
            }

            @Override
            protected EntityManager getEM() {
                return em;
            }

        }).merge();
        entity.unregisterChange("options");

        auditService.audit(existingWDTag, ActionType.UPDATE);
        final Set<WDTagOptGroupState> newStates= entity.getGroupStates();
        final Set<WDTagOptGroupState> existingStates = existingWDTag.getGroupStates();

        (new JpaCollectionMerger<WDTagOptGroupState>(existingStates, newStates) {
            @Override
            protected Object getId(WDTagOptGroupState cs, int index) {
                return cs.getId().getOptionGroupId();
            }

            @Override
            protected void update(WDTagOptGroupState persistent, WDTagOptGroupState updated) {
                updated.setId(persistent.getId());
                super.update(persistent, updated);
            }

            @Override
            protected EntityManager getEM() {
                return em;
            }

        }).merge();
        entity.unregisterChange("groupStates");

        em.merge(entity);
        em.flush();

        try {
            previewService.generatePreview(entity);
            previewService.generateDiscoverCustomization(entity);
        } catch (Exception ignored) {
            // Ignore all exception during preview generation
        }
    }

    @Override
    @Restrict(restriction = "PublisherEntity.delete", parameters = "find('WDTag', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void delete(Long id) {
        changeStatus(id, Status.DELETED);
    }

    @Override
    @Restrict(restriction = "PublisherEntity.undelete", parameters = "find('WDTag', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void undelete(Long id) {
        changeStatus(id, Status.ACTIVE);
    }

    private void changeStatus(Long id, Status newStatus) {
        WDTag tag = find(id);
        tag.setStatus(newStatus);
        auditService.audit(tag, ActionType.UPDATE);
        displayStatusService.update(tag.getSite());
    }

    private void prePersistFeeds(WDTag entity) {
        Set<Feed> optedInFeeds = entity.getOptedInFeeds();
        Set<Feed> optedOutFeeds = entity.getOptedOutFeeds();

        if (optedInFeeds.isEmpty() && optedOutFeeds.isEmpty()) {
            // nothing to merge
            return;
        }

        List<Feed> allFeeds = new ArrayList<Feed>(optedInFeeds.size() + optedOutFeeds.size());
        allFeeds.addAll(optedInFeeds);
        allFeeds.addAll(optedOutFeeds);

        Map<String, Feed> existingFeeds = findAllPersistFeeds(allFeeds);

        entity.setOptedInFeeds(mergeFeeds(existingFeeds, optedInFeeds));
        entity.setOptedOutFeeds(mergeFeeds(existingFeeds, optedOutFeeds));
    }

    private Set<Feed> mergeFeeds(Map<String, Feed> existingFeeds, Set<Feed> feeds) {
        Set<Feed> newFeeds = new HashSet<Feed>(feeds.size());
        for (Feed feed : feeds) {
            Feed existing = existingFeeds.get(feed.getUrl());
            if (existing != null) {
                newFeeds.add(existing);
            } else {
                em.persist(feed);
                newFeeds.add(feed);
                existingFeeds.put(feed.getUrl(), feed);
            }
        }
        return newFeeds;
    }

    private Map<String, Feed> findAllPersistFeeds(List<Feed> urlFeeds) {
        List<String> urls = new ArrayList<String>(urlFeeds.size());
        for (Feed feed: urlFeeds) {
            urls.add(feed.getUrl());
        }

        String sql = "select f from Feed f where f.url in (:urls)";
        @SuppressWarnings("unchecked")
        List<Feed> list = em.createQuery(sql).setParameter("urls", urls).getResultList();

        HashMap<String, Feed> result = new HashMap<String, Feed>(list.size());

        for (Feed feed : list) {
            result.put(feed.getUrl(), feed);
        }

        return result;
    }
}
