package app.programmatic.ui.audienceresearch.validation;

import app.programmatic.ui.common.validation.ValidateMethod;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = { AudienceResearchValidationServiceImpl.class })
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,
        ElementType.FIELD,
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.PARAMETER})
@NotNull
public @interface ValidateAudienceResearch {
    ValidateMethod value();

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
