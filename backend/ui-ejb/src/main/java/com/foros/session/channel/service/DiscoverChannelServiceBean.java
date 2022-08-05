package com.foros.session.channel.service;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.BehavioralParametersList;
import com.foros.model.channel.ChannelNamespace;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.DiscoverChannelList;
import com.foros.model.channel.trigger.TriggersHolder;
import com.foros.model.security.ActionType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.RestrictionService;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.LoggingInterceptor;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.StatusAction;
import com.foros.session.admin.behavioralParameters.BehavioralParamsListService;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.cache.AutoFlushInterceptor;
import com.foros.session.db.DBConstraint;
import com.foros.session.query.QueryExecutorService;
import com.foros.session.query.channel.DiscoverChannelQueryImpl;
import com.foros.session.security.ManagerAccountTO;
import com.foros.session.status.ApprovalAction;
import com.foros.util.CollectionUtils;
import com.foros.util.CopyFlusher;
import com.foros.util.EntityUtils;
import com.foros.util.PersistenceUtils;
import com.foros.util.SQLUtil;
import com.foros.util.comparator.IdNameComparator;
import com.foros.util.jpa.JpaQueryWrapper;
import com.foros.util.mapper.NamedTOConverter;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.ValidationService;
import com.foros.validation.annotation.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.hibernate.FlushMode;

