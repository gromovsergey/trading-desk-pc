package com.foros.model.channel.trigger;

public class SearchKeywordTrigger extends KeywordTrigger {

    public SearchKeywordTrigger() {}

    public SearchKeywordTrigger(String countryCode, String original, boolean negative) {
        super(countryCode, original, negative);
    }

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.SEARCH_KEYWORD;
    }
}
