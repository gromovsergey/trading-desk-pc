package com.foros.validation.constraint.validator;

import com.foros.validation.ValidationException;
import java.util.Collection;

public class SizeValidator extends AbstractMessageSupportValidator<Object, SizeValidator> {

    private int min = 0;
    private int max = Integer.MAX_VALUE;

    public SizeValidator withMin(int min) {
        this.min = min;
        return this;
    }

    public SizeValidator withMax(int max) {
        this.max = max;
        return this;
    }

    @Override
    public void validateValue(Object value) {
        if (value == null) {
            return;
        }

        Number number = null;
        
        if (value instanceof Number) {
            number = (Number) value;
        } else if (value instanceof Collection) {
            number = ((Collection) value).size();
        } else if (value instanceof String) {
            number = ((String) value).length();
        }

        if (number == null) {
            throw new ValidationException("SizeConstraint not applicable for " + value.getClass().getName());
        }

        validateNumber(number);
    }

    private void validateNumber(Number value) {
        int intValue = value.intValue();

        boolean defaultMessage = !isMessageDefined();

        boolean isLess = min > intValue;
        boolean isGreater = max < intValue;

        if (defaultMessage) {
            if (isLess) {
                addConstraintViolation("errors.field.less")
                        .withParameters(min)
                        .withValue(value);
            }

            if (isGreater) {
                addConstraintViolation("errors.field.notgreater")
                        .withParameters(max)
                        .withValue(value);
            }
        } else {
            if (isLess || isGreater) {
                addConstraintViolation()
                        .withParameters(min, max)
                        .withValue(value);
            }
        }
    }

}