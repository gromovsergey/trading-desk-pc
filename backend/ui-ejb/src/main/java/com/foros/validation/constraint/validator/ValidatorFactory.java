package com.foros.validation.constraint.validator;

import java.lang.annotation.Annotation;

/**
 * <p>Factory encapsulate logic of validator creation by constraint annotation</p>
 *
 * @param <T> constraint annotation type
 * @param <V> validation value type
 * @param <VT> validator type
 */
public interface ValidatorFactory<T extends Annotation, V, VT extends Validator<V, VT>> {

    /**
     * <p>Create new instance of validator initialized with annotation parameters</p>
     *
     * @param annotation constraint annotation
     * @return new validator instance
     */
    VT validator(T annotation);

}
