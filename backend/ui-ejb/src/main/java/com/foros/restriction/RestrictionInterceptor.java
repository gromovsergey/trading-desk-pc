package com.foros.restriction;

import com.foros.restriction.invocation.RestrictionInvocationService;

import javax.ejb.EJB;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import org.springframework.core.BridgeMethodResolver;

public class RestrictionInterceptor {

    @EJB
    private RestrictionInvocationService restrictionInvocationService;

    @AroundInvoke
    public Object applySecurityCheck(InvocationContext inv) throws Exception {
        restrictionInvocationService.checkMethodRestrictions(inv.getTarget(), BridgeMethodResolver.findBridgedMethod(inv.getMethod()), inv.getParameters());
        return inv.proceed();
    }
}
