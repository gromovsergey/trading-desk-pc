package com.foros.validation.annotation;

import com.foros.aspect.annotation.Aspect;
import com.foros.validation.constraint.validator.ValidatorFactory;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Aspect(reader = ValidationReaders.ValidatorAspectReader.class, index = Validation.class)
public @interface Validator {

    Class<? extends ValidatorFactory<?, ?, ?>> factory();
    
}
