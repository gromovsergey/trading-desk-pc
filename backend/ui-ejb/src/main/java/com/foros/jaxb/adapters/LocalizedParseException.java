package com.foros.jaxb.adapters;

import com.foros.util.StringUtil;
import com.foros.validation.code.ForosError;
import com.foros.validation.util.ValidationUtil;

public class LocalizedParseException extends RuntimeException {
    private ForosError error;

    public LocalizedParseException(ForosError error, String key, Object... params) {
        this(key, params);
        this.error = error;
    }

    public LocalizedParseException(String key, Object... params) {
        super(StringUtil.getLocalizedString(key, params));
        error = ValidationUtil.getDefaultCodesResolver().resolve(key);
    }

    public ForosError getError() {
        return error;
    }
}
