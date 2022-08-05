package com.foros.reporting.meta;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class ResultSetNameTransformer {

    private static final ResultSetNameResolver DEFAULT_RESULT_SET_NAME_RESOLVER = new DefaultResultSetNameResolver();

    public static Collection<String> getResultSetNames(Collection<DbColumn> columns, ResultSetNameResolver resultSetNameResolver) {
        return resultSetNames(columns, resultSetNameResolver);
    }

    public static Collection<String> getResultSetNames(Collection<DbColumn> columns) {
        return getResultSetNames(columns, DEFAULT_RESULT_SET_NAME_RESOLVER);
    }

    private static Collection<String> resultSetNames(Collection<DbColumn> columns, ResultSetNameResolver resultSetNameResolver) {
        Set<String> names = new LinkedHashSet<>(columns.size());

        for (DbColumn col : columns) {
            names.add(resultSetNameResolver.getResultSetName(col));
        }

        return names;
    }

}
