package com.foros.session.channel.service;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.channel.CategoryChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ExpressionChannel;
import com.foros.model.security.ActionType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.LoggingInterceptor;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.cache.AutoFlushInterceptor;
import com.foros.session.security.AuditService;
import com.foros.session.workflow.WorkflowService;
import com.foros.util.JpaChildCollectionMerger;
import com.foros.util.command.PreparedStatementWork;
import com.foros.util.command.executor.HibernateWorkExecutorService;
import com.foros.util.expression.ExpressionHelper;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Stateless(name = "ExpressionChannelService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class ExpressionChannelServiceBean extends AbstractChannelServiceBean<ExpressionChannel> implements ExpressionChannelService {
    private static final String[] UPDATABLE_FIELDS = {
            "name",
            "description",
            "supersededByChannel",
            "status",
            "channelRate",
            "expression",
            "visibility"
    };

    @EJB
    private WorkflowService workflowService;

    @EJB
    private HibernateWorkExecutorService executor;

    private ChannelRateMerger rateMerger = new ChannelRateMerger() {
        @Override
        public EntityManager getEm() {
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

    @Override
    @Restrict(restriction = "AdvertisingChannel.create", parameters = "find('Account', #channel.account.id)")
    @Validate(validation = "ExpressionChannel.create", parameters = "#channel")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public Long create(ExpressionChannel channel) {
        prePersistCreate(channel);
        em.persist(channel);
        auditService.audit(channel, ActionType.CREATE);
        displayStatusService.update(channel);
        return channel.getId();
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    public void createOrUpdateAll(Long accountId, Collection<ExpressionChannel> channels) {
        super.createOrUpdateAll(accountId, channels);
    }

    private void prePersistCreate(ExpressionChannel channel) {
        prePersist(channel);
        channelFieldsPreparer.prepareCountry(channel);
        channelFieldsPreparer.prepareAccount(channel);
        ChannelFieldsPreparer.initializeStatuses(channel);
        ChannelFieldsPreparer.initializeVisibilityAndRate(channel);
        ChannelFieldsPreparer.initializeQaStatus(channel);
        ChannelFieldsPreparer.initializeId(channel);
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.update", parameters = "find('ExpressionChannel', #channel.id)")
    @Validate(validation = "ExpressionChannel.update", parameters = "#channel")
    @Interceptors({AutoFlushInterceptor.class/* see ejb-jar.xml, CaptureChangesInterceptor.class */})
    public Long update(ExpressionChannel channel) {
        ExpressionChannel existing = find(channel.getId());

        if (!channel.isChanged("version") && existing != null) {
            channel.setVersion(existing.getVersion());
        }

        channel.retainChanges(UPDATABLE_FIELDS);
        prePersistUpdate(channel, existing);

        rateMerger.merge(channel, existing, false);
        channel = em.merge(channel);
        auditService.audit(channel, ActionType.UPDATE);
        displayStatusService.update(channel);

        return channel.getId();
    }

    private void prePersistUpdate(ExpressionChannel channel, ExpressionChannel existingChannel) {
        prePersistUsedChannels(channel, existingChannel);
        prePersistSupersededByChannel(channel);
        checkStatusChangeDate(existingChannel, channel);
    }

    private void processExpression(ExpressionChannel channel) {
        Collection<Long> usedChannelsIds = ExpressionHelper.extractChannels(channel.getExpression());
        Set<Channel> usedChannels = new LinkedHashSet<Channel>();
        for (Long id: usedChannelsIds) {
            Channel ch = em.find(Channel.class, id);
            usedChannels.add(ch);
        }
        channel.setUsedChannels(usedChannels);
    }

    private void prePersistSupersededByChannel(ExpressionChannel channel) {
        if (channel.getSupersededByChannel() != null) {
            channel.setSupersededByChannel(em.find(Channel.class, channel.getSupersededByChannel().getId()));
        }
    }

    private void prePersistUsedChannels(ExpressionChannel channel, ExpressionChannel existingChannel) {
        if (!channel.isChanged("expression")) {
            return;
        }

        processExpression(channel);

        (new JpaChildCollectionMerger<Channel, Channel>(channel, "usedChannels", existingChannel.getUsedChannels(), channel.getUsedChannels()) {

            @Override
            protected EntityManager getEM() {
                return em;
            }

            @Override
            protected boolean delete(Channel persistent) {
                executor.execute(new PreparedStatementWork(
                    "delete from expressionusedchannel where expression_channel_id = " + getParent().getId() +
                    " and used_channel_id = " + persistent.getId()));
                return true;
            }

        }).merge();
    }

    private void prePersist(ExpressionChannel channel) {
        processExpression(channel);
        prePersistSupersededByChannel(channel);
    }

    @Override
    @Interceptors({LoggingInterceptor.class/*see ejb-jar.xml CaptureChangesInterceptor.class*/})
    @Restrict(restriction = "AdvertisingChannel.submitToCmp", parameters = "find('ExpressionChannel', #channel.id)")
    @Validate(validation = "ExpressionChannel.submitToCmp", parameters = "#channel")
    public void submitToCmp(ExpressionChannel channel) {
        cmpChannelHelper.submitToCmp(channel);
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.makePublic", parameters = "find('ExpressionChannel', #channelId)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void makePublic(Long channelId, Timestamp version) {
        super.makePublic(channelId, version);
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.makePrivate", parameters = "find('ExpressionChannel', #channelId)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void makePrivate(Long channelId, Timestamp version) {
        super.makePrivate(channelId, version);
    }

    @Override
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public Long copy(Long channelId) {
        ExpressionChannel copy = createCopy(find(channelId));
        return create(copy);
    }

    @Override
    public Long createBulk(ExpressionChannel channel) {
        return create(channel);
    }

    @Override
    public Long updateBulk(ExpressionChannel channel) {
        return update(channel);
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.update", parameters = "find('ExpressionChannel', #id)")
    public ExpressionChannel findForUpdate(Long id) {
        return find(id);
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.view", parameters = "find('ExpressionChannel', #channelId)")
    public ExpressionChannel view(Long channelId) {
        return find(channelId);
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.delete", parameters = "find('ExpressionChannel', #channelId)")
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void delete(Long channelId) {
        statusService.delete(find(channelId));
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.undelete", parameters = "find('ExpressionChannel', #channelId)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void undelete(Long channelId) {
        statusService.undelete(find(channelId));
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.activate", parameters = "find('ExpressionChannel', #channelId)")
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void activate(Long channelId) {
        statusService.activate(find(channelId));
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.inactivate", parameters = "find('ExpressionChannel', #channelId)")
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void inactivate(Long channelId) {
        statusService.inactivate(find(channelId));
    }

    @Override
    protected Class<ExpressionChannel> getChannelClass() {
        return ExpressionChannel.class;
    }

    @Override
    public List<CategoryChannel> getCategories(Long channelId) {
        throw new UnsupportedOperationException();
    }

}
