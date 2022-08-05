package com.foros.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a bean (e.g. Form in Struts 1 or any other bean) properties where spaces should be raplaced with nbsps.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NumericField {

    /**
     * @return A set of properties where spaces are expected to be replaced with nbsps.
     */
    public String[] value();
}
