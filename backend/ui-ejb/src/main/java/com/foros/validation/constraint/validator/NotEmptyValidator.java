package com.foros.validation.constraint.validator;

import java.util.Collection;

public class NotEmptyValidator extends AbstractMessageSupportValidator<Collection<Object>, NotEmptyValidator> {

    @Override
    protected void validateValue(Collection<Object> value) {
        if (value != null && value.isEmpty()) {
            if (!isMessageDefined()) {
                addConstraintViolation("errors.field.required")
                        .withValue(value);
            } else {
                addConstraintViolation()
                        .withValue(value);
            }
        }
    }

}
