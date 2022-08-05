package com.foros.validation.constraint.validator;

public class IdValidator extends AbstractModesSupportValidator<Object, IdValidator> {

    @Override
    protected void validateWithModes(Object value) {
        if (value != null) {
            addConstraintViolation("errors.notrequired")
                    .withParameters(path())
                    .withValue(value);
        }
    }

}
