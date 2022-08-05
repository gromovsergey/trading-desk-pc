package com.foros.validation.annotation;

import com.foros.aspect.annotation.AspectDeclaration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@AspectDeclaration(
        reader = ValidationReaders.ValidationsNameReader.class,
        index = Validation.class,
        convention = "^([A-Z]\\w*?)Validations$"
)
public @interface Validations {

    String value() default "";

}
