package com.foros.reporting.meta;

import com.foros.reporting.tools.subtotal.aggreagate.AggregateFunction;

public interface AggregatableColumn extends Column {
    AggregateFunction newAggregateFunction();
    boolean hasAggregateFunction();
}
