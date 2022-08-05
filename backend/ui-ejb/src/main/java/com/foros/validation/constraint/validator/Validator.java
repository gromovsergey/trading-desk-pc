package com.foros.validation.constraint.validator;

import com.foros.validation.ValidationContext;

/**
 * <p>Validator implement some validation logic, which can add constraint violations in context.
 * Validators mutable and have state, instantiate new for new validation.</p>
 *
 * @param <V> validation value type
 * @param <VT> validator type
 */
public interface Validator<V, VT extends Validator<V, VT>> {

    /**
     * <p>Use passed validation context for validation</p>
     *
     * @param context validation context
     * @return this for chaining
     */
    VT withContext(ValidationContext context);

    /**
     * <p>Use this sub-path for constraint violation constructing</p>
     *
     * @param path additional path
     * @return this for chaining
     */
    VT withPath(String path);

    /**
     * <p>Validate passed value and add constraint violations in context
     * (see {@link Validator#withContext(com.foros.validation.ValidationContext)})</p>
     *
     * @param value value for validation
     * @throws com.foros.validation.ValidationException if validation can not be processed
     */
    VT validate(V value);

}
