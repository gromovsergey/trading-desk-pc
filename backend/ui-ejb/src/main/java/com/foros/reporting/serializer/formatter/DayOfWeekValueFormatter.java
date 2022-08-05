package com.foros.reporting.serializer.formatter;

import com.foros.reporting.serializer.xlsx.Styles;
import com.foros.util.StringUtil;

import java.util.Locale;

public class DayOfWeekValueFormatter extends ValueFormatterSupport<Long> {

    @Override
    public String formatText(Long value, FormatterContext context) {
        if (value == null) {
            return null;
        }

        Locale locale = context.getLocale();
        return StringUtil.getLocalizedString("report.output.field.dayOfWeek." + value, locale);
    }

    @Override
    public void formatExcel(ExcelCellAccessor cellAccessor, Long value, FormatterContext context) {
        cellAccessor.setString(formatText(value, context));
        cellAccessor.addStyle(Styles.textAlignRight());
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, Long value, FormatterContext context) {
        cellAccessor.addStyle("date");
        super.formatHtml(cellAccessor, value, context);
    }
}