package com.foros.restriction.annotation;

import com.foros.aspect.annotation.AspectDeclaration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate EJB session bean interface with this annotation to state that the bean contains restriction methods.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@AspectDeclaration(
        reader = RestrictionReaders.RestrictionsNameReader.class,
        index = Restriction.class,
        convention = "^([A-Z]\\w*?)Restrictions$"
)
public @interface Restrictions {

    /**
     * Redefines object type if specified. 
     * By default there is a naming convention for object type: 
     * object type is determined from session bean interface name (which shall be like "*Restrictions") by removing 
     * "Restrictions" suffix. For example, object type determined from ColocationRestrictions class would be "Colocation".
     */
    String value() default "";
    
}