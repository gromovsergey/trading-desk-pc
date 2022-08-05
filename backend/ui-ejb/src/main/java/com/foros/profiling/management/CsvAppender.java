package com.foros.profiling.management;

public class CsvAppender {

    private StringBuilder builder;
    private String delimiter;

    public CsvAppender(StringBuilder builder, String delimiter) {
        this.builder = builder;
        this.delimiter = delimiter;
    }

    public CsvAppender field(String value) {
        if (value != null) {
            builder.append("\"").append(value).append("\"");
        }

        return addDelimiter();
    }

    public CsvAppender field(Long value) {
        if (value != null) {
            builder.append(value);
        }

        return addDelimiter();
    }

    public CsvAppender delimiters(int count) {
        for (int i = 0; i < count; i++) {
            addDelimiter();
        }

        return this;
    }

    public CsvAppender addComment(String text) {
        // todo
        builder.append("# ").append(text).append("\n");
        return this;
    }

    public CsvAppender addDelimiter() {
        builder.append(delimiter);
        return this;
    }

    public CsvAppender endLine() {
        builder.append("\n");
        return this;
    }

}
