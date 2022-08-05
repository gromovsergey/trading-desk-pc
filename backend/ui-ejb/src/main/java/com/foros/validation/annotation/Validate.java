package com.foros.validation.annotation;

import com.foros.aspect.annotation.Aspect;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.interceptor.InterceptorBinding;


@Documented
@Inherited
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Aspect(reader = ValidationReaders.ValidateAspectReader.class, index = Validation.class)
public @interface Validate {

    /**
     * The name of the validation to attach.
     */
    String validation();

    /**
     * The array of validation parameters. Each parameter is an EL expression defined in context of current EJB method.
     */
    String[] parameters() default {};

}
