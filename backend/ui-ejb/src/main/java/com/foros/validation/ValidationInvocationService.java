package com.foros.validation;

import com.foros.validation.constraint.violation.ConstraintViolation;
import java.lang.reflect.Method;
import java.util.Set;
import javax.ejb.Local;

@Local
public interface ValidationInvocationService {

    void validate(Object target, Method method, Object[] params);

    Set<ConstraintViolation> validateWeb(Object target, Method method);

}
