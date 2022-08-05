package com.foros.framework.struts;

import com.foros.action.BaseActionSupport;
import com.foros.action.ConstraintValidationsAware;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;

import java.util.Collection;
import java.util.List;

public class DelegatingValidatorContext
        extends com.opensymphony.xwork2.validator.DelegatingValidatorContext
        implements ConstraintValidationsAware {

    private final ConstraintValidationsAware constraintValidationsAware;

    public DelegatingValidatorContext(Object object) {
        super(object);
        constraintValidationsAware = makeConstraintValidationsAware(object);
    }

    private ConstraintValidationsAware makeConstraintValidationsAware(Object object) {
        if (object instanceof ConstraintValidationsAware) {
            return (ConstraintValidationsAware) object;
        } else {
            return new BaseActionSupport();
        }
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return constraintValidationsAware.getConstraintViolationRules();
    }

    @Override
    public Collection<ConstraintViolation> getConstraintViolations() {
        return constraintValidationsAware.getConstraintViolations();
    }
}
