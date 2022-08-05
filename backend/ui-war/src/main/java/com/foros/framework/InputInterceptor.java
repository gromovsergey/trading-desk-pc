package com.foros.framework;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import java.util.Map;

/**
 * If there is a modified model on action's session sets that model on a action.
 * @author alexey_koloskov
 */
public class InputInterceptor extends ParametersInterceptor {
    public String intercept(ActionInvocation invocation) throws Exception {
        Map session = invocation.getInvocationContext().getSession();

        Object params = session.remove(InputResult.INPUT_PARAMS);
        if (params != null) {
            invocation.getInvocationContext().setParameters((Map)params);
        }

        Object errors = session.remove(InputResult.INPUT_ERRORS);
        Object action = invocation.getAction();
        if (errors != null && action instanceof ValidationAware) {
            ((ValidationAware)action).setFieldErrors((Map)errors);
        }

        return super.intercept(invocation);
    }
}
