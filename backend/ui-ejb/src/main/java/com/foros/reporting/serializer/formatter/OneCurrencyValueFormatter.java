package com.foros.reporting.serializer.formatter;

import com.foros.reporting.meta.Column;

public class OneCurrencyValueFormatter extends MultiCurrencyValueFormatter {

    public OneCurrencyValueFormatter(Column currencyColumn) {
        super(currencyColumn);
    }

    @Override
    public String formatText(Number value, FormatterContext context) {
        if (value == null) {
            return "";
        }
        return super.formatText(value, context);
    }
}
