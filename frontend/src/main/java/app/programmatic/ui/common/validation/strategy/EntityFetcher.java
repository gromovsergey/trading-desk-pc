package app.programmatic.ui.common.validation.strategy;

public interface EntityFetcher<T, E> {
    T fetch(E id);
}
