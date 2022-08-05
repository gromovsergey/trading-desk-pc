package com.foros.model.channel.trigger;

import com.foros.model.channel.TriggersChannel;

public class UrlsHolder extends TriggersHolder<UrlTrigger> {
    public UrlsHolder() {
        this(null);
    }

    public UrlsHolder(TriggersChannel owner) {
        super(owner, new UrlTriggerCreator(), "urls");
    }
}
