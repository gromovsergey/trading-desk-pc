package com.foros.reporting.serializer.formatter;

import com.foros.util.StringUtil;

import java.util.Locale;

public class LocalizableValueFormatter extends ValueFormatterSupport<Object> {
    private String valueKey;
    private ValueFormatter<Object> availableFormatter;
    private String excelStyle;

    public LocalizableValueFormatter(String valueKey, ValueFormatter availableFormatter) {
        this(valueKey, availableFormatter, null);
    }

    public LocalizableValueFormatter(String valueKey, ValueFormatter availableFormatter, String excelStyle) {
        this.valueKey = valueKey;
        this.availableFormatter = availableFormatter;
        this.excelStyle = excelStyle;
    }

    @Override
    public String formatText(Object value, FormatterContext context) {
        if (isNotAvailable(value, context)) {
            return getNAValue(context.getLocale());
        }
        return availableFormatter.formatText(value, context);
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, Object value, FormatterContext context) {
        // to apply styles
        availableFormatter.formatHtml(cellAccessor, value, context);

        if (isNotAvailable(value, context)) {
            cellAccessor.setHtml(getNAValue(context.getLocale()));
        }
    }

    @Override
    public void formatExcel(ExcelCellAccessor cellAccessor, Object value, FormatterContext context) {
        if (isNotAvailable(value, context)) {
            if (excelStyle != null) {
                cellAccessor.addStyle(excelStyle);
            }
            super.formatExcel(cellAccessor, null, context);
            return;
        }

        availableFormatter.formatExcel(cellAccessor, value, context);
    }

    protected String getNAValue(Locale locale) {
        return StringUtil.getLocalizedString(valueKey, locale);
    }

    protected boolean isNotAvailable(Object value, FormatterContext context) {
        return value == null;
    }
}
