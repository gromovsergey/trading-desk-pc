package app.programmatic.ui.common.validation.strategy;

import app.programmatic.ui.common.validation.ConstraintViolationBuilder;

public class NullPermittedValidationStrategy implements NullValidationStrategy {
    @Override
    public <E, T> boolean checkNotNull(E validated, String fieldName, ConstraintViolationBuilder<T> builder) {
        return validated != null;
    }
}
