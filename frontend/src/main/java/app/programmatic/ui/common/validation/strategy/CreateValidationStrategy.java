package app.programmatic.ui.common.validation.strategy;

import app.programmatic.ui.common.validation.ConstraintViolationBuilder;

import java.util.Set;
import java.util.stream.Collectors;

public class CreateValidationStrategy extends BaseValidationStrategy {

    public CreateValidationStrategy(NullValidationStrategy nullValidationStrategy) {
        super(nullValidationStrategy);
    }

    @Override
    public <E, T> void checkId(E id, String fieldName, ConstraintViolationBuilder<T> builder) {
        if (id != null) {
            builder.addViolationDescription(fieldName, "entity.id.error.excessive");
        }
    }

    @Override
    public <E, T> T checkBean(E id, String fieldName, ConstraintViolationBuilder<T> builder, EntityFetcher<T, E> fetcher) {
        checkId(id, fieldName, builder);
        return null;
    }

    @Override
    public <E, T> void checkValueNotChanged(T validated, T existing, String fieldName, ConstraintViolationBuilder<E> builder) {
        // Do nothing
    }

    @Override
    public <E, T> void checkInitialValue(T validated, Set<T> initials, String fieldName, ConstraintViolationBuilder<E> builder) {
        if (validated == null || !initials.contains(validated)) {
            String param = initials.stream()
                    .map( value -> value.toString() )
                    .collect(Collectors.joining(", "));
            builder.addViolationDescription(fieldName, "entity.field.error.oneOf", param);
        }
    }
}
