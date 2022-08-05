package com.foros.reporting.serializer.formatter;

public class YesNoFormatter extends ValueFormatterSupport<Boolean>  {
    @Override
    public String formatText(Boolean value, FormatterContext context) {
        return value ? "Y" : "N" ;
    }
}