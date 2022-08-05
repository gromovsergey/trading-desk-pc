package com.foros.session.channel.triggerQA;

public class KeywordValidationException extends Exception {
    private String trigger;
    private String errorKey;

    public KeywordValidationException(String trigger, String errorKey) {
        this.trigger = trigger;
        this.errorKey = errorKey;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public String getTrigger() {
        return trigger;
    }
}
