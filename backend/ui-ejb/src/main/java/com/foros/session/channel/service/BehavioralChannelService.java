package com.foros.session.channel.service;

import com.foros.model.ExtensionProperty;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.trigger.TriggerBase;
import com.foros.model.channel.trigger.TriggerType;

import java.util.Collection;
import java.util.Map;
import javax.ejb.Local;

@Local
public interface BehavioralChannelService extends ChannelService<BehavioralChannel>, AdvertisingChannelSupport<BehavioralChannel> {
    /** @noinspection unchecked*/
    ExtensionProperty<Map<TriggerType, Collection<? extends TriggerBase>>> REMOVED_TRIGGERS = new ExtensionProperty(Map.class);

    @Override
    Long create(BehavioralChannel channel);

    @Override
    Long update(BehavioralChannel channel);

    @Override
    void submitToCmp(BehavioralChannel channel);

    @Override
    BehavioralChannel find(Long channelId);

    BehavioralChannel findWithTriggers(Long channelId);

    Long createBulk(BehavioralChannel channel);

    Long updateBulk(BehavioralChannel channel);

    BehavioralChannel findForUpdate(Long channelId);
}
