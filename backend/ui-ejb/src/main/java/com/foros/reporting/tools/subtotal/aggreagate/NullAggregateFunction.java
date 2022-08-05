package com.foros.reporting.tools.subtotal.aggreagate;

import com.foros.reporting.Row;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.DependentColumn;

import java.util.Collection;
import java.util.Collections;

public class NullAggregateFunction<C extends DependentColumn<C>> implements AggregateFunction, AggregateFunctionFactory<C> {

    private static final NullAggregateFunction INSTANCE = new NullAggregateFunction<>();

    public static <C extends DependentColumn<C>> NullAggregateFunction<C> instance() {
        return (NullAggregateFunction<C>) INSTANCE;
    }

    @Override
    public void aggregate(Row row) {
    }

    @Override
    public void clean() {
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public AggregateFunction newInstance(Column column) {
        return this;
    }

    @Override
    public Collection<C> getDependedColumns() {
        return Collections.emptyList();
    }
}
