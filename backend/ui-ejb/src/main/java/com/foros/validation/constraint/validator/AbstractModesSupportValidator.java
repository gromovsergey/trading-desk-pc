package com.foros.validation.constraint.validator;

import com.foros.validation.strategy.ValidationMode;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>Support for validator, which can customize validation modes</p>
 *
 * @param <V> validation value type
 * @param <VT> validator type
 */
public abstract class AbstractModesSupportValidator<V, VT extends AbstractModesSupportValidator<V, VT>> extends AbstractValidator<V, VT> {

    private Set<ValidationMode> validationModes;
    private Set<ValidationMode> excludedValidationModes;

    public VT withModes(ValidationMode... modes) {
        validationModes = new HashSet<ValidationMode>(Arrays.asList(modes));
        return self();
    }

    public VT excludeModes(ValidationMode... modes) {
        excludedValidationModes = new HashSet<ValidationMode>(Arrays.asList(modes));
        return self();
    }

    @Override
    protected final void validateValue(V value) {
        boolean validationModesConstains =
                validationModes == null ||
                validationModes.isEmpty() ||
                validationModes.contains(context().getMode());

        boolean excludedValidationModesConstains =
                excludedValidationModes != null &&
                excludedValidationModes.contains(context().getMode());

        if (validationModesConstains && !excludedValidationModesConstains) {
            validateWithModes(value);
        }

    }

    protected abstract void validateWithModes(V value);
}
