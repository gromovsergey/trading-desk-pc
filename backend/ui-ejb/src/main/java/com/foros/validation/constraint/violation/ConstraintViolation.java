package com.foros.validation.constraint.violation;

import com.foros.validation.code.ForosError;

/**
 * Constraint violation information
 */
public interface ConstraintViolation {

    /**
     * <p>See {@link com.foros.validation.interpolator.ForosErrorResolver} for mode information.</p>
     *
     * @return code of constraint violation.
     */
    ForosError getError();

    /**
     * <p>See {@link com.foros.validation.interpolator.MessageInterpolator} for mode information.</p>
     *
     * @return interpolated message
     */
    String getMessage();

    /**
     * @return property path
     */
    Path getPropertyPath();

    /**
     * @return link to invalid value
     */
    Object getInvalidValue();
    
}
