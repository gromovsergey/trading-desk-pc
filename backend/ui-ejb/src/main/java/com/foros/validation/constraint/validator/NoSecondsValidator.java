package com.foros.validation.constraint.validator;

import java.util.Calendar;
import java.util.Date;

public class NoSecondsValidator extends AbstractValidator<Date, NoSecondsValidator> {

    @Override
    protected void validateValue(Date value) {
        if (!isValid(value)) {
            addConstraintViolation("errors.field.hasSeconds")
                    .withValue(value);
        }
    }

    private boolean isValid(Date date) {
        if (date == null) {
            return true;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND) == 0 && calendar.get(Calendar.MILLISECOND) == 0;
    }
}