package com.foros.validation.constraint.validator;

import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationException;
import com.foros.validation.constraint.violation.ConstraintViolationBuilder;

/**
 * <p>Default base class for validator, implements main validator state:
 * context and path, also implement base validator state checks.
 * Always use it as base for custom validator.</p>
 *
 * @param <V> validation value type
 * @param <VT> validator type
 */
public abstract class AbstractValidator<V, VT extends AbstractValidator<V, VT>> implements Validator<V, VT> {

    private String name;
    private ValidationContext context;

    protected ValidationContext context() {
        return context;
    }

    protected String path() {
        return name;
    }

    @Override
    public final VT withContext(ValidationContext context) {
        this.context = context;
        return self();
    }

    @Override
    public final VT withPath(String path) {
        this.name = path;
        return self();
    }

    @Override
    public final VT validate(V value) {
        validateContext();
        validateValue(value);
        return self();
    }

    protected void validateContext() {
        if (context() == null) {
            throw new ValidationException("Validator without context!");
        }
    }

    protected ConstraintViolationBuilder addConstraintViolation(String template) {
        return context().addConstraintViolation(template)
                .withPath(path());
    }

    protected abstract void validateValue(V value);

    protected VT self() {
        return (VT) this;
    }
}
