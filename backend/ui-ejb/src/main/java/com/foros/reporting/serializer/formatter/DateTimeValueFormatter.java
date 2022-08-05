package com.foros.reporting.serializer.formatter;

import com.foros.reporting.serializer.xlsx.Styles;
import com.foros.util.DateHelper;

import java.util.Date;
import java.util.TimeZone;

public class DateTimeValueFormatter extends ValueFormatterSupport<Date> {
    private String notSetPhrase;
    private TimeZone timeZone;

    public DateTimeValueFormatter() {
        this(TimeZone.getDefault(), null);
    }

    public DateTimeValueFormatter(TimeZone timeZone) {
        this(timeZone, null);
    }

    public DateTimeValueFormatter(TimeZone timeZone, String notSetPhrase) {
        this.notSetPhrase = notSetPhrase;
        this.timeZone = timeZone;
    }

    @Override
    public String formatText(Date value, FormatterContext context) {
        if (value == null) {
            return notSetPhrase;
        }
        return DateHelper.formatDateTime(value, timeZone, context.getLocale());
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, Date value, FormatterContext context) {
        cellAccessor.addStyle("date");
        super.formatHtml(cellAccessor, value, context);
    }

    @Override
    public void formatExcel(ExcelCellAccessor cellAccessor, Date value, FormatterContext context) {
        if (value == null) {
            cellAccessor.setString(notSetPhrase);
            cellAccessor.addStyle(Styles.text());
        } else {
            cellAccessor.setDate(value);
            cellAccessor.addStyle(Styles.dateTime());
        }
    }

}
