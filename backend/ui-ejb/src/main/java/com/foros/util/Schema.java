package com.foros.util;


public enum Schema {
    HTTP("http://"),
    HTTPS("https://"),
    DEFAULT("//");

    private final String value;

    Schema(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
