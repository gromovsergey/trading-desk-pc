package com.foros.framework.struts2validator;

import com.opensymphony.xwork2.validator.ValidationException;

public abstract class AbstractRangeValidator<T> extends Struts2FieldValidatorSupport {

    public void validate(Object object) throws ValidationException {
        Object obj = getFieldValue(getFieldName(), object);
        T value = (T) obj;

        // if there is no value - don't do comparison
        // if a value is required, a required validator should be added to the field
        if (value == null) {
            return;
        }

        Comparable<T> min = getMinComparatorValue();
        Comparable<T> max = getMaxComparatorValue();

        // only check for a minimum/maximum value if the min/max parameter is set
        if (min != null && min.compareTo(value) > 0) {
            addError(getFieldName(), object);
        } else if (max != null && max.compareTo(value) < 0) {
            addError(getFieldName(), object);
        }
    }

    protected abstract Comparable<T> getMaxComparatorValue();

    protected abstract Comparable<T> getMinComparatorValue();
}
