package com.foros.reporting;

public class ReportingException extends RuntimeException {

    public ReportingException() {
    }

    public ReportingException(String message) {
        super(message);
    }

    public ReportingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportingException(Throwable cause) {
        super(cause);
    }

}
