package app.programmatic.ui.common.aspect.forosApiViolation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.constraints.NotNull;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@NotNull
public @interface ForosApiViolationsAware {
    String value();
}
