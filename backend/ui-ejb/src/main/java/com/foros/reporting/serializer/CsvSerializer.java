package com.foros.reporting.serializer;

import com.foros.reporting.ReportingException;
import com.foros.reporting.Row;
import com.foros.reporting.TooManyRowsException;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.ValueFormatter;
import com.foros.session.reporting.PreparedParameterBuilder;
import com.foros.util.ExceptionUtil;
import com.foros.util.csv.CsvWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

public class CsvSerializer extends ResultSerializerSupport {
    private static final String DEFAULT_LINE_DELIMITER = "\r\n";

    private CsvWriter writer;

    private int exportRowsCount = 0;
    private int exportMaxRows;

    public CsvSerializer(OutputStream stream, Locale locale, int exportMaxRows) {
        this(stream, locale, exportMaxRows, BulkFormat.CSV);
    }

    public CsvSerializer(OutputStream stream, Locale locale, int exportMaxRows, BulkFormat format) {
        this(newCsvWriter(stream, format), locale, exportMaxRows);
        format.addUnicodeSupport(stream);
    }

    public CsvSerializer(CsvWriter writer, Locale locale, int exportMaxRows) {
        super(null, new FormatterContext(locale));
        this.writer = writer;
        this.exportMaxRows = exportMaxRows;
    }

    private void write(String[] record) {
        try {
            writer.writeRecord(record);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setMetaData(MetaData metaData) {
        super.before(metaData);
    }

    @Override
    public void before(MetaData metaData) {

        super.before(metaData);

        List<Column> columns = metaData.getColumns();

        String[] record = new String[columns.size()];

        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            record[i] = resolveColumnNameAlias(column);
        }

        write(record);
    }

    private String resolveColumnNameAlias(Column column) {
        return registry.get(column).formatText(column, context);
    }

    @Override
    public void row(Row row) {
        exportRowsCount++;
        if (exportRowsCount > exportMaxRows) {
            throw new TooManyRowsException(exportMaxRows);
        }

        super.row(row);

        List<Column> columns = metaData.getColumns();

        String[] record = new String[columns.size()];

        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            Object value = row.get(column);

            ValueFormatter<Object> valueFormatter = registry.get(column);
            record[i] = valueFormatter.formatCsv(value, context);
        }

        write(record);
    }

    @Override
    public void close() {
        writer.flush();
        super.close();
    }

    @Override
    public void onError(ReportingException ex) {
        try {
            writer.writeRecord(new String[]{ExceptionUtil.getReportingErrorUserFriendlyMessage(ex)});
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
    }

    @Override
    public ResultSerializer preparedParameters(PreparedParameterBuilder.Factory factory) {
        return this;
    }

    private static CsvWriter newCsvWriter(OutputStream stream, BulkFormat format) {
        CsvWriter w = new CsvWriter(stream, format.getDelimiter(), Charset.forName(format.getEncoding()));
        w.setRecordDelimiter(DEFAULT_LINE_DELIMITER);
        w.setForceQualifier(true);
        return w;
    }
}
