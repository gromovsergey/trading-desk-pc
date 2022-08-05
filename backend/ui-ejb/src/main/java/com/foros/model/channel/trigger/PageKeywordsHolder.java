package com.foros.model.channel.trigger;

import com.foros.model.channel.TriggersChannel;

public class PageKeywordsHolder extends TriggersHolder<PageKeywordTrigger> {
    public PageKeywordsHolder() {
        this(null);
    }

    public PageKeywordsHolder(TriggersChannel owner) {
        super(owner, new PageKeywordTriggerCreator(owner), "pageKeywords");
    }
}
