package com.foros.action.birt;

public class BirtException extends RuntimeException {
    private String errorCode;
    public BirtException(String errorCode) {
        super("Error code: " + errorCode);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
