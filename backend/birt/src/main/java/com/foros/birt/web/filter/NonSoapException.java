package com.foros.birt.web.filter;

public class NonSoapException extends RuntimeException {
    private String key;

    public NonSoapException(String key) {
        super("Key: " + key);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
