package com.foros.validation.constraint.validator;

import com.foros.util.StringUtil;

public class RequiredValidator extends AbstractMessageSupportValidator<Object, RequiredValidator> {

    @Override
    protected void validateValue(Object value) {
        if (isEpmty(value)) {
            if (!isMessageDefined()) {
                addConstraintViolation("errors.field.required")
                        .withParameters(path())
                        .withValue(value);
            } else {
                addConstraintViolation()
                        .withValue(value);
            }
        }
    }

    private boolean isEpmty(Object value) {
        return value == null || StringUtil.isPropertyEmpty(value.toString());
    }
}
