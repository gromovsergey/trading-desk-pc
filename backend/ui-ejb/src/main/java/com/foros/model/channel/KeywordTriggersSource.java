package com.foros.model.channel;

import com.foros.model.channel.trigger.KeywordTrigger;

import java.util.Collection;

public interface KeywordTriggersSource {

    Collection<KeywordTrigger> getAllKeywordTriggers();
}
