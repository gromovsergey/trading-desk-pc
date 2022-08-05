package com.foros.restriction.invocation;

import java.lang.reflect.Method;
import javax.ejb.Local;

/**
 * Session bean to process method annotations. 
 */
@Local
public interface RestrictionInvocationService {

    /**
     * Asserts restrictions defined on method
     * @param target object for which the method is called
     * @param method method
     * @param params method parameters
     */
    void checkMethodRestrictions(Object target, Method method, Object[] params);

    void checkWebMethodRestrictions(Object target, Method method, Object[] params);

}
