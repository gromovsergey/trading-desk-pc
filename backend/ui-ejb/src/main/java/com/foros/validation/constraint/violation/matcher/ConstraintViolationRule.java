package com.foros.validation.constraint.violation.matcher;

import com.foros.validation.code.ForosError;

import ognl.Ognl;
import ognl.OgnlException;

public class ConstraintViolationRule {
    private ConstraintViolationQuery query;
    private ForosError forosError;
    private Object pathExpression;
    private Object messageExpression;

    public ConstraintViolationRule(String queryString, ForosError forosError, String pathExpression, String messageExpression) {
        this.query = ConstraintViolationQuery.compile(queryString);
        try {
            this.pathExpression = Ognl.parseExpression(pathExpression);
            this.messageExpression = Ognl.parseExpression(messageExpression);
        } catch (OgnlException e) {
            throw new RuntimeException(e);
        }
        this.forosError = forosError;
    }

    public ConstraintViolationQuery getQuery() {
        return query;
    }


    public Object getPathExpression() {
        return pathExpression;
    }

    public Object getMessageExpression() {
        return messageExpression;
    }

    public ForosError getForosError() {
        return forosError;
    }

}
