package com.foros.validation.constraint.validator;

import com.foros.validation.ValidationException;
import javax.persistence.EntityManager;

/**
 * <p>Support for validator, which needs persistent context for validations</p>
 *
 * @param <V> validation value type
 * @param <VT> validator type
 */
public abstract class AbstractPersistentContextSupportValidator<V, VT extends AbstractPersistentContextSupportValidator<V, VT>> extends AbstractValidator<V, VT> {

    private EntityManager em;

    public VT withPersistentContext(EntityManager entityManager) {
        this.em = entityManager;
        return self();
    }

    @Override
    protected void validateContext() {
        super.validateContext();

        if (em == null) {
            throw new ValidationException("Persistent validator without persistent context!");
        }
    }

    protected EntityManager em() {
        return em;
    }



}
