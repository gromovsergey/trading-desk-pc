package com.foros.validation.strategy;

import com.foros.validation.ValidationContext;

public class DefaultValidationStrategy implements ValidationStrategy {

    @Override
    public boolean isReachable(ValidationContext context, String fieldName) {
        return true;
    }

}
