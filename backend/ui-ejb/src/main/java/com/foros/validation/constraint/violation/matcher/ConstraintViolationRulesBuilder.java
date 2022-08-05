package com.foros.validation.constraint.violation.matcher;

import com.foros.validation.code.ForosError;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ConstraintViolationRulesBuilder {
    private final LinkedList<ConstraintViolationRule> rules = new LinkedList<>();

    public ConstraintViolationRulesBuilder add(String query, String pathExpression, String messageExpression) {
        return match(query).apply(pathExpression, messageExpression);
    }

    public ConstraintViolationRulesBuilder add(String query, String messageExpression) {
        return match(query).apply(messageExpression);
    }

    public Match match(String query) {
        return match(query, null);
    }

    public Match match(String query, ForosError errorCode) {
        return new Match(errorCode, query);
    }

    public ConstraintViolationRule rule() {
        return rules.peekLast();
    }

    public List<ConstraintViolationRule> rules() {
        return Collections.unmodifiableList(rules);
    }

    public class Match {
        private final String query;
        private final ForosError errorCode;

        public Match(ForosError errorCode, String query) {
            this.errorCode = errorCode;
            this.query = query;
        }

        public ConstraintViolationRulesBuilder apply(String messageExpression) {
            return apply("null", messageExpression);
        }

        public ConstraintViolationRulesBuilder apply(String pathExpression, String messageExpression) {
            rules.add(new ConstraintViolationRule(query, errorCode, pathExpression, messageExpression));
            return ConstraintViolationRulesBuilder.this;
        }
    }
}
