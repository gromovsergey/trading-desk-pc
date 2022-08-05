package app.programmatic.ui.common.validation.strategy;

import app.programmatic.ui.common.validation.ConstraintViolationBuilder;

public interface NullValidationStrategy {
    <E, T> boolean checkNotNull(E validated, String fieldName, ConstraintViolationBuilder<T> builder);
}
