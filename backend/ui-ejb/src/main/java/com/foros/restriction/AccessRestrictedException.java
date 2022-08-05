package com.foros.restriction;

import com.foros.validation.constraint.violation.ConstraintViolation;
import java.security.AccessControlException;
import java.util.Collections;
import java.util.Set;

public class AccessRestrictedException extends AccessControlException {

    private Set<ConstraintViolation> constraintViolations;

    public AccessRestrictedException(String message, Set<ConstraintViolation> constraintViolations) {
        super(message);
        this.constraintViolations = Collections.unmodifiableSet(constraintViolations);
    }

    public Set<ConstraintViolation> getConstraintViolations() {
        return constraintViolations;
    }

}
