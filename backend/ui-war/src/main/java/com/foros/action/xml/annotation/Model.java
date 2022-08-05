package com.foros.action.xml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: Boris Vanin
 * Date: 08.12.2008
 * Time: 19:27:30
 * Version: 1.0
 * <p/>
 * Annotate method or field for fething it by XmlResult
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Model {
}
