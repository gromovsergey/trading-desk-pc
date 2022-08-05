package com.foros.validation.constraint.convertion;

import static com.foros.util.StringUtil.isPropertyEmpty;
import com.foros.validation.constraint.violation.ConstraintViolation;

import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.util.ValueStack;
import java.util.Collections;
import ognl.Ognl;
import ognl.OgnlException;

public class StrutsConstraintViolationConverter extends ConstraintViolationConverterSupport {

    private ValidationAware action;
    private ValueStack stack;

    public StrutsConstraintViolationConverter(ValidationAware action, ValueStack stack) {
        this.action = action;
        this.stack = stack;
    }

    @Override
    protected String evaluateExpression(Object expression, ConstraintViolation violation, String[] groups) {
        stack.push(new Tuple(violation, groups));
        try {
            return (String) Ognl.getValue(expression, Collections.emptyMap(), stack.getRoot());
        } catch (OgnlException e) {
            return null;
        } finally {
            stack.pop();
        }
    }

    @Override
    protected void addError(String path, String message) {
        if (isPropertyEmpty(path)) {
            action.addActionError(message);
        } else {
            action.addFieldError(path, message);
        }
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
