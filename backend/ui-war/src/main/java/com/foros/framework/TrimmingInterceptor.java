package com.foros.framework;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import java.util.Map;

/**
 * This interceptor trims incoming parameter's
 * (returns parameter values with leading and trailing whitespace omitted).<p/>
 * If {@link Trim} annotation is presented in a corresponding Struts2 action,
 * then parameters will be filtered depending on excluded/included fields set.
 * By default all incomming parameters will be trimmed.
 * <p/>
 * Note: Trimming will be performed before the parameters set into Action bean by
 * {@link com.opensymphony.xwork2.interceptor.ParametersInterceptor}.
 *
 */
public class TrimmingInterceptor extends AbstractInterceptor {
    // Configuration parameter responsible for default intercepter behaviour
    private boolean trimAll = true;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        Map parameters = invocation.getInvocationContext().getParameters();

        if (action == null || parameters == null || parameters.isEmpty()) {
            return invocation.invoke(); // Skip proccessing...
        }

        ParametersPreprocessingUtil.trimParameters(action, parameters, trimAll, new Struts2ParameterTrimmer());

        return invocation.invoke();
    }

    public void setTrimAll(boolean trimAll) {
        this.trimAll = trimAll;
    }
}
