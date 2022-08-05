package com.foros.restriction.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the set of permission
 * 
 * <p>Specific permission identified by two names:
 * <ol>
 *  <li>The name of action defined for the object type (defined by {@link Permission#action})</li>
 * </ol>
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Permissions {

    /**
     * The set of permissions
     */
    Permission[] value();
}
