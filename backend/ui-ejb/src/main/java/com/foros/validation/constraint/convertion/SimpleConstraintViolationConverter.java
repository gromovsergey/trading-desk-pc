package com.foros.validation.constraint.convertion;

import com.foros.validation.constraint.violation.ConstraintViolation;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

public class SimpleConstraintViolationConverter extends ConstraintViolationConverterSupport {
    private ErrorMessageList errors;
    private OgnlContext context = new OgnlContext();

    public SimpleConstraintViolationConverter(ErrorMessageList errors) {
        this.errors = errors;
    }

    @Override
    protected String evaluateExpression(Object expression, ConstraintViolation violation, String[] groups) {
        try {
            return (String) Ognl.getValue(expression, context, new Tuple(violation, groups));
        } catch (OgnlException e) {
            return null;
        }
    }

    @Override
    protected void addError(String path, String message) {
        errors.add(path, message);
    }

    public void addToContext(String name, Object value) {
        context.put(name, value);
    }

    private static class Tuple {
        private ConstraintViolation violation;
        private String[] groups;

        public Tuple(ConstraintViolation violation, String[] groups) {
            this.violation = violation;
            this.groups = groups;
        }

        public ConstraintViolation getViolation() {
            return violation;
        }

        public String[] getGroups() {
            return groups;
        }
    }
}