@Stateless(name = "DiscoverChannelService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class DiscoverChannelServiceBean extends AbstractTriggersChannelServiceBean<DiscoverChannel> implements DiscoverChannelService {
    @EJB
    private RestrictionService restrictionService;

    @EJB
    private ValidationService validationService;

    @EJB
    private QueryExecutorService executorService;

    @EJB
    private DiscoverChannelListService discoverChannelListService;

    @EJB
    private BehavioralParamsListService behavioralParamsListService;

    @EJB
    private CurrentUserService currentUserService;

    private ChannelOperationsPreprocessor preprocessor = new ChannelOperationsPreprocessor() {
        @Override
        protected EntityManager getEm() {
            return em;
        }
    };

    @Override
    @Restrict(restriction = "DiscoverChannel.create", parameters = "find('InternalAccount', #channel.account.id)")
    @Validate(validation = "DiscoverChannel.create", parameters = "#channel")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public Long create(DiscoverChannel channel) {
        channel.unregisterChange("status");
        Long channelId = createInternal(channel);
        channel.setBaseKeyword(null);
        return channelId;
    }

    private Long createInternal(DiscoverChannel channel) {
        ChannelFieldsPreparer.initializeVisibilityAndRate(channel);
        ChannelFieldsPreparer.initializeStatuses(channel);
        ChannelFieldsPreparer.initializeQaStatus(channel);
        ChannelFieldsPreparer.initializeId(channel);
        channelFieldsPreparer.initializeLanguage(channel);
        prepare(channel);

        checkMinUrlTriggerThreshold(channel);

        em.persist(channel);

        postUpdate(channel, ActionType.CREATE, true);

        return channel.getId();
    }

    private void postUpdate(DiscoverChannel channel, ActionType actionType, boolean triggersUpdated) {
        if (triggersUpdated) {
            triggerService.addToBulkTriggersUpdate(channel);
        }
        auditService.audit(channel, actionType);
        displayStatusService.update(channel);
    }

    @Override
    public Collection<BehavioralParameters> getBehavioralParameters(DiscoverChannel channel) {
        BehavioralParametersList list = channel.getBehavParamsList();
        if (list != null && list.getId() != null) {
            BehavioralParametersList behavioralParameters = behavioralParamsListService.find(list.getId());
            if (!behavioralParameters.getBehavioralParameters().isEmpty()) {
                return behavioralParameters.getBehavioralParameters();
            }
        }
        return Collections.emptyList();
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.create")
    @Interceptors(LoggingInterceptor.class)
    public void createChild(DiscoverChannel child) {
        createInternal(child);
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.update")
    @Interceptors(LoggingInterceptor.class)
    public void updateChild(DiscoverChannel channel) {
        DiscoverChannel existing = em.merge(channel);
        TriggersHolder.copyChangedTriggers(channel, existing);
        existing.getUrls().clear();
        triggerService.addToBulkTriggersUpdate(existing);
        auditService.audit(existing, ActionType.UPDATE);
        displayStatusService.update(existing);
    }

    @Override
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class})
    @Validate(validation = "Operations.integrity", parameters = {"#channelOperations", "'discoverChannel'"})
    public OperationsResult perform(Operations<DiscoverChannel> channelOperations) throws Exception{
        // to prevent Hibernate doing auto-flush
        PersistenceUtils.getHibernateSession(em).setFlushMode(FlushMode.MANUAL);

        fetch(channelOperations);

        // validate
        validationService.validate("DiscoverChannel.merge", channelOperations).throwIfHasViolations();

        List<Long> result = new ArrayList<Long>();
        for (Operation<DiscoverChannel> channelMergeOperation : channelOperations.getOperations()) {
            result.add(processMergeOperation(channelMergeOperation));
        }

        try {
            em.flush();
        } catch (PersistenceException e) {
            if (DBConstraint.CHANNEL_NAME.match(e)) {
                validationService.validateInNewTransaction("BulkChannel.countryNameConstraintViolations", ChannelNamespace.DISCOVER, channelOperations)
                        .throwIfHasViolations();
            }
            throw e;
        }

        // let's Hibernate do rest of the job
        PersistenceUtils.getHibernateSession(em).setFlushMode(FlushMode.AUTO);

        return new OperationsResult(result);
    }

    private void fetch(Operations<DiscoverChannel> channelOperations) {
        List<Long> channelIds = prepareChannelIds(channelOperations);
        if (!channelIds.isEmpty()) {
            new DiscoverChannelQueryImpl()
                .matchedIds(channelIds)
                .asBean()
                .executor(executorService)
                .list();
        }

        preprocessor.preProcess(channelOperations);
    }

    private List<Long> prepareChannelIds(Operations<DiscoverChannel> channelOperations) {
        List<Long> channelIds = new ArrayList<Long>();
        for (Operation<DiscoverChannel> operation : channelOperations.getOperations()) {
            if (operation != null && operation.getEntity() != null && operation.getEntity().getId() != null) {
                channelIds.add(operation.getEntity().getId());
            }
        }
        return channelIds;
    }

    private Long processMergeOperation(Operation<DiscoverChannel> mergeOperation) throws Exception {
        DiscoverChannel channel = mergeOperation.getEntity();

            switch (mergeOperation.getOperationType()) {
            case CREATE:
                channel.setId(null);
                return createInternal(channel);
            case UPDATE:
                channel.retainChanges(
                        "name",
                        "country",
                        "language",
                        "discoverQuery",
                        "discoverAnnotation",
                        "description",
                        "pageKeywords",
                        "searchKeywords",
                        "urls",
                        "status",
                        "channelList",
                        "behavParamsList",
                        "version",
                        "categories");
                updateInternal(channel);
                return channel.getId();
            }
        throw new RuntimeException(mergeOperation.getOperationType() + " not supported!");
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.update", parameters = "find('DiscoverChannel', #channel.id)")
    @Validate(validation = "DiscoverChannel.update", parameters = "#channel")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public Long update(DiscoverChannel channel) {
        channel.retainChanges(
                "name",
                "country",
                "language",
                "discoverQuery",
                "discoverAnnotation",
                "description",
                "pageKeywords",
                "searchKeywords",
                "urls",
                "behavParamsList",
                "version");
        updateInternal(channel);
        return channel.getId();
    }

    private void updateInternal(DiscoverChannel channel) {
        DiscoverChannel existing = findWithTriggers(channel.getId());
        boolean countryUpdated = !existing.getCountry().equals(channel.getCountry());

        if (!channel.isChanged("version")) {
            channel.setVersion(existing.getVersion());
        }

        prepare(channel);

        channel.setBaseKeyword(null);

        existing = em.merge(channel);

        makeApprovedOnChange(existing, channel);

        boolean triggersUpdated = TriggersHolder.copyChangedTriggers(channel, existing);

        boolean needUpdateTriggers = countryUpdated || triggersUpdated;

        checkMinUrlTriggerThreshold(existing);

        postUpdate(existing, ActionType.UPDATE, needUpdateTriggers);

        existing.getId();
    }

    protected void prepare(DiscoverChannel channel) {
        channelFieldsPreparer.prepareAccount(channel);
        channelFieldsPreparer.prepareCountry(channel);
        channelFieldsPreparer.prepareBehavParamsList(channel);
        channelFieldsPreparer.prepareCategoryChannel(channel);
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.createCopy", parameters = "find('DiscoverChannel', #channelId)")
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class, LoggingInterceptor.class})
    public Long copy(Long channelId) {
        DiscoverChannel channel = find(channelId);
        DiscoverChannel copy = createCopy(channel);
        channel.resetTriggers(triggerService.getTriggersByChannelId(channel));
        copy.setPageKeywords(channel.getPageKeywords());
        copy.setSearchKeywords(channel.getSearchKeywords());
        copy.setUrls(channel.getUrls());
        copy.registerChange("language");
        Long id = create(copy);
        CopyFlusher.flush(em, copy.getName(), DBConstraint.CHANNEL_NAME);
        return id;
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.view")
    public DiscoverChannel view(Long channelId) {
        DiscoverChannel channel = find(channelId);
        // todo
        if (channel.getBehavParamsList() != null) {
            PersistenceUtils.initialize(channel.getBehavParamsList().getBehavioralParameters());
        }
        channel.resetTriggers(triggerService.getTriggersByChannelId(channel));
        return channel;
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.delete", parameters = "find('DiscoverChannel', #channelId)")
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void delete(Long channelId) {
        DiscoverChannel dc = find(channelId);
        if (dc.getChannelList() != null) {
            discoverChannelListService.unlink(dc.getId());
        }
        statusService.delete(dc);
    }


    @Override
    @Restrict(restriction = "DiscoverChannel.undelete", parameters = "find('DiscoverChannel', #channelId)")
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void undelete(Long channelId) {
        statusService.undelete(find(channelId));
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.activate", parameters = "find('DiscoverChannel', #channelId)")
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void activate(Long channelId) {
        DiscoverChannel channel = findWithTriggers(channelId);
        statusService.activate(channel);
        checkMinUrlTriggerThreshold(channel);
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.inactivate", parameters = "find('DiscoverChannel', #channelId)")
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void inactivate(Long channelId) {
        statusService.inactivate(find(channelId));
    }

    @Override
    protected Class<DiscoverChannel> getChannelClass() {
        return DiscoverChannel.class;
    }

    @Override
    public List<String> getAvailableLanguages() {
        return ChannelUtils.getAvailableLanguages();
    }

    @Override
    public boolean isBatchActionPossible(Collection<Long> ids, String action) {
        StatusAction statusAction = null;
        ApprovalAction approvalAction = null;
        try {
            statusAction = StatusAction.valueOf(action);
        } catch (IllegalArgumentException e) {
            approvalAction = ApprovalAction.valueOf(action);
        }

        List<Long> idList = new ArrayList<Long>(ids);
        List<DiscoverChannel> channels = new ArrayList<DiscoverChannel>();
        for (int i = 0; i < ids.size() / 999; ++i) {
            List<Long> idsBatch = idList.subList(999*i, Math.min(idList.size(), (i + 1)*999));

            channels.addAll(new JpaQueryWrapper<DiscoverChannel>(em,
                "select c from DiscoverChannel c where c.id in :ids")
                .setPrimitiveArrayParameter("ids", idsBatch)
                .getResultList());
        }
        for (DiscoverChannel dc : channels) {
            if (statusAction != null && !restrictionService.isPermitted("DiscoverChannel." + statusAction.toString().toLowerCase(), dc)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<EntityTO> findAvailableChannelLists(DiscoverChannel channel, String name, int maxResults) {
        @SuppressWarnings({"unchecked"})
        List<EntityTO> resultList = em.createNamedQuery("DiscoverChannel.availableChannelLists")
                .setParameter("account", channel.getAccount())
                .setParameter("name", "%" + SQLUtil.getEscapedString(name, '\\') + "%")
                .setMaxResults(maxResults).getResultList();
        DiscoverChannelList parentList = channel.getChannelList();
        boolean isLinked = parentList != null;
        if (isLinked) {
            EntityTO parentEnity = new EntityTO(parentList.getId(), parentList.getName(), parentList.getStatus().getLetter());
            resultList.remove(parentEnity);
        }
        return resultList;
    }

    @Override
    public List<ManagerAccountTO> getAvailableAccounts() {
        Query query = em.createNamedQuery("Account.findForDiscovery");
        List<ManagerAccountTO> accounts = query.getResultList();
        removeRestrictedAccounts(accounts);
        Collections.sort(accounts, new IdNameComparator());
        EntityUtils.applyStatusRules(accounts, null, true);
        return accounts;
    }

    protected void removeRestrictedAccounts(List<ManagerAccountTO> list) {
        if (currentUserService.isInternalWithRestrictedAccess()) {
            list.retainAll(CollectionUtils.convert(new NamedTOConverter(), currentUserService.getAccessAccountIds()));
        }
    }
}
