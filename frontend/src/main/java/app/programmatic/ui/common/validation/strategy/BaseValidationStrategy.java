package app.programmatic.ui.common.validation.strategy;

import app.programmatic.ui.common.validation.ConstraintViolationBuilder;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseValidationStrategy implements ValidationStrategy {
    public static final String DEFAULT_ID_FIELD_NAME = "id";

    protected NullValidationStrategy nullValidationStrategy;

    protected BaseValidationStrategy(NullValidationStrategy nullValidationStrategy) {
        this.nullValidationStrategy = nullValidationStrategy;
    }

    @Override
    public <E, T> void checkId(E id, ConstraintViolationBuilder<T> builder) {
        checkId(id, DEFAULT_ID_FIELD_NAME, builder);
    }

    @Override
    public <E, T> T checkBean(E id, ConstraintViolationBuilder<T> builder, EntityFetcher<T, E> fetcher) {
        return checkBean(id, DEFAULT_ID_FIELD_NAME, builder, fetcher);
    }

    @Override
    public <E, T> void checkInitialValue(T validated, T initial, String fieldName, ConstraintViolationBuilder<E> builder) {
        Set<T> initials = new HashSet<T>(1);
        initials.add(initial);
        checkInitialValue(validated, initials, fieldName, builder);
    }

    @Override
    public <E, T> boolean checkNotNull(E validated, String fieldName, ConstraintViolationBuilder<T> builder) {
        return nullValidationStrategy.checkNotNull(validated, fieldName, builder);
    }
}
