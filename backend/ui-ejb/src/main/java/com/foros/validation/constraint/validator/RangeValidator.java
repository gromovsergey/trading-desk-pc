package com.foros.validation.constraint.validator;

import com.foros.util.NumberUtil;
import com.foros.util.StringUtil;
import com.foros.validation.constraint.violation.ConstraintViolationBuilder;
import java.math.BigDecimal;

public class RangeValidator extends AbstractValidator<Number, RangeValidator> {

    private BigDecimal min;
    private BigDecimal max;

    private boolean isValid(Number value) {
        if (value == null) {
            return true;
        }

        BigDecimal decimal = NumberUtil.toBigDecimal(value);

        if (min != null && min.compareTo(decimal) > 0) {
            return false;
        }

        if (max != null && max.compareTo(decimal) < 0) {
            return false;
        }

        return true;
    }

    public RangeValidator withMin(BigDecimal min) {
        this.min = min;
        return this;
    }

    public RangeValidator withMin(BigDecimal min, int fraction) {
        return withMin(NumberUtil.addFraction(min, fraction));
    }

    public RangeValidator withMax(BigDecimal max) {
        this.max = max;
        return this;
    }

    public RangeValidator withMax(BigDecimal max, int fraction) {
        return withMax(NumberUtil.subtractFraction(max, fraction));
    }

    @Override
    public void validateValue(Number number) {
        if (isValid(number)) {
            return;
        }

        String minStr = StringUtil.getLocalizedBigDecimal(min);
        String maxStr = StringUtil.getLocalizedBigDecimal(max);

        addConstraintViolation(minStr, maxStr)
                .withValue(number);
    }

    private ConstraintViolationBuilder addConstraintViolation(String minStr, String maxStr) {
        if (minStr != null && maxStr != null) {
            return addConstraintViolation("errors.field.range")
                    .withParameters(minStr, maxStr);
        } else if (minStr != null) {
            return addConstraintViolation("errors.field.less")
                    .withParameters(minStr);
        } else {
            return addConstraintViolation("errors.field.notgreater")
                    .withParameters(maxStr);
        }
    }

}