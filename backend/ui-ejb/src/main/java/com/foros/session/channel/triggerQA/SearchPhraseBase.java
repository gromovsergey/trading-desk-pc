package com.foros.session.channel.triggerQA;

import java.util.List;

public abstract class SearchPhraseBase {
    private TriggerQAType type;

    public SearchPhraseBase(TriggerQAType type) {
        this.type = type;
    }

    public TriggerQAType getType() {
        return type;
    }

    public abstract List<String> getSearchPhrases();
}
