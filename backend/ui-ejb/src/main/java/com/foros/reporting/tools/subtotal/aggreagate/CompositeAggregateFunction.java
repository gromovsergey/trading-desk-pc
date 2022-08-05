package com.foros.reporting.tools.subtotal.aggreagate;

import com.foros.reporting.Row;
import com.foros.reporting.meta.Column;

import java.util.ArrayList;
import java.util.List;


public abstract class CompositeAggregateFunction implements AggregateFunction {
    private List<AggregateFunction> aggregateFunctions = new ArrayList<AggregateFunction>();
    protected final Column target;

    protected CompositeAggregateFunction(Column target) {
        this.target = target;
    }

    protected <T extends AggregateFunction> T add(T aggregateFunction) {
        aggregateFunctions.add(aggregateFunction);
        return aggregateFunction;
    }

    @Override
    public void aggregate(Row row) {
        for (AggregateFunction aggregateFunction : aggregateFunctions) {
            aggregateFunction.aggregate(row);
        }
    }

    @Override
    public void clean() {
        for (AggregateFunction aggregateFunction : aggregateFunctions) {
            aggregateFunction.clean();
        }
    }
}
