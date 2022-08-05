package com.foros.validation.constraint.violation.parsing;

import com.foros.validation.code.ForosError;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.Path;

public class ParseErrorConstraintViolation implements ConstraintViolation {

    private String message;
    private Path propertyPath;
    private String value;
    private ForosError error;

    public ParseErrorConstraintViolation(String message,
                                         String propertyPath,
                                         String value,
                                         ForosError error) {
        this.message = message;
        this.error = error;
        this.propertyPath = Path.fromString(propertyPath);
        this.value = value;
    }

    @Override
    public ForosError getError() {
        return error;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Path getPropertyPath() {
        return propertyPath;
    }

    @Override
    public Object getInvalidValue() {
        return value;
    }
}
