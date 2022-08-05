package com.foros.action.account;

import com.foros.util.StringUtil;

public class ContextNotSetException extends RuntimeException{

    private String messageKey;

    public ContextNotSetException(String messageKey) {
        this(messageKey, null);
    }

    public ContextNotSetException(String messageKey, Throwable cause) {
        super(StringUtil.getLocalizedString(messageKey), cause);
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
