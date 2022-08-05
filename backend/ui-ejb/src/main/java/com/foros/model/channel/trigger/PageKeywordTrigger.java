package com.foros.model.channel.trigger;

public class PageKeywordTrigger extends KeywordTrigger {

    public PageKeywordTrigger() {}

    public PageKeywordTrigger(String countryCode, String original, boolean negative) {
        super(countryCode, original, negative);
    }

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.PAGE_KEYWORD;
    }
}
