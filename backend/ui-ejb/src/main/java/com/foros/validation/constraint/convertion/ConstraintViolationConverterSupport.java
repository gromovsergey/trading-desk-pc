package com.foros.validation.constraint.convertion;

import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationMatcher;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;

import java.util.Collection;
import java.util.List;

public abstract class ConstraintViolationConverterSupport {

    public static final ConstraintViolationRule DEFAULT_RULE = new ConstraintViolationRule("(#path)", null, "groups[0]", "violation.message");

    public ConstraintViolationConverterSupport() {
    }

    public void applyRules(List<ConstraintViolationRule> rules, ConstraintViolation violation) {
        for (ConstraintViolationRule rule : rules) {
            if (applyRule(rule, violation)) {
                return;
            }
        }
        applyRule(DEFAULT_RULE, violation);
    }

    public boolean applyRule(ConstraintViolationRule rule, ConstraintViolation violation) {
        ConstraintViolationMatcher matcher = rule.getQuery().matcher(violation);
        if (!matcher.matches()) {
            return false;
        }

        if (rule.getForosError() != null && !rule.getForosError().equals(violation.getError())) {
            return false;
        }

        String message = evaluateExpression(rule.getMessageExpression(), violation, matcher.groups());
        if (message == null) {
            throw new RuntimeException("Can't find message for " + violation);
        }
        String path = evaluateExpression(rule.getPathExpression(), violation, matcher.groups());

        addError(path, message);

        return true;
    }

    protected abstract String evaluateExpression(Object expression, ConstraintViolation violation, String[] groups);

    protected abstract void addError(String path, String message);

    public void applyRules(List<ConstraintViolationRule> rules, Collection<ConstraintViolation> violations) {
        for (ConstraintViolation violation : violations) {
            applyRules(rules, violation);
        }
    }
}
