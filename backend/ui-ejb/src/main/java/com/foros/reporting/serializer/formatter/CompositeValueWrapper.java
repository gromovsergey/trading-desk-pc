package com.foros.reporting.serializer.formatter;

import com.foros.reporting.meta.Column;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CompositeValueWrapper extends ValueFormatterSupport {

    Map<Column, ValueFormatter> formatters = new LinkedHashMap<>();
    private String format;

    public CompositeValueWrapper(String format) {
        this.format = format;
    }

    public CompositeValueWrapper putFormatter(Column column, ValueFormatter formatter) {
        formatters.put(column, formatter);
        return this;
    }

    @Override
    public String formatText(Object value, FormatterContext context) {
        return String.format(format, getValues(context).toArray());
    }

    private List<String> getValues(FormatterContext context) {
        List<String> values = new ArrayList<>(formatters.size());
        for (Map.Entry<Column, ValueFormatter> entry : formatters.entrySet()) {
            Object value = context.getRow().get(entry.getKey());
            values.add(entry.getValue().formatText(value, context));
        }
        return values;
    }
}
