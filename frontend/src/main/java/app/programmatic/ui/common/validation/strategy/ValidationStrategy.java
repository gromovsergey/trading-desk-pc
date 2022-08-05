package app.programmatic.ui.common.validation.strategy;

import app.programmatic.ui.common.validation.ConstraintViolationBuilder;

import java.util.Set;

public interface ValidationStrategy {
    <E, T> void checkId(E id, ConstraintViolationBuilder<T> builder);

    <E, T> void checkId(E id, String fieldName, ConstraintViolationBuilder<T> builder);

    <E, T> T checkBean(E id, ConstraintViolationBuilder<T> builder, EntityFetcher<T, E> fetcher);

    <E, T> T checkBean(E id, String fieldName, ConstraintViolationBuilder<T> builder, EntityFetcher<T, E> fetcher);

    <E, T> void checkValueNotChanged(T validated, T existing, String fieldName, ConstraintViolationBuilder<E> builder);

    <E, T> void checkInitialValue(T validated, T initial, String fieldName, ConstraintViolationBuilder<E> builder);

    <E, T> void checkInitialValue(T validated, Set<T> initials, String fieldName, ConstraintViolationBuilder<E> builder);

    <E, T> boolean checkNotNull(E validated, String fieldName, ConstraintViolationBuilder<T> builder);
}
