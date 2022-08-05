package com.foros.reporting.serializer.formatter;

public class NAValueFormatter extends LocalizableValueFormatter {
    private static final String key = "notAvailable";

    public NAValueFormatter(ValueFormatter availableFormatter) {
        super(key, availableFormatter, null);
    }

    public NAValueFormatter(ValueFormatter availableFormatter, String excelStyle) {
        super(key, availableFormatter, excelStyle);
    }
}
