package com.foros.validation.strategy;

import com.foros.validation.ValidationContext;

public class CreateValidationStrategy implements ValidationStrategy {

    @Override
    public boolean isReachable(ValidationContext context,  String fieldName) {
        return true;
    }

}
