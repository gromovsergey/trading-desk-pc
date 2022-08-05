package com.foros.reporting.tools.subtotal.aggreagate;

import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.DependentColumn;
import com.foros.reporting.tools.subtotal.aggreagate.AggregateFunction;
import com.foros.reporting.tools.subtotal.aggreagate.AggregateFunctionFactory;
import com.foros.reporting.tools.subtotal.aggreagate.CompositeAggregateFunction;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

public class MarginAggregateFunction extends CompositeAggregateFunction {
    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private AggregateFunction revenueAggFunction;
    private AggregateFunction pubPayoutAggFunction;

    public MarginAggregateFunction(Column target, AggregateFunction revenue, AggregateFunction pubPayoff) {
        super(target);
        revenueAggFunction = add(revenue);
        pubPayoutAggFunction = add(pubPayoff);
    }

    @Override
    public Object getValue() {
        BigDecimal revenue = (BigDecimal) revenueAggFunction.getValue();
        BigDecimal pubPayout = (BigDecimal) pubPayoutAggFunction.getValue();

        revenue = revenue == null ? BigDecimal.ZERO : revenue;
        pubPayout = pubPayout == null ? BigDecimal.ZERO : pubPayout;

        if (revenue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        // (revenue-pubPayoff)*100/revenue
        return revenue.subtract(pubPayout).divide(revenue, MATH_CONTEXT).multiply(HUNDRED);
    }

    public static <C extends DependentColumn<C>> AggregateFunctionFactory<C> factory(
            final C revenue,
            final C pubPayout) {
        return new AggregateFunctionFactory<C>() {
            @Override
            public AggregateFunction newInstance(Column column) {
                return new MarginAggregateFunction(column, revenue.newAggregateFunction(), pubPayout.newAggregateFunction());
            }

            @Override
            public Collection<C> getDependedColumns() {
                return Arrays.asList(revenue, pubPayout);
            }
        };
    }
}
