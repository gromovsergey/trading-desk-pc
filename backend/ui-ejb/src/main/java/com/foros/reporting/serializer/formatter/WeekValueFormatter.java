package com.foros.reporting.serializer.formatter;

import com.foros.reporting.serializer.xlsx.Styles;
import org.joda.time.LocalDate;

import java.util.Locale;

public class WeekValueFormatter extends ValueFormatterSupport<LocalDate> {

    @Override
    public String formatText(LocalDate value, FormatterContext context) {
        if (value == null) {
            return null;
        }

        Locale locale = context.getLocale();
        LocalDate endDate = value.plusDays(6);

        if (value.getYear() == endDate.getYear()) {
            return value.toString("d MMM", locale) + " - " + endDate.toString("d MMM yyyy", locale);
        } else {
            return value.toString("d MMM yyyy", locale) + " - " + endDate.toString("d MMM yyyy", locale);
        }
    }

    @Override
    public void formatExcel(ExcelCellAccessor cellAccessor, LocalDate value, FormatterContext context) {
        cellAccessor.setString(formatText(value, context));
        cellAccessor.addStyle(Styles.textAlignRight());
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, LocalDate value, FormatterContext context) {
        cellAccessor.addStyle("date");
        super.formatHtml(cellAccessor, value, context);
    }
}