package com.foros.tools;

import com.foros.model.Country;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.trigger.KeywordTrigger;
import com.foros.model.channel.trigger.PageKeywordTrigger;
import com.foros.model.channel.trigger.SearchKeywordTrigger;
import com.foros.model.channel.trigger.UrlTrigger;
import com.foros.session.channel.descriptors.ChannelTriggersContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class TriggersCollector {

    private Map<Long, ChannelTriggersContainer> triggersById = new HashMap<>();


    public void add(Long channelId, String country, String triggerType, String originalTrigger, boolean negative) {
        ChannelTriggersContainer container = triggersById.get(channelId);
        if (container == null) {
            BehavioralChannel channel = new BehavioralChannel();
            channel.setId(channelId);
            channel.setCountry(new Country(country));

            HashSet<UrlTrigger> urls = new HashSet<>();
            HashSet<KeywordTrigger> keywords = new HashSet<>();
            container = new ChannelTriggersContainer(channel, keywords, urls);
            triggersById.put(channelId, container);
        }

        switch (triggerType) {
            case "U":
                container.getUrlTriggers().add(new UrlTrigger(originalTrigger, negative));
                break;
            case "P":
                container.getKeywordTriggers().add(new PageKeywordTrigger(country, originalTrigger, negative));
                break;
            case "S":
                container.getKeywordTriggers().add(new SearchKeywordTrigger(country, originalTrigger, negative));
                break;
            default:
                throw new IllegalArgumentException(triggerType);

        }
    }

    public void addAll(Collection<ChannelTriggersContainer> triggers) {
        for (ChannelTriggersContainer container : triggers) {
            ChannelTriggersContainer existing = triggersById.get(container.getChannel().getId());
            if (existing == null) {
                triggersById.put(container.getChannel().getId(), container);
            } else {
                existing.getUrlTriggers().addAll(container.getUrlTriggers());
                existing.getKeywordTriggers().addAll(container.getKeywordTriggers());
            }
        }

    }

    public List<ChannelTriggersContainer> getTriggers() {
        return new ArrayList<>(triggersById.values());
    }
}
