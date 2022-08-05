package app.programmatic.ui.reporting.tools;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import app.programmatic.ui.reporting.model.ReportRows;
import app.programmatic.ui.reporting.model.ReportRows.ColumnValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class JsonReportFormatter implements ReportFormatter {
    private static final int MAX_REPORT_ROWS = 100;

    @Override
    public ByteArrayOutputStream formatRows(ReportRows reportRows, ReportColumnFormatter formatter) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();

        boolean truncated = reportRows.getRows().size() > MAX_REPORT_ROWS;

        try (OutputStreamWriter sw = new OutputStreamWriter(result, Charset.forName("UTF8"));
            JsonGenerator generator = new JsonFactory().createGenerator(sw)) {
            generator.writeStartObject();

            if (truncated) {
                generator.writeFieldName("truncated");
                generator.writeBoolean(true);
            }

            generator.writeArrayFieldStart("headers");
            for (String header : reportRows.getHeader()) {
                generator.writeString(header);
            }
            generator.writeEndArray();

            generator.writeArrayFieldStart("rows");
            for (ColumnValue[] row : truncated ? reportRows.getRows().subList(0, MAX_REPORT_ROWS) : reportRows.getRows()) {
                generator.writeStartArray();
                writeRow(row, formatter, generator);
                generator.writeEndArray();
            }
            generator.writeEndArray();

            if (!truncated) {
                generator.writeArrayFieldStart("total");
                if (reportRows.getTotalRow() != null) {
                    writeRow(reportRows.getTotalRow(), formatter, generator);
                }
                generator.writeEndArray();
            }

            generator.writeEndObject();
            generator.flush();

            return result;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private void writeRow(ColumnValue[] values, ReportColumnFormatter formatter, JsonGenerator generator) throws IOException {
        for (ColumnValue value: values) {
            if (value != null && value.getValue() != null) {
                generator.writeString(formatter.format(value.getValue(), value.getColumn().getColumnType(), value.getCurrencySign()));
            } else {
                generator.writeString("");
            }
        }
    }
}

