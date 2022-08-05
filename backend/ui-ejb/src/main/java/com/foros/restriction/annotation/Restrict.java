package com.foros.restriction.annotation;

import com.foros.aspect.annotation.Aspect;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows to attach restriction to an action. Action in this case is a method of an EJB session bean.
 * 
 * @author pavel
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Aspect(reader = RestrictionReaders.RestrictAspectReader.class, index = Restriction.class)
public @interface Restrict {
    
    /**
     * The name of the restriction to attach.
     */
    String restriction();
    
    /**
     * The array of restriction parameters. Each parameter is an EL expression defined in context of current EJB method.
     */
    String[] parameters() default {};

}
