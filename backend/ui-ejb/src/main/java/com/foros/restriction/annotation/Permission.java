package com.foros.restriction.annotation;

import com.foros.security.AccountRole;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Defines an action for some object type.
 * 
 * @see Permissions  
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {

    /**
     * Object type to define permissions for. This name is used to read/write permission from/to database.
     */
    String objectType();

    /**
     * The name of the action constrained by this permission. This name is used to read/write permission from/to database.
     */
    String action();
    
    /**
     * Determines whether this permission is parameterized or not. 
     * Parameterized permissions allow to store additional parameter, for example, to define access to specific object.
     */
    boolean parameterized() default false;

    /**
     * Determines account roles
     */
    AccountRole[] accountRoles();
}