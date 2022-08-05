package com.foros.aspect.el;

import ognl.Evaluation;
import ognl.OgnlException;

public class ExpressionException extends Exception {

    private Evaluation evaluation;
    private Throwable reason;

    public ExpressionException(OgnlException e) {
        super(e.getMessage(), e.getReason());
        evaluation = e.getEvaluation();
        reason = e.getReason();
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "\n Evaluation: " + (evaluation!=null?evaluation.toString():"");
    }

}
