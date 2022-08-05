package com.foros.framework;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsStatics;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class CSRFInterceptor extends AbstractInterceptor {

    private boolean isReadOnly;

    public void setReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    private boolean isReadOnly(ActionInvocation invocation) {
        if (isReadOnly) {
            return true;
        }
        String methodName = invocation.getProxy().getMethod();
        try {
            Method m = invocation.getAction().getClass().getMethod(methodName, new Class[0]);
            return m.getAnnotation(ReadOnly.class) != null;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ActionContext ac = invocation.getInvocationContext();

        HttpServletRequest request = (HttpServletRequest) ac.get(StrutsStatics.HTTP_REQUEST);

        PWSHelper.checkCSRFConstraint(request, isReadOnly(invocation));

        return invocation.invoke();
    }

}
