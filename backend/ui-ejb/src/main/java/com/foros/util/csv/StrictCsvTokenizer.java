package com.foros.util.csv;

import com.foros.util.StringUtil;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public class StrictCsvTokenizer implements Closeable {
    protected int lineNumber = 0;
    protected BufferedLineReader lineReader;
    protected StringBuilder sb = null;
    protected int col;
    protected int potentialSpaces;
    protected State state;
    protected final char quote;
    protected final char delimiter;

    public StrictCsvTokenizer(BufferedLineReader lineReader, char quote, char delimiter) throws IOException {
        this.quote = quote;
        this.delimiter = delimiter;
        this.lineReader = lineReader;
        this.sb = new StringBuilder(500);
    }

    private void addSpaces() {
        for (int i = 0; i < potentialSpaces; i++) {
            sb.append(' ');
        }
        potentialSpaces = 0;
    }

    public void close() throws IOException {
        lineReader.close();
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getRawLineNumber() {
        return lineReader.getLineNumber() + 1;
    }

    public int getRawColumnNumber() {
        return col + 1;
    }

    public final boolean readRecord(final List<String> result) throws IOException {
        return readRecord(result, false);
    }

    public final boolean readRecord(final List<String> result, boolean readHeader) throws IOException {
        result.clear();
        state = State.NORMAL;
        col = 0;
        potentialSpaces = 0;
        sb.delete(0, sb.length());

        if(lineReader.isEndOfFile()) {
            return false;
        }

        lineNumber++;
        if(lineReader.isEndOfLine()) {
            handleEmptyLine();
        }

        while (true) {

            final char c = !lineReader.isEndOfLine() ? lineReader.read() : '\n';

            switch (state) {

                case NORMAL:
                    if (c == delimiter) {
                        addSpaces();
                        addResult(result);
                        break;
                    } else if (c == ' ') {
                        potentialSpaces++;
                        break;
                    } else if (c == '\n') {
                        // save token
                        addSpaces();
                        addResult(result);
                        lineReader.nextLine();
                        return true;
                    } else if (c == quote) {
                        // quote first on line cannot be escaped
                        if (sb.length() == 0 && potentialSpaces == 0) {
                            state = State.QUOTED;
                            break; // read more
                        } else {
                            handleUnexpectedCharacter(c, readHeader);
                        }
                    } else {
                        // if just a normal character
                        addSpaces();
                        sb.append(c);
                    }
                    break;

                case QUOTED:
                    if (c == '\n') {
                        if (!lineReader.isEndOfFile()) {
                            sb.append(lineReader.readSeparators());
                            // parse the next line of the file
                            lineReader.nextLine();
                        } else {
                            handleUnexpectedQuotedFieldEnd(readHeader);
                        }
                        col = 0;
                        continue;
                    } else if (c == quote) {
                        // if next char is quote too
                        if (!lineReader.isEndOfLine() && lineReader.peek() == quote) {
                            // append quote
                            sb.append(c);
                            // skip next quote
                            lineReader.read();
                            break;
                        } else {
                            // a single quote, just change state
                            potentialSpaces = 0;
                            addResult(result);
                            state = State.AFTER_QUOTED;
                            break;
                        }
                    } else {
                        sb.append(c);
                    }
                    break;
                case AFTER_QUOTED:
                    if (c == delimiter) {
                        state = State.NORMAL;
                    } else if (c == '\n') {
                        lineReader.nextLine();
                        return true;
                    } else {
                        handleUnexpectedCharacter(c, readHeader);
                    }
                    break;
                default:
                    throw new IllegalStateException("this can never happen!");

            }

            col++;
        }
    }

    private void addResult(List<String> result) {
        result.add(sb.toString());
        sb.delete(0, sb.length());
    }

    protected void handleUnexpectedCharacter(char c, boolean readHeader) {
        if (readHeader) {
            throwException(StringUtil.getLocalizedString("errors.csv.header.unexpectedCharacter", c));
        } else {
            throwException(StringUtil.getLocalizedString("errors.csv.unexpectedCharacter", c));
        }
    }

    protected void handleUnexpectedQuotedFieldEnd(boolean readHeader) {
        if (readHeader) {
            throwException(StringUtil.getLocalizedString("errors.csv.header.unexpectedCharacter"));
        } else {
            throwException(StringUtil.getLocalizedString("errors.csv.unexpectedEndOfField"));
        }
    }

    protected void handleEmptyLine() {
        throwException(StringUtil.getLocalizedString("errors.csv.emptyLine"));
    }

    protected void throwException(String msg) {
        String loneColMsg = StringUtil.getLocalizedString("errors.csv", msg, getRawLineNumber(), getRawColumnNumber());
        throw new CsvFormatException(loneColMsg, lineNumber, getRawLineNumber(), getRawColumnNumber());
    }

    public enum State {
        NORMAL,
        QUOTED,
        AFTER_QUOTED
    }
}
