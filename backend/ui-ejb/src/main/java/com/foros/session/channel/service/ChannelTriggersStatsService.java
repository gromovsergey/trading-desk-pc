package com.foros.session.channel.service;

import java.util.List;
import javax.ejb.Local;

@Local
public interface ChannelTriggersStatsService {

    ChannelTriggersTotalsTO getTriggersTotals(Long channelId);

    List<TriggerStatsTO> getTriggers(Long channelId, TriggersFilter triggersFilter);
}
