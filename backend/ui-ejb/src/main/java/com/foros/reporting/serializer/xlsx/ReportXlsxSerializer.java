package com.foros.reporting.serializer.xlsx;

import com.foros.reporting.Row;
import com.foros.reporting.TooManyRowsException;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.serializer.formatter.ExcelCellAccessor;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryHolder;
import com.foros.session.reporting.PreparedParameter;
import com.foros.session.reporting.PreparedParameterBuilder;
import com.foros.util.StringUtil;

import java.io.OutputStream;
import java.util.List;

public class ReportXlsxSerializer extends XlsxSerializer {

    private List<PreparedParameter> parameters;
    private int exportRowsCount = 0;
    private int exportMaxRows;
    private String reportTitle;

    public ReportXlsxSerializer(OutputStream stream, ValueFormatterRegistryHolder customRegistry, ExcelStyles styles, int exportMaxRows, String reportTitle) {
        super(stream, customRegistry, styles);
        this.exportMaxRows = exportMaxRows;
        this.reportTitle = reportTitle;
    }

    @Override
    protected void beforeHeader(MetaData metaData) {
        // output title and parameters only to first sheet
        if (workbook.getNumberOfSheets() == 1) {
            serializeTitle(metaData);
            serializeParameters();
        }
    }

    @Override
    public void row(Row row) {
        exportRowsCount++;
        if (exportRowsCount > exportMaxRows) {
            throw new TooManyRowsException(exportMaxRows);
        }
        super.row(row);
    }

    private void serializeTitle(MetaData metaData)  {
        if (reportTitle == null) {
            return;
        }
        rowCounter++;
        sheetRow = sheet.createRow(rowCounter);
        ExcelCellAccessor cellAccessor = getCellAccessor(0);

        String systemTitle = StringUtil.getLocalizedString("systemTitle", context.getLocale());
        cellAccessor.setString(systemTitle + " " + reportTitle);
        cellAccessor.addStyle(Styles.title());
        rowCounter++;
    }

    private void serializeParameters() {
        ExcelCellAccessor cellAccessor;
        if (parameters != null && !parameters.isEmpty()) {
            for (PreparedParameter parameter : parameters) {
                if (parameter.getValueText() != null) {
                    rowCounter++;
                    sheetRow = sheet.createRow(rowCounter);
                    cellAccessor = getCellAccessor(0);
                    cellAccessor.setString(parameter.getName());
                    cellAccessor.addStyle(Styles.parameterName());
                    cellAccessor = getCellAccessor(1);
                    cellAccessor.setString(parameter.getValueText());
                    cellAccessor.addStyle(Styles.parameterValue());
                }
            }
            rowCounter++;
        }
    }

    @Override
    public XlsxSerializer preparedParameters(PreparedParameterBuilder.Factory factory) {
        parameters = factory.builder(context.getLocale()).parameters();
        return this;
    }

    @Override
    public int getMaxRows() {
        return exportMaxRows;
    }
}
