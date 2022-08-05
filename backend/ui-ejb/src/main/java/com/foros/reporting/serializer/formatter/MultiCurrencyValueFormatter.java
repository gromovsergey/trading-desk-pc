package com.foros.reporting.serializer.formatter;

import com.foros.reporting.meta.Column;

public class MultiCurrencyValueFormatter extends CurrencyValueFormatterSupport {

    private Column currencyColumn;

    public MultiCurrencyValueFormatter(Column currencyColumn) {
        this.currencyColumn = currencyColumn;
    }

    @Override
    protected String getCurrencyCode(FormatterContext context) {
        return (String) context.getRow().get(currencyColumn);
    }
}
