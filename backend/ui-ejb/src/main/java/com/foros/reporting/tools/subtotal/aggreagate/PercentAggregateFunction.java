package com.foros.reporting.tools.subtotal.aggreagate;

import com.foros.reporting.meta.Column;

import java.math.BigDecimal;

public class PercentAggregateFunction extends CompositeAggregateFunction {
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private RatioAggregateFunction function;

    public PercentAggregateFunction(Column target, AggregateFunction dividend, AggregateFunction divisor) {
        super(target);
        this.function = add(new RatioAggregateFunction(target, dividend, divisor));
    }

    @Override
    public Object getValue() {
        BigDecimal value = function.getValue();
        if (value == null) {
            return null;
        }
        return value.multiply(HUNDRED);
    }
}
