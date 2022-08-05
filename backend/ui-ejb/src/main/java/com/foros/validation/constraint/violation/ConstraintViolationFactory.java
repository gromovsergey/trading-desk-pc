package com.foros.validation.constraint.violation;

import com.foros.validation.interpolator.ForosErrorResolver;
import com.foros.validation.interpolator.MessageInterpolator;

public class ConstraintViolationFactory {

    private ForosErrorResolver errorResolver;
    private MessageInterpolator interpolator;

    public ConstraintViolationFactory(ForosErrorResolver errorResolver, MessageInterpolator interpolator) {
        this.errorResolver = errorResolver;
        this.interpolator = interpolator;
    }

    public ConstraintViolation create(ConstraintViolationBuilder builder) {
        return builder.build(errorResolver, interpolator);
    }

}
