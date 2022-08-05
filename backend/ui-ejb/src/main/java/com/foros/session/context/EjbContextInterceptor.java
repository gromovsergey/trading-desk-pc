package com.foros.session.context;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class EjbContextInterceptor {

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        try {
            EjbContext.setContextOn();
            return context.proceed();
        } finally {
            EjbContext.setContextOff();
        }
    }


}
