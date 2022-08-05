package com.foros.validation.constraint.validator;

import java.util.regex.Pattern;

public class PatternValidator extends AbstractMessageSupportValidator<String, PatternValidator> {

    private Pattern pattern;

    public PatternValidator withPattern(String pattern) {
        this.pattern = Pattern.compile(pattern);
        return this;
    }

    public PatternValidator withPattern(Pattern pattern) {
        this.pattern = pattern;
        return this;
    }

    @Override
    protected void validateValue(String value) {
        if (value == null) {
            return;
        }

        if (!pattern.matcher(value).matches()) {
            addConstraintViolation()
                    .withValue(value);
        }
    }

}
