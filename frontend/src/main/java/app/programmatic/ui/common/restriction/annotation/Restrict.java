package app.programmatic.ui.common.restriction.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Restrict {

    String restriction();

    String[] parameters() default {};

}