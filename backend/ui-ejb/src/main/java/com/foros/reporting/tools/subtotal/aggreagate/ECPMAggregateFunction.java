package com.foros.reporting.tools.subtotal.aggreagate;

import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.DependentColumn;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

public class ECPMAggregateFunction extends CompositeAggregateFunction {
    public static final BigDecimal THOUSAND = new BigDecimal("1000");
    private AggregateFunction costAggFunction;
    private AggregateFunction impsAggFunction;

    private ECPMAggregateFunction(Column target, AggregateFunction cost, AggregateFunction impressions) {
        super(target);
        costAggFunction = add(cost);
        impsAggFunction = add(impressions);
    }

    @Override
    public Object getValue() {
        BigDecimal cost = (BigDecimal) costAggFunction.getValue();
        BigDecimal imps = (BigDecimal) impsAggFunction.getValue();

        if (cost == null) {
            return null;
        }
        imps = imps == null ? BigDecimal.ZERO : imps;

        if (imps.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return cost.multiply(THOUSAND).divide(imps, MATH_CONTEXT);
    }

    public static <C extends DependentColumn<C>> AggregateFunctionFactory<C> factory(
            final C revenue,
            final C pubPayout) {
        return new AggregateFunctionFactory<C>() {
            @Override
            public AggregateFunction newInstance(Column column) {
                return new ECPMAggregateFunction(column, revenue.newAggregateFunction(), pubPayout.newAggregateFunction());
            }

            @Override
            public Collection<C> getDependedColumns() {
                return Arrays.asList(revenue, pubPayout);
            }
        };
    }
}
