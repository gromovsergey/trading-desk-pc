package com.foros.util.csv;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

public class CsvReader implements Closeable {
    private final StrictCsvTokenizer tokenizer;
    private final ArrayList<String> data = new ArrayList<String>(30);
    private final ArrayList<String> headers = new ArrayList<String>(30);

    public CsvReader(Reader reader) throws IOException {
        this(reader, ',');
    }

    public CsvReader(Reader reader, char delimiter) throws IOException {
        BufferedLineReader r = createLineReader(reader);
        tokenizer = createTokenizer(delimiter, r);
    }

    protected StrictCsvTokenizer createTokenizer(char delimiter, BufferedLineReader r) throws IOException {
        return new StrictCsvTokenizer(r, '"', delimiter);
    }

    protected BufferedLineReader createLineReader(Reader reader) throws IOException {
        return new BufferedLineReader(reader);
    }

    public boolean readRecord() throws IOException {
        return tokenizer.readRecord(data);
    }

    public int getColumnCount() {
        return data.size();
    }

    public int getLineNumber() {
        return tokenizer.getLineNumber();
    }

    public String get(int i) {
        return data.get(i);
    }

    public String[] getValues() {
        return data.toArray(new String[data.size()]);
    }

    public boolean readHeaders() throws IOException {
        return tokenizer.readRecord(headers, true);
    }

    public Integer getHeaderCount() {
        return headers.size();
    }

    public String[] getHeaders() {
        return headers.toArray(new String[headers.size()]);
    }

    @Override
    public void close() throws IOException {
        tokenizer.close();
    }

    public static void closeQuietly(CsvReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
}