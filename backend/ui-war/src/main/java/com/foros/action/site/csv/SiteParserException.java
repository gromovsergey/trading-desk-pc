package com.foros.action.site.csv;

public class SiteParserException extends Exception {
    private int row;
    private int column;
    private String key;
    private Object[] parameters;

    public SiteParserException(int column, String key, Object... params) {
        super(key);
        this.column = column;
        this.key = key;
        this.parameters = params;
    }

    public SiteParserException(String key, Object... params) {
        this(0, key, params);
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getRow() {
        return row;
    }

    public String getKey() {
        return key;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public int getColumn() {
        return column;
    }
}
