package com.foros.validation;

import javax.ejb.EJB;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import org.springframework.core.BridgeMethodResolver;

public class ValidationInterceptor {

    @EJB
    private ValidationInvocationService validationInvocationService;

    @AroundInvoke
    public Object around(InvocationContext context) throws Exception {

        validationInvocationService.validate(context.getTarget(), BridgeMethodResolver.findBridgedMethod(context.getMethod()), context.getParameters());

        return context.proceed();
    }

}
