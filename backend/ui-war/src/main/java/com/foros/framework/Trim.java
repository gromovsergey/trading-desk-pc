package com.foros.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a bean (e.g. Form in Struts 1 or any other bean) or Stuts2 Action properties to be trimed or not.
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Trim {
    /**
     * @return A set of properties which are expected to be trimed
     */
    String[] include() default {};

    /**
     * @return A set of properties which are not expected to be trimed
     */
    String[] exclude() default {};
}
