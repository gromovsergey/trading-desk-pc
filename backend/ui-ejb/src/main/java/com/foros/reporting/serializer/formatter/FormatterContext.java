package com.foros.reporting.serializer.formatter;

import com.foros.reporting.Row;

import java.util.Locale;

public class FormatterContext {

    private Row row;
    private Locale locale;

    public FormatterContext(Locale locale) {
        this(locale, null);
    }

    public FormatterContext(Locale locale, Row row) {
        this.locale = locale;
        this.row = row;
    }

    public void setRow(Row row) {
        this.row = row;
    }

    public Row getRow() {
        return row;
    }

    public Locale getLocale() {
        return locale;
    }
}
