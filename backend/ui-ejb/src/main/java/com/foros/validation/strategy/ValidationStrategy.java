package com.foros.validation.strategy;

import com.foros.validation.ValidationContext;

/**
 * Validation strategy
 */
public interface ValidationStrategy {

    /**
     * @param context validation context
     * @param fieldName field name
     * @return true, if field have to validate, else false
     */
    boolean isReachable(ValidationContext context, String fieldName);

}
