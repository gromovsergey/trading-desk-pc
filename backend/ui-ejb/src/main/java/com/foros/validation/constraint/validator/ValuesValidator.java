package com.foros.validation.constraint.validator;

import com.foros.util.StringUtil;

public class ValuesValidator extends AbstractValidator<Object, ValuesValidator> {

    private boolean ignoreCase;
    private String[] values;

    public ValuesValidator withIgnoreCase() {
        return withIgnoreCase(true);
    }

    public ValuesValidator withIgnoreCase(boolean ignore) {
        this.ignoreCase = ignore;
        return this;
    }

    public ValuesValidator withValues(String... values) {
        this.values = values;
        return this;
    }

    @Override
    protected void validateValue(Object object) {
        // null/empty values are valid for this constraint
        // use @RequiredConstraint to validate null/empty values
        if (object == null) {
            return;
        }

        String value = asString(object);
        
        if (StringUtil.isPropertyEmpty(value)) {
            return;
        }

        boolean equals = ignoreCase ?
                StringUtil.equalsWithIgnoreCase(value, values) :
                StringUtil.equalsWith(value, values);

        if (!equals) {
            addConstraintViolation("")
                    .withValue(value);
        }
    }

    private String asString(Object object) {
        if (object instanceof Enum) {
            return ((Enum) object).name();
        }

        return object.toString();
    }

}
