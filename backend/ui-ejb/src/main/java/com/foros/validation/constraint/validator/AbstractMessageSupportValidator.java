package com.foros.validation.constraint.validator;

import com.foros.util.StringUtil;
import com.foros.validation.ValidationException;
import com.foros.validation.constraint.violation.ConstraintViolationBuilder;

/**
 * <p>Support for validator, which can customize constraint violation message</p>
 *
 * @param <V> validation value type
 * @param <VT> validator type
 */
public abstract class AbstractMessageSupportValidator<V, VT extends AbstractMessageSupportValidator<V, VT>> extends AbstractValidator<V, VT> {

    private String message;
    private Object[] params;

    public VT withMessage(String message) {
        this.message = message;
        return self();
    }

    public VT withParameters(Object... params) {
        this.params = params;
        return self();
    }

    @Override
    protected void validateContext() {
        super.validateContext();

        if (message == null) {
            throw new ValidationException("Messagable validator without message!");
        }
    }

    protected ConstraintViolationBuilder addConstraintViolation() {
        return addConstraintViolation(message)
                .withParameters(params);
    }

    protected String message() {
        return message;
    }

    protected boolean isMessageDefined() {
        return StringUtil.isPropertyNotEmpty(message);
    }
}
