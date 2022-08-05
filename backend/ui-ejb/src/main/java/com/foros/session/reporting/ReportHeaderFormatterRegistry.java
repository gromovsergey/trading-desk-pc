package com.foros.session.reporting;

import com.foros.reporting.meta.Column;
import com.foros.reporting.serializer.formatter.HeaderFormatter;
import com.foros.reporting.serializer.formatter.ValueFormatter;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;

import java.util.HashMap;
import java.util.Map;

public class ReportHeaderFormatterRegistry implements ValueFormatterRegistry {
    protected Map<Column, ValueFormatter<Column>> formatters = new HashMap<Column, ValueFormatter<Column>>();

    @Override
    public ValueFormatter<Column> get(Column column) {
        ValueFormatter<Column> formatter = formatters.get(column);
        return formatter != null ? formatter : HeaderFormatter.INSTANCE;
    }
}
