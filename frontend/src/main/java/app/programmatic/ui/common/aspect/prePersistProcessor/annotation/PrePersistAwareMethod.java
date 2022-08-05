package app.programmatic.ui.common.aspect.prePersistProcessor.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import app.programmatic.ui.common.aspect.prePersistProcessor.impl.PrePersistProcessorAspect;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks method to be processed by {@link PrePersistProcessorAspect}.
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface PrePersistAwareMethod {
}
