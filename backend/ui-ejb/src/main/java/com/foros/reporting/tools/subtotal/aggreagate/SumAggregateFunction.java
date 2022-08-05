package com.foros.reporting.tools.subtotal.aggreagate;

import com.foros.reporting.Row;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.DependentColumn;
import com.foros.util.NumberUtil;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

public class SumAggregateFunction implements AggregateFunction {

    private Column target;
    private BigDecimal sum = null;

    public SumAggregateFunction(Column target) {
        this.target = target;
    }

    @Override
    public void aggregate(Row row) {
        Object o = row.get(target);
        if (o != null) {
            BigDecimal decimal = NumberUtil.toBigDecimal((Number) o);
            if (sum == null && decimal != null) {
                sum = BigDecimal.ZERO;
            }
            sum = sum.add(decimal);
        }
    }

    @Override
    public void clean() {
        sum = null;
    }

    @Override
    public Object getValue() {
        return sum;
    }

    public static <C extends DependentColumn<C>> AggregateFunctionFactory<C> factory() {
        return new AggregateFunctionFactory<C>() {
            @Override
            public AggregateFunction newInstance(Column column) {
                return new SumAggregateFunction(column);
            }

            @Override
            public Collection<C> getDependedColumns() {
                return Collections.emptyList();
            }
        };
    }
}
