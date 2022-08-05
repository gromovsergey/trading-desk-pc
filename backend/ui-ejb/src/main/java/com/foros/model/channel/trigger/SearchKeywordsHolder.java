package com.foros.model.channel.trigger;

import com.foros.model.channel.TriggersChannel;

public class SearchKeywordsHolder extends TriggersHolder<SearchKeywordTrigger> {
    public SearchKeywordsHolder() {
        this(null);
    }

    public SearchKeywordsHolder(TriggersChannel owner) {
        super(owner, new SearchKeywordTriggerCreator(owner), "searchKeywords");
    }
}
