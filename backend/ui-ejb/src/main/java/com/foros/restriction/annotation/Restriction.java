package com.foros.restriction.annotation;

import com.foros.aspect.annotation.AspectDeclaration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Designate session bean method as restriction that can be used through RestrictionService. 
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@AspectDeclaration(
        reader = RestrictionReaders.RestrictionNameReader.class,
        index = Restriction.class,
        convention = "^can([A-Z]\\w*)$"
)
public @interface Restriction {

    /**
     * Redefines restriction name.
     * 
     * <p>*NOTE*: Specifying value for Restriction annotation redefines both full 
     * restriction name (both object type and action).</p>
     * 
     * <p>By default there is a naming convention for action names:
     * action name is determined from session bean method name which shall have the name like: "can*".
     * For example, action name for the "canView()" method would be "view".
     * Full restriction name is the joined from object type and action name as follows: 
     * [object type].[action name]. For example, "Colocation.view".</p>
     */
    String value() default "";

}
