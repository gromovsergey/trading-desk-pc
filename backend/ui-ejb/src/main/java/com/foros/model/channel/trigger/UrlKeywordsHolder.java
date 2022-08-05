package com.foros.model.channel.trigger;

import com.foros.model.channel.TriggersChannel;

public class UrlKeywordsHolder extends TriggersHolder<UrlKeywordTrigger> {
    public UrlKeywordsHolder() {
        this(null);
    }

    public UrlKeywordsHolder(TriggersChannel owner) {
        super(owner, new UrlKeywordTriggerCreator(owner), "urlKeywords");
    }
}
