package com.foros.reporting.serializer;

import com.foros.reporting.Row;
import com.foros.reporting.RowType;
import com.foros.reporting.RowTypes;
import com.foros.reporting.TooManyRowsException;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.ValueFormatter;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryHolder;
import com.foros.session.reporting.PreparedParameterBuilder;

import java.util.List;
import java.util.Locale;

public class ResultHolder extends ResultSerializerSupport<ResultHolder> {

    private SimpleReportData reportData;
    private int maxRows;

    private int counter = 0;

    public ResultHolder(int maxRows, SimpleReportData reportData, ValueFormatterRegistryHolder customRegistry, Locale locale) {
        super(customRegistry, new FormatterContext(locale));
        this.maxRows = maxRows;
        this.reportData = reportData;
    }

    @Override
    public void before(MetaData metaData) {
        super.before(metaData);
        reportData.setMetaData(metaData);

        HtmlCell[] cells = formatHeaders(metaData.getColumns());
        reportData.setHeaders(cells);
    }

    @Override
    public void after() {
        super.after();
        registry = registryHolder.registry(RowTypes.header());
        if (reportData.getSummary() != null) {
            HtmlCell[] cells = formatHeaders(reportData.getSummary().getMetaData().getColumns());
            reportData.getSummary().setHeaders(cells);
        }
    }

    private HtmlCell[] formatHeaders(List<Column> columns) {
        HtmlCell[] cells = new HtmlCell[columns.size()];
        for (int i = 0; i < cells.length; i++) {
            Column column = columns.get(i);
            ValueFormatter<Object> formatter = registry.get(column);
            HtmlCell cell = new HtmlCell();
            formatter.formatHtml(cell, column, context);
            cells[i] = cell;
        }
        return cells;
    }

    @Override
    public void row(Row row) {
        super.row(row);
        HtmlCell[] cells = format(metaData.getColumns(), row);
        RowType rowType = row.getType();
        reportData.addRow(rowType, cells);
        counter++;
        if (counter >= maxRows) {
            reportData.setPartialResult(true);
            throw new TooManyRowsException(maxRows);
        }
    }

    public SimpleReportData getReportData() {
        return reportData;
    }

    @Override
    public ResultHolder preparedParameters(PreparedParameterBuilder.Factory factory) {
        this.reportData.setPreparedParameters(factory.builder(context.getLocale()).parameters());
        return this;
    }

    @Override
    public int getMaxRows() {
        return maxRows;
    }

    @Override
    public ResultSerializer summary(MetaData metaData, Row row) {
        super.summary(metaData, row);
        HtmlCell[] strings = format(metaData.getColumns(), row);
        reportData.setSummary(new SummaryData(metaData, strings));
        return this;
    }

    private HtmlCell[] format(List<Column> columns, Row row) {
        HtmlCell[] cells = new HtmlCell[columns.size()];
        for (int i = 0; i < cells.length; i++) {
            Column column = columns.get(i);
            Object value = row.get(column);
            ValueFormatter<Object> formatter = registry.get(column);
            HtmlCell accessor = new HtmlCell();
            formatter.formatHtml(accessor, value, context);
            cells[i] = accessor;
        }
        return cells;
    }

}
