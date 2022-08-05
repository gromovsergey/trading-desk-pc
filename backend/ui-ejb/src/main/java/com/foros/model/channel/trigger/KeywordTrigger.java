package com.foros.model.channel.trigger;

import com.foros.util.unixcommons.TriggerNormalization;

public abstract class KeywordTrigger extends TriggerBase {

    private String normalized;
    private String countryCode;

    protected KeywordTrigger() {
    }

    public KeywordTrigger(String countryCode, String original, boolean negative) {
        super(original, negative);
        this.countryCode = countryCode;
    }

    public abstract TriggerType getTriggerType();

    @Override
    public String getNormalized() {
        if (normalized == null) {
            normalized = TriggerNormalization.normalizeKeyword(countryCode, original);
        }
        return normalized;
    }

    @Override
    public String getUINormalized() {
        return original;
    }

    public String getTrimmed() {
        return original.trim();
    }

    @Override
    public String getQANormalized() {
        return isNegative() ? "-" + getNormalized() : getNormalized();
    }

    public interface TriggerNormalizer {
        String normalize(String original);
    }
}
