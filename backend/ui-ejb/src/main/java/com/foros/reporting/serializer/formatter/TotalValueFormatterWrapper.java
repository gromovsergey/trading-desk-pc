package com.foros.reporting.serializer.formatter;

import com.foros.util.StringUtil;

public class TotalValueFormatterWrapper extends ValueFormatterSupport<Object> {

    private ValueFormatter formatter;

    public TotalValueFormatterWrapper(ValueFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public String formatText(Object value, FormatterContext context) {
        if (value != null) {
            String v = formatter.formatText(value, context);
            return StringUtil.getLocalizedString("report.subtotal", context.getLocale(), v);
        } else {
            return StringUtil.getLocalizedString("report.subtotal.empty", context.getLocale());
        }
    }
}
