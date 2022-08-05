package com.foros.validation;

import javax.ejb.EJB;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import org.springframework.core.BridgeMethodResolver;

public class ValidateBeanInterceptor {

    @EJB
    private ValidationService validationService;

    @AroundInvoke
    public Object validate(InvocationContext context) throws Exception {
        validationService
                .validateParameters(
                        BridgeMethodResolver.findBridgedMethod(context.getMethod()),
                        context.getParameters())
                .throwIfHasViolations();

        return context.proceed();
    }

}
