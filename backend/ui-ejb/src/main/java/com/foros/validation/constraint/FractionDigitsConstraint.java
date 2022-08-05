package com.foros.validation.constraint;

import com.foros.validation.annotation.Validator;
import com.foros.validation.constraint.validator.ValidatorFactories;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Checks number of fraction digits of decimal number
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Validator(factory = ValidatorFactories.FractionDigits.class)
public @interface FractionDigitsConstraint {
    int value();
}
