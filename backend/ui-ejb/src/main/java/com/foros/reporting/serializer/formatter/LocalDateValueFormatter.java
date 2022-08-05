package com.foros.reporting.serializer.formatter;

import com.foros.reporting.serializer.xlsx.Styles;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Locale;

public class LocalDateValueFormatter extends ValueFormatterSupport<LocalDate> {

    @Override
    public String formatText(LocalDate value, FormatterContext context) {
        return formatHtml(value, context.getLocale());
    }

    @Override
    public void formatExcel(ExcelCellAccessor cellAccessor, LocalDate value, FormatterContext context) {
        cellAccessor.setDate(value.toLocalDateTime(LocalTime.MIDNIGHT));
        cellAccessor.addStyle(Styles.date());
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, LocalDate value, FormatterContext context) {
        cellAccessor.addStyle("date");
        super.formatHtml(cellAccessor, value, context);
    }

    public static String formatHtml(LocalDate value, Locale locale) {
        if (value == null) {
            return null;
        }

        String pattern = DateTimeFormat.patternForStyle("S-", locale);
        return value.toString(pattern);
    }
}
