package com.foros.util.formatter;

/**
 * @author Andrei Rynkevich
 */
public class CurrencyFormatter implements FieldFormatter {
    private final String currencyCode;

    public CurrencyFormatter(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getString(Object o) {
        if (o == null) {
            return "";
        }

        if (!(o instanceof Number)) {
            return o.toString();
        }

        return com.foros.web.taglib.NumberFormatter.formatCurrency(o, currencyCode);
    }

    public String getCurrencyCode() {
        return currencyCode;
    }
}
