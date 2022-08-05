package com.foros.session.channel.service;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.security.ActionType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.LoggingInterceptor;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.cache.AutoFlushInterceptor;
import com.foros.session.security.AuditService;
import com.foros.validation.ValidationInterceptor;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.sql.Timestamp;
import java.util.Collection;


@Stateless(name = "AudienceChannelService")
@Interceptors( {RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class AudienceChannelServiceBean extends AbstractChannelServiceBean<AudienceChannel> implements AudienceChannelService {

    @EJB
    private AuditService auditService;

    @Override
    public Long create(AudienceChannel channel) {
        throw new RuntimeException("API only");
    }

    @Override
    public Long update(AudienceChannel channel) {
        throw new RuntimeException("API only");
    }

    @Override
    public Long createBulk(AudienceChannel channel) {
        prePersistCreate(channel);

        em.persist(channel);

        auditService.audit(channel, ActionType.CREATE);
        displayStatusService.update(channel);

        return channel.getId();
    }

    @Override
    public Long updateBulk(AudienceChannel channel) {
        AudienceChannel existing = find(channel.getId());
        if (!channel.isChanged("version") && existing != null) {
            channel.setVersion(existing.getVersion());
        }

        checkStatusChangeDate(existing, channel);

        existing = em.merge(channel);

        auditService.audit(existing, ActionType.UPDATE);
        displayStatusService.update(existing);

        return existing.getId();
    }

    @Override
    protected Class<AudienceChannel> getChannelClass() {
        return AudienceChannel.class;
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.view", parameters = "find('AudienceChannel', #channelId)")
    public AudienceChannel view(Long channelId) {
        return find(channelId);
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.delete", parameters = "find('AudienceChannel', #channelId)")
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void delete(Long channelId) {
        statusService.delete(find(channelId));
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.undelete", parameters = "find('AudienceChannel', #channelId)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void undelete(Long channelId) {
        statusService.undelete(find(channelId));
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.activate", parameters = "find('AudienceChannel', #channelId)")
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void activate(Long channelId) {
        AudienceChannel channel = find(channelId);
        statusService.activate(channel);
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.inactivate", parameters = "find('AudienceChannel', #channelId)")
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void inactivate(Long channelId) {
        statusService.inactivate(find(channelId));
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.makePublic", parameters = "find('AudienceChannel', #channelId)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void makePublic(Long channelId, Timestamp version) {
        super.makePublic(channelId, version);
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.makePrivate", parameters = "find('AudienceChannel', #channelId)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void makePrivate(Long channelId, Timestamp version) {
        super.makePrivate(channelId, version);
    }

    @Override
    public void submitToCmp(AudienceChannel channel) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createOrUpdateAll(Long accountId, Collection<AudienceChannel> channels) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long copy(Long channelId) {
        throw new UnsupportedOperationException();
    }

    private void prePersistCreate(AudienceChannel channel) {
        ChannelFieldsPreparer.initializeVisibilityAndRate(channel);
        ChannelFieldsPreparer.initializeStatuses(channel);
        ChannelFieldsPreparer.initializeQaStatus(channel);
        ChannelFieldsPreparer.initializeId(channel);
        channelFieldsPreparer.prepareAccount(channel);
        channelFieldsPreparer.prepareCountry(channel);
    }
}
