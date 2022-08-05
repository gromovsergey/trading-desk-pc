package com.foros.reporting.serializer.formatter;

import com.foros.reporting.serializer.xlsx.Styles;
import com.foros.util.StringUtil;
import org.joda.time.LocalDate;

import java.util.Locale;

public class QuarterValueFormatter extends ValueFormatterSupport<LocalDate> {

    @Override
    public String formatText(LocalDate value, FormatterContext context) {
        if (value == null) {
            return null;
        }

        Locale locale = context.getLocale();

        int quarter = (value.getMonthOfYear() / 3) + 1;
        String text = StringUtil.getLocalizedString("report.output.field.quarter" + quarter + ".short", locale);
        String pattern = "'" + text + "' yyyy";

        return value.toString(pattern, locale);
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
