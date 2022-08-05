package com.foros.reporting.serializer.formatter;

public class CurrencyValueFormatter extends CurrencyValueFormatterSupport {

    private String currencyCode;

    public CurrencyValueFormatter(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public CurrencyValueFormatter(String currencyCode, int precision) {
        super(precision);
        this.currencyCode = currencyCode;
    }

    @Override
    protected String getCurrencyCode(FormatterContext context) {
        return currencyCode;
    }
}
