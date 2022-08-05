package com.foros.framework;

import com.foros.restriction.invocation.RestrictionInvocationService;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import java.lang.reflect.Method;
import javax.ejb.EJB;

public class SecurityInterceptor extends AbstractInterceptor {

    @EJB
    private RestrictionInvocationService restrictionInvocationService;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        Class<?> actionClass = action.getClass();

        String methodName = invocation.getProxy().getMethod();
        Method method = actionClass.getMethod(methodName);

        restrictionInvocationService.checkWebMethodRestrictions(action, method, null);

        return invocation.invoke();
    }
}
