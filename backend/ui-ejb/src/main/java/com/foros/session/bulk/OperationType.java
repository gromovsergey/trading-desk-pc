package com.foros.session.bulk;

import com.foros.validation.strategy.ValidationMode;

public enum OperationType {

    CREATE(ValidationMode.CREATE), UPDATE(ValidationMode.UPDATE);

    private ValidationMode mode;

    OperationType(ValidationMode mode) {
        this.mode = mode;
    }

    public ValidationMode toValidationMode() {
        return mode;
    }

}
