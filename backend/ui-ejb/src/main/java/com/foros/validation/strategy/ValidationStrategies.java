package com.foros.validation.strategy;

import java.util.Collection;

public final class ValidationStrategies {

    private static final ValidationStrategy DEFAULT = new DefaultValidationStrategy();

    private static final ValidationStrategy CREATE = new CreateValidationStrategy();
    private static final ValidationStrategy UPDATE = new UpdateValidationStrategy();

    private ValidationStrategies() {
    }

    public static ValidationStrategy byValidationMode(ValidationMode validationMode) {
        switch (validationMode) {
            case UPDATE:
                return update();
            case CREATE:
                return create();
            default:
                return defaultStrategy();
        }
    }

    public static ValidationStrategy defaultStrategy() {
        return DEFAULT;
    }

    public static ValidationStrategy update() {
        return UPDATE;
    }

    public static ValidationStrategy create() {
        return CREATE;
    }

    public static ValidationStrategy exclude(Collection<String> properties) {
        return new ExcludeValidationStrategy(properties);
    }

    public static ValidationStrategy chain(ValidationStrategy strategy, ValidationStrategy additionalStrategy) {
        return new ValidationStrategyChain()
                .add(strategy)
                .add(additionalStrategy);
    }
}
