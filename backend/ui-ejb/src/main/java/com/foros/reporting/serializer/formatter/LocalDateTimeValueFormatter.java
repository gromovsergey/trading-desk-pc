package com.foros.reporting.serializer.formatter;

import com.foros.reporting.serializer.xlsx.Styles;

import java.util.Locale;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

public class LocalDateTimeValueFormatter extends ValueFormatterSupport<LocalDateTime> {
    @Override
    public String formatText(LocalDateTime value, FormatterContext context) {
        Locale locale = context.getLocale();
        return formatHtml(value, locale);
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, LocalDateTime value, FormatterContext context) {
        cellAccessor.addStyle("date");
        super.formatHtml(cellAccessor, value, context);
    }

    @Override
    public void formatExcel(ExcelCellAccessor cellAccessor, LocalDateTime value, FormatterContext context) {
        cellAccessor.setDate(value);
        cellAccessor.addStyle(Styles.dateTime());
    }

    public static String formatHtml(LocalDateTime value, Locale locale) {
        if (value == null) {
            return "";
        }

        String pattern = DateTimeFormat.patternForStyle("SS", locale);
        return value.toString(pattern);
    }
}
