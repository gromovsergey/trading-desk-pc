package com.foros.reporting.tools.subtotal;

import com.foros.reporting.Row;
import com.foros.reporting.meta.Column;
import com.foros.reporting.tools.subtotal.aggreagate.AggregateFunction;

public class LastValueAggregateFunction implements AggregateFunction {

    private Column column;
    private Object lastValue;

    public LastValueAggregateFunction(Column column) {
        this.column = column;
    }

    @Override
    public void aggregate(Row row) {
        lastValue = row.get(column);
    }

    @Override
    public void clean() {
    }

    @Override
    public Object getValue() {
        return lastValue;
    }
}
