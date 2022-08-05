package com.foros.reporting.serializer.formatter;


import com.foros.model.Status;
import com.foros.reporting.meta.DbColumn;

public class StatusableEntityUrlValueFormatter extends EntityUrlValueFormatter {
    private final DbColumn statusColumn;

    protected StatusableEntityUrlValueFormatter(DbColumn idColumn, String urlPattern, DbColumn statusColumn) {
        super(idColumn, urlPattern, null);
        this.statusColumn = statusColumn;
    }

    @Override
    public void formatExcel(ExcelCellAccessor cellAccessor, String value, FormatterContext context) {
        if (isStatusAcceptable(context)) {
            super.formatExcel(cellAccessor, value, context);
        } else {
            formatExcelDefault(cellAccessor, value, context);
        }
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, String value, FormatterContext context) {
        if (isStatusAcceptable(context)) {
            super.formatHtml(cellAccessor, value, context);
        } else {
            formatHtmlDefault(cellAccessor, value, context);
        }
    }

    public static StatusableEntityUrlValueFormatter html(DbColumn idColumn, String urlPattern, DbColumn statusColumn) {
        return new StatusableEntityUrlValueFormatter(idColumn, urlPattern, statusColumn);
    }

    protected boolean isStatusAcceptable(FormatterContext context) {
        return statusColumn == null || (Status)context.getRow().get(statusColumn) != Status.DELETED;
    }
}
