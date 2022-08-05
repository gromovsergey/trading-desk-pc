package com.foros.validation.annotation;

import com.foros.validation.strategy.ValidationMode;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface ValidateBean {

    ValidationMode value() default ValidationMode.DEFAULT;
    
}
