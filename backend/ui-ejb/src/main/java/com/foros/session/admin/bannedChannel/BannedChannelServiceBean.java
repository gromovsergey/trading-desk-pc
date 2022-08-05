package com.foros.session.admin.bannedChannel;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.ApproveStatus;
import com.foros.model.channel.BannedChannel;
import com.foros.model.channel.trigger.ChannelTrigger;
import com.foros.model.channel.trigger.TriggersHolder;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.channel.TriggerService;
import com.foros.session.channel.triggerQA.TriggerQATO;
import com.foros.session.security.AuditService;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

@Stateless(name = "BannedChannelService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class BannedChannelServiceBean implements BannedChannelService {

    @PersistenceContext(unitName = "AdServerPU")
    protected EntityManager em;

    @EJB
    protected TriggerService triggerService;

    @EJB
    protected AuditService auditService;

    @Override
    @Restrict(restriction = "BannedChannel.update")
    @Validate(validation = "BannedChannel.update", parameters = "#channel")
    @Interceptors({CaptureChangesInterceptor.class})
    public void update(BannedChannel channel) {
        BannedChannel existing = em.merge(channel);

        boolean triggersUpdated = TriggersHolder.copyChangedTriggers(channel, existing);

        if (triggersUpdated) {
            triggerService.addToBulkTriggersUpdate(existing);
            triggerService.forceBulkTriggersUpdate();
            em.refresh(existing);
        }

        Set<ChannelTrigger> channelTriggers = triggerService.getTriggersByChannelId(existing);
        List<TriggerQATO> triggers = new ArrayList<>(channelTriggers.size());
        for (ChannelTrigger channelTrigger : channelTriggers) {
            TriggerQATO to = new TriggerQATO();
            to.setId(channelTrigger.getTriggerId());
            to.setQaStatus(ApproveStatus.APPROVED);
            triggers.add(to);
        }

        triggerService.updateTriggers(triggers);
    }

    @Override
    @Restrict(restriction = "BannedChannel.view")
    public BannedChannel getNoAdvBannedChannel() {
        return findById(BannedChannel.NO_ADV_CHANNEL_ID);
    }

    @Override
    @Restrict(restriction = "BannedChannel.view")
    public BannedChannel getNoTrackBannedChannel() {
        return findById(BannedChannel.NO_TRACK_CHANNEL_ID);
    }

    @Override
    @Restrict(restriction = "BannedChannel.view")
    public BannedChannel findById(Long channelId) {
        BannedChannel channel = em.find(BannedChannel.class, channelId);
        if (channel == null) {
            throw new EntityNotFoundException(BannedChannel.class.getSimpleName() + " with id=" + channelId + " not found");
        }
        channel.resetTriggers(triggerService.getTriggersByChannelId(channel));
        return channel;
    }
}
