package app.programmatic.ui.localization.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;

@Documented
@Constraint(validatedBy = { LocalizationValidationServiceImpl.class }
)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@NotNull
public @interface ValidateLocalization {
    LocalizationValidateMethod value();

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
