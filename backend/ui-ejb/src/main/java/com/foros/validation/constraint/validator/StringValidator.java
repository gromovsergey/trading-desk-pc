package com.foros.validation.constraint.validator;

import com.foros.util.StringUtil;

public class StringValidator extends AbstractValidator<String, StringValidator> {

    private int size;

    public StringValidator withSize(int size) {
        this.size = size;
        return this;
    }

    @Override
    protected void validateValue(String value) {
        if (!isStringValid(value)) {
            addConstraintViolation("errors.field.maxlength")
                    .withParameters(size)
                    .withValue(value);

        }
        context().validator(XmlAllowableValidator.class)
                .withPath(path())
                .validate(value);

    }

    private boolean isStringValid(String value) {
        return StringUtil.isPropertyEmpty(value) || value.length() <= size;

    }
}
