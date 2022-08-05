package com.foros.reporting.meta;

import com.foros.reporting.tools.subtotal.aggreagate.AggregateFunction;
import com.foros.reporting.tools.subtotal.aggreagate.AggregateFunctionFactory;
import com.foros.reporting.tools.subtotal.aggreagate.NullAggregateFunction;
import com.foros.session.reporting.parameters.Order;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractDependentColumn<C extends DependentColumn<C>> implements DependentColumn<C> {

    protected String nameKey;
    protected ColumnType columnType;
    protected AggregateFunctionFactory<C> aggregateFunctionFactory;
    protected DependenciesColumnResolver<C> dependenciesColumnResolver;

    public AbstractDependentColumn(
            String nameKey,
            ColumnType columnType,
            AggregateFunctionFactory<C> aggregateFunctionFactory,
            DependenciesColumnResolver<C> dependenciesColumnResolver) {
        this.nameKey = nameKey;
        this.columnType = columnType;
        this.aggregateFunctionFactory = aggregateFunctionFactory;
        this.dependenciesColumnResolver = dependenciesColumnResolver;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        AbstractDependentColumn other = (AbstractDependentColumn) obj;

        if (nameKey == null) {
            if (other.nameKey != null)
                return false;
        } else if (!nameKey.equals(other.nameKey)) {
            return false;
        }

        return true;
    }

    protected ColumnType getColumnType() {
        return columnType;
    }

    public Order getDefaultOrder() {
        return columnType.getDefaultOrder();
    }

    @Override
    public Set<C> getDependentColumns(Object context) {
        Set<C> res = new HashSet<>();

        if (aggregateFunctionFactory != null) {
            res.addAll(aggregateFunctionFactory.getDependedColumns());
        }
        res.addAll(dependenciesColumnResolver.resolve(context));
        return res;
    }

    @Override
    public String getNameKey() {
        return nameKey;
    }

    @Override
    public ColumnType getType() {
        return columnType;
    }

    @Override
    public boolean hasAggregateFunction() {
        return aggregateFunctionFactory != NullAggregateFunction.instance();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nameKey == null) ? 0 : nameKey.hashCode());
        return result;
    }

    @Override
    public AggregateFunction newAggregateFunction() {
        return aggregateFunctionFactory.newInstance(this);
    }

}
