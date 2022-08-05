package com.foros.changes;

import javax.ejb.EJB;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 * EJB interceptor for initialization changes capturing mechanism.
 * To enable capture changes for a session bean method use <code>javax.interceptor.Interceptors</code>
 * annotation on the session bean method I.e.:
 * <code>
 * &#064;Interceptors({CaptureChangesInterceptor.class})
 * </code>
 */
public class CaptureChangesInterceptor {

    @EJB
    private ChangesService changesService;

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        changesService.initialize();
        return context.proceed();
    }

}
