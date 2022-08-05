package com.foros.aspect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks EJB session bean method to be used in EL expressions.  
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ElFunction {

    public static enum Namespace {

        GENERAL

    }

    /**
     * The name of the function as used in expressions. 
     * If specific 'name' is not provided method name will be used instead.
     */
    String name() default "";

    /**
     * The set of namespaces for the defined function. 
     * Determines where the function can be used (currently in {@link com.foros.restriction.annotation.Restriction} definition
     * and in {@link com.foros.restriction.annotation.Restrict} annotation).
     */
    Namespace[] namespaces() default { Namespace.GENERAL};
}