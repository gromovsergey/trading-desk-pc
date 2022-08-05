package com.foros.validation.constraint.validator;

import com.foros.util.StringUtil;
import com.foros.validation.ValidationException;
import java.io.UnsupportedEncodingException;

public class ByteLengthValidator extends AbstractValidator<String, ByteLengthValidator> {

    private int length;

    public ByteLengthValidator withLength(int length) {
        this.length = length;
        return this;
    }

    @Override
    protected void validateValue(String value) {
        if (!isValid(value)) {
            addConstraintViolation("errors.field.maxBytesLength")
                .withParameters(length)
                .withValue(value);
        }
    }

    private boolean isValid(String value) {
        if (StringUtil.isPropertyEmpty(value)) {
            return true;
        }

        try {
            if (value.getBytes("UTF-8").length > length) {
                return false;
            }
        } catch (UnsupportedEncodingException e) {
            throw new ValidationException(e);
        }

        return true;
    }
}
