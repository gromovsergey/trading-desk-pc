package com.foros.validation.annotation;

import com.foros.validation.strategy.ValidationMode;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Validate(validation = "Bean.bean", parameters = {"#fieldName", "#value", "#annotation.value()"})
public @interface CascadeValidation {

    ValidationMode value() default ValidationMode.DEFAULT;

}
