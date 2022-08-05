package com.foros.model.channel.trigger;

public class UrlKeywordTrigger extends KeywordTrigger {

    public UrlKeywordTrigger() {}

    public UrlKeywordTrigger(String countryCode, String original, boolean negative) {
        super(countryCode, original, negative);
    }

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.URL_KEYWORD;
    }
}
