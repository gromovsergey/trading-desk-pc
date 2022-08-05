package com.foros.reporting.tools.subtotal.aggreagate;

import com.foros.reporting.Row;
import com.foros.reporting.meta.Column;

import java.util.Collection;
import java.util.Collections;

public class MinAggregateFunction implements AggregateFunction {

    private Column target;
    private Comparable min;

    public MinAggregateFunction(Column target) {
        this.target = target;
    }

    @Override
    public void aggregate(Row row) {
        Comparable val = (Comparable) row.get(target);
        if (val == null) {
            return;
        }

        if (min == null) {
            min = val;
            return;
        }

        min = min.compareTo(val) < 0 ? min : val;
    }

    @Override
    public void clean() {
        min = null;
    }

    @Override
    public Object getValue() {
        return min;
    }

    public static AggregateFunctionFactory factory() {
        return new AggregateFunctionFactory() {
            @Override
            public AggregateFunction newInstance(Column column) {
                return new MinAggregateFunction(column);
            }

            @Override
            public Collection<Column> getDependedColumns() {
                return Collections.emptyList();
            }
        };
    }
}
