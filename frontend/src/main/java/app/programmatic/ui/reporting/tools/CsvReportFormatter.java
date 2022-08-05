package app.programmatic.ui.reporting.tools;

import com.opencsv.CSVWriter;
import app.programmatic.ui.reporting.model.ReportRows;
import app.programmatic.ui.reporting.model.ReportRows.ColumnValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import static app.programmatic.ui.reporting.model.ReportColumnLocation.SETTINGS;

public class CsvReportFormatter implements ReportFormatter {

    @Override
    public ByteArrayOutputStream formatRows(ReportRows reportRows, ReportColumnFormatter formatter) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(result));
        writer.writeNext(Arrays.copyOf(reportRows.getHeader(), reportRows.getHeader().length, String[].class));

        for (ColumnValue[] row : reportRows.getRows()) {
            writeRow(row, formatter, writer);
        }

        if (reportRows.getTotalRow() != null) {
            writeRow(reportRows.getTotalRow(), formatter, writer);
        }

        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private void writeRow(ColumnValue[] values, ReportColumnFormatter formatter, CSVWriter writer) {
        String[] buf = new String[values.length];
        int i = 0;
        for (ColumnValue value : values) {
            if (value == null || value.getValue() == null) {
                buf[i++] = "";
            } else {
                buf[i++] = formatter.format(value.getValue(), value.getColumn().getColumnType(), value.getCurrencySign());
            }
        }
        writer.writeNext(buf);
    }
}

