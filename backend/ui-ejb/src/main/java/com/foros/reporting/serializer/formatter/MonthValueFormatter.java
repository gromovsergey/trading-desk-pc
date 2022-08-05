package com.foros.reporting.serializer.formatter;

import com.foros.reporting.serializer.xlsx.Styles;

import java.util.Locale;

import org.joda.time.LocalDate;

public class MonthValueFormatter extends ValueFormatterSupport<LocalDate> {

    @Override
    public String formatText(LocalDate value, FormatterContext context) {
        if (value == null) {
            return null;
        }

        Locale locale = context.getLocale();
        return value.toString("MMM yyyy", locale);
    }

    @Override
    public void formatExcel(ExcelCellAccessor cellAccessor, LocalDate value, FormatterContext context) {
        cellAccessor.setDate(value);
        cellAccessor.addStyle(Styles.dateMonth());
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, LocalDate value, FormatterContext context) {
        cellAccessor.addStyle("date");
        super.formatHtml(cellAccessor, value, context);
    }
}
