package com.foros.reporting.serializer;

import com.foros.session.reporting.ExportFormat;

import java.io.IOException;
import java.io.OutputStream;

public enum BulkFormat {

    CSV(ExportFormat.CSV, "UTF-8", ',', new byte[] { -17, -69, -65 } /* EF BB BF */),
    TAB(ExportFormat.CSV_TAB, "UTF-16LE", '\t', new byte[] { -1, -2 } /* FF FE */),
    XLSX(ExportFormat.EXCEL);

    private ExportFormat format;
    private String encoding;
    private char delimiter;
    private byte[] bom;

    private BulkFormat(ExportFormat format) {
        this.format = format;
    }

    private BulkFormat(ExportFormat format, String encoding, char delimiter, byte[] bom) {
        this(format);
        this.encoding = encoding;
        this.delimiter = delimiter;
        this.bom = bom;
    }

    public void addUnicodeSupport(OutputStream fos) {
        try {
            fos.write(bom);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getEncoding() {
        return encoding;
    }

    public char getDelimiter() {
        return delimiter;
    }

    public ExportFormat getFormat() {
        return format;
    }
}
