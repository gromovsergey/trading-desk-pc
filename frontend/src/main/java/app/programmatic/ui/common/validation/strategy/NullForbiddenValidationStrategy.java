package app.programmatic.ui.common.validation.strategy;

import app.programmatic.ui.common.validation.ConstraintViolationBuilder;

public class NullForbiddenValidationStrategy implements NullValidationStrategy {
    @Override
    public <E, T> boolean checkNotNull(E validated, String fieldName, ConstraintViolationBuilder<T> builder) {
        if (validated != null) {
            return true;
        }

        builder.addViolationDescription(fieldName, "entity.field.error.mandatory");
        return false;
    }
}
