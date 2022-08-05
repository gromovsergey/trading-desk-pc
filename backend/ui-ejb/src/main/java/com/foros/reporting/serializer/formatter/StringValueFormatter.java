package com.foros.reporting.serializer.formatter;

public class StringValueFormatter extends ValueFormatterSupport<String> {

    @Override
    public String formatText(String value, FormatterContext context) {
        return value;
    }

}
