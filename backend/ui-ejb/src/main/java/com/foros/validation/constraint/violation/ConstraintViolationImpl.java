package com.foros.validation.constraint.violation;

import com.foros.validation.code.ForosError;

import java.io.Serializable;

public class ConstraintViolationImpl implements ConstraintViolation, Serializable {

    private final ForosError error;
    private final String message;
    private final Path path;
    private final Object value;
    private final String messageTemplate;

    public ConstraintViolationImpl(ForosError error, String message, Path path, Object value, String messageTemplate) {
        this.error = error;
        this.message = message;
        this.path = path;
        this.value = value;
        this.messageTemplate = messageTemplate;
    }

    @Override
    public ForosError getError() {
        return error;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    @Override
    public Path getPropertyPath() {
        return path;
    }

    @Override
    public Object getInvalidValue() {
        return value;
    }

    @Override
    public String toString() {
        return "violation " + error + " [" + message + "] for path: " + path;
    }
}
