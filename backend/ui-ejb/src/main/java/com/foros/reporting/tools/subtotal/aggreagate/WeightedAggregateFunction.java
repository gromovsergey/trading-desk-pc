package com.foros.reporting.tools.subtotal.aggreagate;

import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.util.mapper.Pair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WeightedAggregateFunction extends CompositeAggregateFunction {
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private List<Pair<AggregateFunction, BigDecimal>> weightedAggregateFunction = new ArrayList<>();
    private AggregateFunction divisor;

    private WeightedAggregateFunction(Column target) {
        super(target);
    }

    @Override
    public Object getValue() {
        BigDecimal divisorValue = (BigDecimal) divisor.getValue();
        BigDecimal total = BigDecimal.ZERO;

        if (divisorValue == null || divisorValue.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        for (Pair<AggregateFunction, BigDecimal> v : weightedAggregateFunction) {
            if (v.getLeftValue().getValue() != null) {
                total = total.add(v.getRightValue().multiply((BigDecimal) v.getLeftValue().getValue()), MATH_CONTEXT);
            }
        }
        return total.divide(divisorValue, MATH_CONTEXT).multiply(HUNDRED);
    }

    public static AggregateFunctionFactory<OlapColumn> factory(
            final OlapColumn divisor, final Object... args) {
        return new AggregateFunctionFactory<OlapColumn>() {
            private List<OlapColumn> columns = new ArrayList<>();

            @Override
            public AggregateFunction newInstance(Column column) {
                WeightedAggregateFunction f = new WeightedAggregateFunction(column);
                f.divisor = divisor.newAggregateFunction();
                f.add(f.divisor);
                for (int i = 0; i < args.length; i++) {
                    columns.add((OlapColumn) args[i]);
                    AggregateFunction aggr = ((OlapColumn) args[i]).newAggregateFunction();
                    f.add(aggr);
                    f.weightedAggregateFunction.add(new Pair(aggr, BigDecimal.valueOf((Double)args[++i])));
                }
                return f;
            }

            @Override
            public Collection<OlapColumn> getDependedColumns() {
                return columns;
            }
        };
    }
}
