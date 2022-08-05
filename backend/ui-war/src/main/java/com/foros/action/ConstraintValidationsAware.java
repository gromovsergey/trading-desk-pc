package com.foros.action;

import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;

import com.opensymphony.xwork2.ValidationAware;
import java.util.Collection;
import java.util.List;

public interface ConstraintValidationsAware extends ValidationAware {
    List<ConstraintViolationRule> getConstraintViolationRules();
    Collection<ConstraintViolation> getConstraintViolations();
}
