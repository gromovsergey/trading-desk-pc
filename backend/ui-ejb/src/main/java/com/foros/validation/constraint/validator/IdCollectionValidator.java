package com.foros.validation.constraint.validator;

public class IdCollectionValidator extends AbstractValidator<Iterable<Object>, IdCollectionValidator> {

    @Override
    protected void validateValue(Iterable<Object> value) {
        if (value != null) {
            if (!isValid(value)) {
                addConstraintViolation("errors.collectionsIds.positiveNumbers")
                        .withValue(value);
            }
        }
    }

    private boolean isValid(Iterable<Object> collection) {
        for (Object elem : collection) {
            if (!(elem instanceof Long) || (Long) elem <= 0) {
                return false;
            }
        }

        return true;
    }
}
