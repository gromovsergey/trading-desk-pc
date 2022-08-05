package com.foros.reporting.tools.subtotal.aggreagate;

import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.DependentColumn;

import java.util.Collection;

public interface AggregateFunctionFactory<C extends DependentColumn<C>> {

    AggregateFunction newInstance(Column column);

    Collection<C> getDependedColumns();

}
