package com.foros.validation.constraint;

import com.foros.validation.annotation.Validator;
import com.foros.validation.constraint.validator.ValidatorFactories;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Validator(factory = ValidatorFactories.Size.class)
public @interface SizeConstraint {

    int min() default 0;

    int max() default Integer.MAX_VALUE;

    String message() default "";

}
