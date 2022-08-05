package com.foros.reporting.serializer.formatter.registry;

import com.foros.reporting.meta.Column;
import com.foros.reporting.serializer.formatter.NullValueFormatter;
import com.foros.reporting.serializer.formatter.ValueFormatter;

import java.util.Set;

public class FilteringValueFormatterRegistry implements ValueFormatterRegistry {
    private ValueFormatterRegistry target;
    private Set<Column> includeColumns;

    public FilteringValueFormatterRegistry(ValueFormatterRegistry target, Set<Column> includeColumns) {
        this.target = target;
        this.includeColumns = includeColumns;
    }

    @Override
    public <T> ValueFormatter<T> get(Column column) {
        if (includeColumns.contains(column)) {
            return target.get(column);
        }

        return NullValueFormatter.getInstance();
    }
}
