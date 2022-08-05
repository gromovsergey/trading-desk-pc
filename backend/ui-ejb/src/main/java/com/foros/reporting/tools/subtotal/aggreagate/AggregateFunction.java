package com.foros.reporting.tools.subtotal.aggreagate;

import com.foros.reporting.Row;

import java.math.MathContext;
import java.math.RoundingMode;

public interface AggregateFunction {
    MathContext MATH_CONTEXT = new MathContext(32, RoundingMode.HALF_UP);

    void aggregate(Row row);

    void clean();

    Object getValue();
}
