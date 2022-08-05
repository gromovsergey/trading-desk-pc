package com.foros.validation.constraint.validator;

public class NotNullValidator extends AbstractModesSupportValidator<Object, NotNullValidator> {

    @Override
    protected void validateWithModes(Object value) {
        if (value == null) {
            addConstraintViolation("errors.required")
                    .withParameters(path())
                    .withValue(value);
        }
    }

}
