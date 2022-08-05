package app.programmatic.ui.common.aspect.prePersistProcessor.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import app.programmatic.ui.common.aspect.prePersistProcessor.ServiceOperationType;
import app.programmatic.ui.common.aspect.prePersistProcessor.impl.PrePersistProcessorAspect;
import app.programmatic.ui.common.model.EntityBase;
import app.programmatic.ui.common.tool.javabean.EntityChangesGetter;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks class interested in registering changes detected by {@link PrePersistProcessorAspect}.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface SubscribeForEntityChanges {
    /**
     * Callback method used for changes registration.
     * Signature: public void { callback method name }({@link EntityBase<?>} entity,
     *                                                 {@link ServiceOperationType} operationType,
     *                                                 {@link EntityChangesGetter} changes)
     * @return {entity fetcher name}
     */
    String processChangesMethod();
}
