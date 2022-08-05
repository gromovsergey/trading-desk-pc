package com.foros.framework;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class ViewValidationInterceptor extends AbstractInterceptor {
    public static String INPUT_VIEW = "input_view";

    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();

        // call validate method
        if (action instanceof ViewEditValidatable) {
            if (invocation.getInvocationContext().getName().endsWith("view")) {
                if (!((ViewEditValidatable)action).viewValidate()) {
                    return INPUT_VIEW;
                }
            } else if (invocation.getInvocationContext().getName().endsWith("edit")) {
                if (!((ViewEditValidatable)action).editValidate()) {
                    return INPUT_VIEW;
                }
            } else if (invocation.getInvocationContext().getName().endsWith("selectAccount")) {
                // here when an entity is being viewed/selected
                if (!((ViewEditValidatable)action).viewValidate()) {
                    return INPUT_VIEW;
                }
            }
        }

        return invocation.invoke();
    }
}
