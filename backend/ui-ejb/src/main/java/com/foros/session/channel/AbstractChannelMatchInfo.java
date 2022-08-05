package com.foros.session.channel;

import com.foros.session.NamedTO;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractChannelMatchInfo {

    protected NamedTO channel;
    protected List<PopulatedTriggerInfo> searchTriggers = new LinkedList<PopulatedTriggerInfo>();
    protected List<PopulatedTriggerInfo> pageTriggers = new LinkedList<PopulatedTriggerInfo>();
    protected List<PopulatedTriggerInfo> urlTriggers = new LinkedList<PopulatedTriggerInfo>();
    protected List<PopulatedTriggerInfo> urlKeywordTriggers = new LinkedList<PopulatedTriggerInfo>();

    public AbstractChannelMatchInfo(NamedTO channel, List<PopulatedTriggerInfo> triggers) {
        this.channel = channel;
        for (PopulatedTriggerInfo trigger : triggers) {
            switch (trigger.getTriggerType()) {
            case PAGE_KEYWORD:
                pageTriggers.add(trigger);
                break;
            case SEARCH_KEYWORD:
                searchTriggers.add(trigger);
                break;
            case URL:
                urlTriggers.add(trigger);
                break;
            case URL_KEYWORD:
                urlKeywordTriggers.add(trigger);
                break;
            default:
                break;
            }
        }
    }

    public NamedTO getChannel() {
        return channel;
    }

    public List<PopulatedTriggerInfo> getSearchTriggers() {
        return searchTriggers;
    }

    public List<PopulatedTriggerInfo> getPageTriggers() {
        return pageTriggers;
    }

    public List<PopulatedTriggerInfo> getUrlTriggers() {
        return urlTriggers;
    }

    abstract public List<PopulatedTriggerInfo> getUrlKeywordTriggers();

}