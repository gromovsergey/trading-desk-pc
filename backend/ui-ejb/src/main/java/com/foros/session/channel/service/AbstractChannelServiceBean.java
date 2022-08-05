package com.foros.session.channel.service;

import com.foros.changes.ChangesService;
import com.foros.model.Status;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.model.security.ActionType;
import com.foros.service.ByIdLocatorService;
import com.foros.session.UtilityService;
import com.foros.session.security.AuditService;
import com.foros.session.status.DisplayStatusService;
import com.foros.session.status.StatusService;
import com.foros.util.EntityUtils;
import com.foros.util.PersistenceUtils;
import com.foros.util.bean.Filter;
import org.hibernate.FlushMode;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

abstract class AbstractChannelServiceBean<C extends Channel> extends AbstractCategoryOwnedChannelServiceBean implements ByIdLocatorService<C> {
    private static final Integer HIBERNATE_ENTITIES_COUNT_THRESHOLD = 10000;

    @EJB
    protected AuditService auditService;

    @EJB
    protected UtilityService utilityService;

    @EJB
    protected StatusService statusService;

    @EJB
    protected DisplayStatusService displayStatusService;

    @EJB
    protected ChangesService changesService;

    protected ChannelFieldsPreparer channelFieldsPreparer = new ChannelFieldsPreparer() {
        @Override
        protected EntityManager getEM() {
            return em;
        }
    };

    protected abstract Class<C> getChannelClass();

    public C find(Long channelId) {
        C channel = (channelId == null) ? null : em.find(getChannelClass(), channelId);
        if (channel == null) {
            throw new EntityNotFoundException(getChannelClass().getSimpleName() + " with id=" + channelId + " not found");
        }
        return channel;
    }

    @Override
    public C findById(Long id) {
        return find(id);
    }

    protected C createCopy(C managedChannel) {
        C newChannel = EntityUtils.clone(managedChannel);
        newChannel.setStatus(ChannelUtils.getDefaultStatus());
        String name = utilityService.calculateNameForCopy(managedChannel, 100);
        newChannel.setName(name);
        return newChannel;
    }

    protected void makePublic(Long channelId, Timestamp version) {
        C existingChannel = find(channelId);
        EntityUtils.checkEntityVersion(existingChannel, version);
        auditService.audit(existingChannel, ActionType.UPDATE);
        existingChannel.setVisibility(ChannelVisibility.PUB);
    }

    protected void makePrivate(Long channelId, Timestamp version) {
        C existingChannel = find(channelId);
        EntityUtils.checkEntityVersion(existingChannel, version);
        auditService.audit(existingChannel, ActionType.UPDATE);
        existingChannel.setVisibility(ChannelVisibility.PRI);
    }

    public abstract Long update(C channel);

    public abstract Long create(C channel);

    protected void createOrUpdateAll(Long accountId, Collection<C> channels) {
        // to prevent Hibernate doing auto-flush
        PersistenceUtils.getHibernateSession(em).setFlushMode(FlushMode.MANUAL);

        for (C channel : channels) {
            if (channel.getId() == null) {
                create(channel);
            } else {
                update(channel);
            }

            PersistenceUtils.flushAndClear(em, new Filter<Integer>() {
                @Override
                public boolean accept(Integer entitiesSize) {
                    return entitiesSize > HIBERNATE_ENTITIES_COUNT_THRESHOLD;
                }
            });

        }

        PersistenceUtils.flushAndClear(em);

        // let's Hibernate do rest of the job
        PersistenceUtils.getHibernateSession(em).setFlushMode(FlushMode.AUTO);
    }

    protected void checkStatusChangeDate(Channel existing, Channel updated) {
        Status updatedStatus = updated.getStatus();
        Status existingStatus = existing.getStatus();
        if (updated.isChanged("status") && !existingStatus.equals(updatedStatus)) {
            updated.setStatusChangeDate(new Date());
        }

    }
}
