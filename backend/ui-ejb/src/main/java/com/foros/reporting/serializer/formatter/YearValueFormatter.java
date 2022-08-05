package com.foros.reporting.serializer.formatter;

import com.foros.reporting.serializer.xlsx.Styles;
import org.joda.time.LocalDate;

import java.util.Locale;

public class YearValueFormatter extends ValueFormatterSupport<LocalDate> {

    @Override
    public String formatText(LocalDate value, FormatterContext context) {
        if (value == null) {
            return null;
        }

        Locale locale = context.getLocale();
        return value.toString("yyyy", locale);
    }

    @Override
    public void formatExcel(ExcelCellAccessor cellAccessor, LocalDate value, FormatterContext context) {
        cellAccessor.setDate(value);
        cellAccessor.addStyle(Styles.dateYear());
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, LocalDate value, FormatterContext context) {
        cellAccessor.addStyle("date");
        super.formatHtml(cellAccessor, value, context);
    }
}
