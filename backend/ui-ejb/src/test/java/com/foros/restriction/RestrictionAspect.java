package com.foros.restriction;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;

public class RestrictionAspect {

    @Autowired
    RestrictionInterceptor restrictionInterceptor;

    public Object doRestrictionCheck(ProceedingJoinPoint pjp) throws Exception {
        InvocationContextAdapter invocationContextAdapter = new InvocationContextAdapter(pjp);
        return restrictionInterceptor.applySecurityCheck(invocationContextAdapter);
    }
}
