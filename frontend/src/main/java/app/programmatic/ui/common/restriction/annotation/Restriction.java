package app.programmatic.ui.common.restriction.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Restriction {

    String value();

}