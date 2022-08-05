package com.foros.reporting.serializer.formatter.registry;

import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.ColumnType;
import com.foros.reporting.serializer.formatter.ValueFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ValueFormatterRegistryImpl implements ValueFormatterRegistry {

    private ValueFormatter defaultFormatter = null;

    private Map<Column, ValueFormatter> formatterByColumnName =
            new HashMap<Column, ValueFormatter>();

    private Map<ColumnType, ValueFormatter> formatterByColumnType =
            new HashMap<ColumnType, ValueFormatter>();

    protected ValueFormatterRegistryImpl() {
    }

    public ValueFormatterRegistryImpl column(Column column, ValueFormatter valueFormatter) {
        formatterByColumnName.put(column, valueFormatter);
        return this;
    }

    public ValueFormatterRegistryImpl columns(ValueFormatter valueFormatter, Collection<? extends Column> columns) {
        for (Column column : columns) {
            formatterByColumnName.put(column, valueFormatter);
        }
        return this;
    }

    public ValueFormatterRegistryImpl type(ColumnType columnType, ValueFormatter valueFormatter) {
        formatterByColumnType.put(columnType, valueFormatter);
        return this;
    }


    @Override
    public <T> ValueFormatter<T> get(Column column) {
        ValueFormatter<T> formatter = formatterByColumnName.get(column);

        if (formatter == null) {
            formatter = formatterByColumnType.get(column.getType());
        }

        if (formatter != null) {
            return formatter;
        } else {
            if (defaultFormatter != null) {
                return defaultFormatter;
            } else {
                return null;
            }
        }
    }

    public ValueFormatterRegistryImpl defaultFormatter(ValueFormatter<?> formatter) {
        defaultFormatter = formatter;
        return this;
    }

}
