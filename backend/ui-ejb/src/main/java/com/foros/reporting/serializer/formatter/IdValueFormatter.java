package com.foros.reporting.serializer.formatter;

public class IdValueFormatter extends ValueFormatterSupport<Long> {
    @Override
    public String formatText(Long value, FormatterContext context) {
        return value == null ? null : value.toString();
    }
}
