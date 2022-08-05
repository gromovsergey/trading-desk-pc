package com.foros.aspect.el;

/**
 * Compiled EL expression.
 * 
 *  @see ExpressionLanguageService#compileExpression
 */
public class Expression {

    private Object expression;

    public Expression(Object expression) {
        this.expression = expression;
    }

    public Object getExpression() {
        return expression;
    }

}
