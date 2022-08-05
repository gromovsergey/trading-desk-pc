package com.foros.util.bulk.csv;

import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.util.DateHelper;
import com.foros.util.NumberUtil;
import com.foros.util.StringUtil;
import com.foros.util.bulk.BulkReader;
import com.foros.util.csv.CsvReader;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CsvBulkReader implements BulkReader {
    private CsvReader csvReader;
    private TimeZone timeZone;
    private BulkReaderHandler handler = null;
    private Locale locale = CurrentUserSettingsHolder.getLocale();


    public CsvBulkReader(Reader reader, char delimiter, TimeZone timeZone) throws IOException {
        this.csvReader = new CsvReader(reader, delimiter);
        this.timeZone = timeZone;
    }

    @Override
    public void setBulkReaderHandler(BulkReaderHandler handler) {
        this.handler = handler;
    }

    @Override
    public void read() throws IOException {
        if(!csvReader.readHeaders()) {
            throw ConstraintViolationException.newBuilder("errors.invalid.header").build();
        }
        CsvBulkReaderRow row = new CsvBulkReaderRow(csvReader.getLineNumber(), csvReader.getHeaders());
        handler.handleRow(row);

        while (csvReader.readRecord()) {
            row = new CsvBulkReaderRow(csvReader.getLineNumber(), csvReader.getColumnCount());
            for (int i = 0; i < csvReader.getColumnCount(); i++) {
                row.addValue(i, csvReader.get(i));
            }
            handler.handleRow(row);
        }
    }

    @Override
    public void close() throws IOException {
        csvReader.close();
    }

    public class CsvBulkReaderRow implements BulkReaderRow {
        private int rowNum;
        private String[] values;

        public CsvBulkReaderRow(int rowNum, String[] values) {
            this.rowNum = rowNum;
            this.values = values;
        }

        public CsvBulkReaderRow(int rowNum, int columnsCount) {
            this.rowNum = rowNum;
            values = new String[columnsCount];
        }

        @Override
        public int getRowNum() {
            return rowNum;
        }

        @Override
        public int getColumnCount() {
            return values.length;
        }

        public void addValue(int index, String value) {
            values[index] = value;
        }

        @Override
        public Object getValue(int i) {
            return values[i];
        }

        @Override
        public String getStringValue(int i) {
            String str = StringUtil.trimProperty(values[i]);
            return str.isEmpty() ? null : str;
        }

        @Override
        public BigDecimal getNumericValue(int i) throws ParseException {
            return NumberUtil.parseBigDecimal(getStringValue(i));
        }

        @Override
        public Date getDateValue(int i) throws ParseException {
            return DateHelper.parseDateTime(getStringValue(i), timeZone, locale);
        }
    }
}
