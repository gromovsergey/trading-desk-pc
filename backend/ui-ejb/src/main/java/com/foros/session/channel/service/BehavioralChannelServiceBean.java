package com.foros.session.channel.service;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.Channel;
import com.foros.model.channel.trigger.TriggerBase;
import com.foros.model.channel.trigger.TriggerType;
import com.foros.model.channel.trigger.TriggersHolder;
import com.foros.model.security.ActionType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.LoggingInterceptor;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.cache.AutoFlushInterceptor;
import com.foros.session.db.DBConstraint;
import com.foros.session.security.AuditService;
import com.foros.util.CopyFlusher;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.*;

@Stateless(name = "BehavioralChannelService")
@Interceptors( {RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class BehavioralChannelServiceBean extends AbstractTriggersChannelServiceBean<BehavioralChannel>
        implements BehavioralChannelService {

    private final static List<String> EXTERNAL_UPDATABLE_FIELDS = new ArrayList<String>() {
        {
            add("name");
            add("description");
            add("supersededByChannel");
            add("status");
            add("channelRate");
            add("pageKeywords");
            add("searchKeywords");
            add("urls");
            add("urlKeywords");
            add("behavioralParameters");
            add("language");
        }
    };

    private final static List<String> INTERNAL_UPDATABLE_FIELDS = new ArrayList<String>(EXTERNAL_UPDATABLE_FIELDS);

    private final static List<String> EXTERNAL_CREATABLE_FIELDS = new ArrayList<String>(EXTERNAL_UPDATABLE_FIELDS);

    private final static List<String> INTERNAL_CREATABLE_FIELDS =  new ArrayList<String>(EXTERNAL_CREATABLE_FIELDS);

    static {
        INTERNAL_UPDATABLE_FIELDS.add("categories");
        INTERNAL_UPDATABLE_FIELDS.add("visibility");

        EXTERNAL_CREATABLE_FIELDS.add("country");

        INTERNAL_CREATABLE_FIELDS.add("categories");
        INTERNAL_CREATABLE_FIELDS.add("visibility");
    }

    @EJB
    private CurrentUserService currentUserService;

    private BehavioralParametersMerger<BehavioralChannel> behavioralParametersMerger = new BehavioralParametersMerger<BehavioralChannel>() {
        @Override
        protected EntityManager getEM() {
            return em;
        }
    };

    private CmpChannelHelper cmpChannelHelper = new CmpChannelHelper() {
        @Override
        protected EntityManager getEM() {
            return em;
        }
        @Override
        protected AuditService getAuditService() {
            return auditService;
        }
    };

    private ChannelRateMerger rateMerger = new ChannelRateMerger() {
        @Override
        public EntityManager getEm() {
            return em;
        }
    };

    @Override
    @Restrict(restriction = "AdvertisingChannel.create", parameters = "find('Account', #channel.account.id)")
    @Validate(validation = "BehavioralChannel.create", parameters = "#channel")
    @Interceptors( {AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public Long create(BehavioralChannel channel) {
        prePersistCreate(channel);
        checkMinUrlTriggerThreshold(channel);

        em.persist(channel);

        auditService.audit(channel, ActionType.CREATE);
        displayStatusService.update(channel);
        triggerService.addToBulkTriggersUpdate(channel);

        return channel.getId();
    }

    private void prePersistCreate(BehavioralChannel channel) {
        prePersist(channel);

        ChannelFieldsPreparer.initializeVisibilityAndRate(channel);
        ChannelFieldsPreparer.initializeStatuses(channel);
        ChannelFieldsPreparer.initializeQaStatus(channel);
        ChannelFieldsPreparer.initializeId(channel);
        channelFieldsPreparer.initializeLanguage(channel);
        channelFieldsPreparer.prepareAccount(channel);
        channelFieldsPreparer.prepareCountry(channel);
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.update", parameters = "find('BehavioralChannel', #channel.id)")
    @Validate(validation = "BehavioralChannel.update", parameters = "#channel")
    @Interceptors({AutoFlushInterceptor.class, LoggingInterceptor.class/* see ejb-jar.xml, CaptureChangesInterceptor.class */})
    public Long update(BehavioralChannel channel) {
        BehavioralChannel existing = findWithTriggers(channel.getId());
        if (!channel.isChanged("version") && existing != null) {
            channel.setVersion(existing.getVersion());
        }

        if (currentUserService.isExternal()) {
            channel.retainChanges(EXTERNAL_UPDATABLE_FIELDS);
        } else {
            channel.retainChanges(INTERNAL_UPDATABLE_FIELDS);
        }

        checkStatusChangeDate(existing, channel);
        prePersist(channel);
        rateMerger.merge(channel, existing, true);
        behavioralParametersMerger.merge(channel, existing);

        existing = em.merge(channel);

        makeApprovedOnChange(existing, channel);

        boolean triggersUpdated = TriggersHolder.copyChangedTriggers(channel, existing);

        auditService.audit(existing, ActionType.UPDATE);

        checkMinUrlTriggerThreshold(existing);

        displayStatusService.update(existing);//TODO

        if (triggersUpdated) {
            triggerService.addToBulkTriggersUpdate(existing);
        }

        displayStatusService.update(channel);//TODO

        return existing.getId();
    }

	private void prePersist(BehavioralChannel channel) {
        ChannelFieldsPreparer.prepareBehavioralParameters(channel);

        if (channel.getSupersededByChannel() != null) {
            channel.setSupersededByChannel(em.find(Channel.class, channel.getSupersededByChannel().getId()));
        }

        channelFieldsPreparer.prepareCategoryChannel(channel);
        normalizeTriggers(channel);
    }

    private void normalizeTriggers(BehavioralChannel channel) {
        Map<TriggerType, Collection<? extends TriggerBase>> removed = new HashMap<>();
        if (channel.isChanged("pageKeywords")) {
            removed.put(TriggerType.PAGE_KEYWORD, channel.getPageKeywords().normalizeAndDeduplicate());
        }

        if (channel.isChanged("searchKeywords")) {
            removed.put(TriggerType.SEARCH_KEYWORD, channel.getSearchKeywords().normalizeAndDeduplicate());        }

        if (channel.isChanged("urlKeywords")) {
            removed.put(TriggerType.URL_KEYWORD, channel.getUrlKeywords().normalizeAndDeduplicate());
        }

        if (channel.isChanged("urls")) {
            removed.put(TriggerType.URL, channel.getUrls().normalizeAndDeduplicate());
        }
        channel.setProperty(REMOVED_TRIGGERS, removed);
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.submitToCmp", parameters = "find('BehavioralChannel', #channel.id)")
    @Validate(validation = "BehavioralChannel.submitToCmp", parameters = "#channel")
    @Interceptors({LoggingInterceptor.class/*see ejb-jar.xml CaptureChangesInterceptor.class*/})
    public void submitToCmp(BehavioralChannel channel) {
        cmpChannelHelper.submitToCmp(channel);
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.makePublic", parameters = "find('BehavioralChannel', #channelId)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void makePublic(Long channelId, Timestamp version) {
        super.makePublic(channelId, version);
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.makePrivate", parameters = "find('BehavioralChannel', #channelId)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void makePrivate(Long channelId, Timestamp version) {
        super.makePrivate(channelId, version);
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.createCopy", parameters = "find('BehavioralChannel', #channelId)")
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public Long copy(Long channelId) {
        BehavioralChannel channel = findWithTriggers(channelId);
        BehavioralChannel copy = createCopy(channel);
        if (currentUserService.isExternal()) {
            copy.setAccount(currentUserService.getUser().getAccount());
        }
        copy.setPageKeywords(channel.getPageKeywords());
        copy.setSearchKeywords(channel.getSearchKeywords());
        copy.setUrls(channel.getUrls());
        copy.setUrlKeywords(channel.getUrlKeywords());
        copy.registerChange("language");
        Long id = create(copy);
        CopyFlusher.flush(em, copy.getName(), DBConstraint.CHANNEL_NAME);
        return id;
    }

    @Override
    public Long createBulk(BehavioralChannel channel) {
        if (currentUserService.isExternal()) {
            channel.retainChanges(EXTERNAL_CREATABLE_FIELDS);
        } else {
            channel.retainChanges(INTERNAL_CREATABLE_FIELDS);
        }
        return create(channel);
    }

    @Override
    public Long updateBulk(BehavioralChannel channel) {
        return update(channel);
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.update", parameters = "find('BehavioralChannel', #channelId)")
    public BehavioralChannel findForUpdate(Long channelId) {
        return findWithTriggers(channelId);
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.view", parameters = "find('BehavioralChannel', #channelId)")
    public BehavioralChannel view(Long channelId) {
        return findWithTriggers(channelId);
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.delete", parameters = "find('BehavioralChannel', #channelId)")
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void delete(Long channelId) {
        statusService.delete(find(channelId));
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.undelete", parameters = "find('BehavioralChannel', #channelId)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void undelete(Long channelId) {
        statusService.undelete(find(channelId));
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.activate", parameters = "find('BehavioralChannel', #channelId)")
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void activate(Long channelId) {
        BehavioralChannel channel = findWithTriggers(channelId);
        statusService.activate(channel);
        checkMinUrlTriggerThreshold(channel);
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.inactivate", parameters = "find('BehavioralChannel', #channelId)")
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void inactivate(Long channelId) {
        statusService.inactivate(find(channelId));
    }

    @Override
    protected Class<BehavioralChannel> getChannelClass() {
        return BehavioralChannel.class;
    }

    @Override
    public Collection<BehavioralParameters> getBehavioralParameters(BehavioralChannel channel) {
        return channel.getBehavioralParameters();
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    public void createOrUpdateAll(Long accountId, Collection<BehavioralChannel> channels) {
        super.createOrUpdateAll(accountId, channels);
    }
}
