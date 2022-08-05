package com.foros.validation.constraint.validator;

import com.foros.util.NumberUtil;
import java.math.BigDecimal;

public class FractionDigitsValidator extends AbstractValidator<Number, FractionDigitsValidator> {

    private int fraction;

    public FractionDigitsValidator withFraction(int fraction) {
        this.fraction = fraction;
        return this;
    }

    @Override
    protected void validateValue(Number number) {
        if (!isValid(number)) {
            addConstraintViolation("errors.field.maxFractionDigits")
                    .withParameters(fraction)
                    .withValue(number);
        }
    }

    private boolean isValid(Number value) {
        if (value == null) {
            return true;
        }

        BigDecimal decimal = NumberUtil.toBigDecimal(value).stripTrailingZeros();

        int fractionPartLength = decimal.scale();

        return fraction >= fractionPartLength;
    }

}
