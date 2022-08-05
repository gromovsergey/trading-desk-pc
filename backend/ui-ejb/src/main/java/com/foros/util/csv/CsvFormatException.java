package com.foros.util.csv;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class CsvFormatException extends RuntimeException {

    private int line;
    private int rawLine;
    private int rawColumn;

    public CsvFormatException(String msg, int line, int rawLine, int rawColumn) {
        super(msg);
        this.line = line;
        this.rawLine = rawLine;
        this.rawColumn = rawColumn;
    }

    public int getLine() {
        return line;
    }

    public int getRawLine() {
        return rawLine;
    }

    public int getRawColumn() {
        return rawColumn;
    }
}
