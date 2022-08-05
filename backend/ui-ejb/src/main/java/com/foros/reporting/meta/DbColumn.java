package com.foros.reporting.meta;

import com.foros.reporting.tools.subtotal.aggreagate.AggregateFunctionFactory;

public class DbColumn extends AbstractDependentColumn<DbColumn> {

    private String columnName;

    public DbColumn(
            String id,
            String columnName,
            ColumnType type,
            AggregateFunctionFactory<DbColumn> aggregateFunctionFactory,
            DependenciesColumnResolver<DbColumn> dependenciesColumnResolver) {
        super(id, type, aggregateFunctionFactory, dependenciesColumnResolver);
        this.columnName = columnName;
    }

    public String getResultSetName() {
        return columnName;
    }

    @Override
    public String toString() {
        return "DbColumn[" + getNameKey() + "]";
    }
}
