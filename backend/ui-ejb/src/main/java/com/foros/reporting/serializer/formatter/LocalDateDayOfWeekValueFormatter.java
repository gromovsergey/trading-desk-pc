package com.foros.reporting.serializer.formatter;

import com.foros.reporting.serializer.xlsx.Styles;
import com.foros.util.StringUtil;

import java.util.Locale;

import org.joda.time.LocalDate;

public class LocalDateDayOfWeekValueFormatter extends ValueFormatterSupport<LocalDate> {
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

    @Override
    public String formatText(LocalDate value, FormatterContext context) {
        if (value == null) {
            return null;
        }

        Locale locale = context.getLocale();
        return StringUtil.getLocalizedString("report.output.field.dayOfWeek." + value.getDayOfWeek(), locale);
    }
}
