package app.programmatic.ui.common.validation.strategy;

import app.programmatic.ui.common.validation.ConstraintViolationBuilder;

import java.util.Set;

public class UpdateValidationStrategy extends BaseValidationStrategy {

    public UpdateValidationStrategy(NullValidationStrategy nullValidationStrategy) {
        super(nullValidationStrategy);
    }

    @Override
    public <E, T> void checkId(E id, String fieldName, ConstraintViolationBuilder<T> builder) {
        if (id == null) {
            builder.addViolationDescription(fieldName, "entity.field.error.mandatory");
        }
    }

    @Override
    public <E, T> T checkBean(E id, String fieldName, ConstraintViolationBuilder<T> builder, EntityFetcher<T, E> fetcher) {
        if (id == null) {
            builder.addViolationDescription(fieldName, "entity.field.error.mandatory");
            return null;
        }

        T entity = fetcher.fetch(id);
        if (entity != null) {
            return entity;
        }
        builder.addViolationDescription(fieldName, "entity.error.notFound", String.valueOf(id));
        return null;
    }

    @Override
    public <E, T> void checkValueNotChanged(T validated, T existing, String fieldName, ConstraintViolationBuilder<E> builder) {
        if (existing == null) {
            // Currently null values are not supported and existence should be checked elsewhere
            return;
        }

        if (!existing.equals(validated)) {
            builder.addViolationDescription(fieldName, "entity.field.error.changeForbidden");
        }
    }

    @Override
    public <E, T> void checkInitialValue(T validated, Set<T> initials, String fieldName, ConstraintViolationBuilder<E> builder) {
        // Do nothing
    }
}
