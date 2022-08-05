package app.programmatic.ui.common.aspect.prePersistProcessor.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import app.programmatic.ui.common.aspect.prePersistProcessor.impl.PrePersistProcessorAspect;
import app.programmatic.ui.common.model.EntityBase;
import app.programmatic.ui.common.tool.javabean.emptyValues.EmptyValuesStrategy;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks class to be processed by {@link PrePersistProcessorAspect}.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface PrePersistAwareService {
    /**
     * Returns name of method to be used for fetching an entity by {identifier}
     * Updated entity will be compared with fetched entity
     * Comparing result depends on {@link EmptyValuesStrategy}
     * Signature: public {@link EntityBase} {entity fetcher name}(Object id)
     * @return {entity fetcher name}
     */
    String storedValueGetter();

    /**
     * Returns name of method to be used for fetching an entity with default values
     * New entity will be compared with fetched entity
     * Comparing result depends on {@link EmptyValuesStrategy}
     * Signature: public {@link EntityBase} {default entity fetcher name}()
     * @return {default entity fetcher name}
     */
    String defaultValueGetter() default "";
}
