package com.foros.tools.csv;

import java.io.IOException;
import java.io.Reader;

/**
 * Helper class for line reading. Line separator any of "\n", "\r\n" or "\r".
 */
public class BufferedLineReader {
    private static final int CHAR_BUFFER_SIZE = 8192;
    private static final int MAX_LINE_SIZE = 102400;
    private Reader reader;

    private char cb[];
    private int charsInBuffer, nextCharPos;

    private int nextChar;
    private int lineNumber = 0;
    private int lineSize = 0;


    public BufferedLineReader(Reader reader) throws IOException {
        this.reader = reader;
        cb = new char[CHAR_BUFFER_SIZE];
        nextCharPos = charsInBuffer = 0;
        nextChar = read0();
        // skip BOM
        if (nextChar == '\uFEFF' || nextChar == '\uFFFE') {
            nextChar = read0();
        }
    }

    public boolean isEndOfFile() {
        return nextChar == -1 ;
    }

    public boolean isEndOfLine() {
        return nextChar == -1 || nextChar == '\n' || nextChar == '\r';
    }

    public char read() throws IOException {
        char res = peek();
        nextChar = read0();
        lineSize++;
        if (lineSize > MAX_LINE_SIZE) {
            throw new FileFormatException("errors.csv.invalidFormat");
        }

        return res;
    }

    public char peek() throws IOException {
        if(isEndOfLine()) {
            throw new IllegalStateException("EOL");
        }

        return (char) nextChar;
    }

    public void nextLine() throws IOException {
        readSeparators();
    }

    public String readSeparators() throws IOException {
        String res;
        int c = nextChar;
        switch (c) {
            case '\n':
                nextChar = read0();
                res = "\n";
                break;
            case '\r':
                nextChar = read0();
                if (nextChar == '\n') {
                    nextChar = read0();
                    res = "\r\n";
                } else {
                    res = "\r";
                }
                break;
            default:
                // no separators to read
                res = null;
                break;
        }

        if (res != null) {
            lineNumber++;
            lineSize = 0;
        }

        return res;
    }

    public void close() throws IOException {
        reader.close();
    }

    public int getLineNumber() {
        return lineNumber;
    }

    private void fill() throws IOException {
        int dst = 0;
        int n;
        do {
            n = reader.read(cb, dst, cb.length - dst);
        } while (n == 0);
        if (n > 0) {
            charsInBuffer = dst + n;
            nextCharPos = dst;
        }
    }

    private int read0() throws IOException {
        if (nextCharPos >= charsInBuffer) {
            fill();
            if (nextCharPos >= charsInBuffer)
                return -1;
        }

        return cb[nextCharPos++];
    }
}
