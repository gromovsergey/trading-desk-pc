package com.foros.util.bulk;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

public interface BulkReader extends Closeable {

    void setBulkReaderHandler(BulkReaderHandler handler);

    void read() throws IOException;

    public interface BulkReaderHandler {
        void handleRow(BulkReaderRow row);
    }

    public interface BulkReaderRow {
        int getRowNum();

        int getColumnCount();

        Object getValue(int i);

        String getStringValue(int i);

        BigDecimal getNumericValue(int i) throws ParseException;

        Date getDateValue(int i) throws ParseException;
    }
}
