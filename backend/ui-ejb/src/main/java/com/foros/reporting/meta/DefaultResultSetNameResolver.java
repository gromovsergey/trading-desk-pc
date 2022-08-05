package com.foros.reporting.meta;

public class DefaultResultSetNameResolver implements ResultSetNameResolver {
    @Override
    public String getResultSetName(DbColumn column) {
        return column.getResultSetName();
    }
}
