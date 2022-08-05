package com.foros.validation;

import com.foros.validation.constraint.violation.ConstraintViolationException;
import com.foros.validation.strategy.ValidationMode;
import com.foros.validation.strategy.ValidationStrategy;

/**
 * Validation context builder
 */
public interface ValidationContextBuilder {

    /**
     * @param path path
     * @return this for chaining
     */
    ValidationContextBuilder withPath(String path);

    /**
     * @param index index
     * @return this for chaining
     */
    ValidationContextBuilder withIndex(int index);

    /**
     * @param mode validation mode
     * @return this for chaining
     */
    ValidationContextBuilder withMode(ValidationMode mode);

    /**
     * @param strategy additional validation strategy
     * @return this for chaining
     */
    ValidationContextBuilder withAdditionalStrategy(ValidationStrategy strategy);

    /**
     * @return configured validation context
     */
    ValidationContext build();

}
