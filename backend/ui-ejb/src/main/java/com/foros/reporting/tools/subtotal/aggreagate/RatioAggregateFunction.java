package com.foros.reporting.tools.subtotal.aggreagate;

import com.foros.reporting.meta.Column;

import java.math.BigDecimal;

public class RatioAggregateFunction extends CompositeAggregateFunction {

    private AggregateFunction dividend;
    private AggregateFunction divisor;

    public RatioAggregateFunction(Column target, AggregateFunction dividend, AggregateFunction divisor) {
        super(target);
        this.dividend = add(dividend);
        this.divisor = add(divisor);
    }

    @Override
    public BigDecimal getValue() {
        BigDecimal dividendValue = (BigDecimal) dividend.getValue();
        BigDecimal divisorValue = (BigDecimal) divisor.getValue();

        if (dividendValue == null || divisorValue == null) {
            return null;
        }

        if (divisorValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return dividendValue.divide(divisorValue, MATH_CONTEXT);
    }
}
