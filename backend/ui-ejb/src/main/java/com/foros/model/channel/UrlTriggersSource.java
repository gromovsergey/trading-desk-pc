package com.foros.model.channel;

import com.foros.model.channel.trigger.UrlTrigger;

import java.util.Collection;

public interface UrlTriggersSource {

    Collection<UrlTrigger> getAllUrlTriggers();
}
