package com.foros.action.xml.options.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: Boris Vanin
 * Date: 05.12.2008
 * Time: 18:51:26
 * Version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface AccountId {

    boolean isPair() default true;

}
