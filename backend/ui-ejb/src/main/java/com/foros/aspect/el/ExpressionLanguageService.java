package com.foros.aspect.el;

import com.foros.aspect.annotation.ElFunction;

import java.util.Map;
import javax.ejb.Local;

/**
 * Service for evaluating EL expressions. 
 */
@Local
public interface ExpressionLanguageService {

    /**
     * Evaluates compiled EL expression.
     * @param expression expression returned by {@link #compileExpression}
     * @param parameters the set of defined variables to use in expression
     * @param namespace EL functions namespace.
     * @return the product of evaluation
     * @throws ExpressionException
     */
    Object evaluate(Expression expression,
                    Map<String, Object> parameters,
                    ElFunction.Namespace namespace) throws ExpressionException;

    /**
     * Compiles EL expression into some internal format.
     * @param expression EL expression to compile
     * @return compiled expression
     * @throws ExpressionException
     */
    Expression compileExpression(String expression) throws ExpressionException;

}
