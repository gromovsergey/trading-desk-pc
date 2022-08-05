package com.foros.session.channel;

import com.foros.model.campaign.CCGKeyword;
import com.foros.model.channel.Channel;
import com.foros.model.channel.trigger.ChannelTrigger;
import com.foros.session.channel.triggerQA.TriggerQATO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.Local;

@Local
public interface TriggerService {

    void updateTriggers(List<TriggerQATO> triggers);

    void updateCCGKeywordsStatus(Long ccgId, Collection<Long> ids, String status);

    void addToBulkLinkCCGKeywords(CCGKeyword newKeyword);

    void addToBulkTriggersUpdate(Channel channel);

    void forceBulkTriggersUpdate();

    Map<Long, Set<ChannelTrigger>> getTriggersByChannelIds(List<Long> ids, boolean allowPartial);

    Set<ChannelTrigger> getTriggersByChannelId(Channel channel);
}
