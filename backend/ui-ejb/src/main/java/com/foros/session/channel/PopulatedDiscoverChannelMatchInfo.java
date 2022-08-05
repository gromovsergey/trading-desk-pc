package com.foros.session.channel;

import com.foros.session.NamedTO;

import java.util.Collections;
import java.util.List;

public class PopulatedDiscoverChannelMatchInfo extends AbstractChannelMatchInfo {
    private List<PopulatedNewsItemInfo> newsItems;

    public PopulatedDiscoverChannelMatchInfo(NamedTO channel, List<PopulatedTriggerInfo> triggers, List<PopulatedNewsItemInfo> newsItems) {
        super(channel, triggers);
        this.newsItems = newsItems;
    }

    public List<PopulatedNewsItemInfo> getNewsItems() {
        return newsItems;
    }

    public List<PopulatedTriggerInfo> getUrlKeywordTriggers() {
        return Collections.emptyList();
    }
}
