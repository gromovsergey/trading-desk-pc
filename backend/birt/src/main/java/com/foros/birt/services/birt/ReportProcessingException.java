package com.foros.birt.services.birt;

import java.util.List;

public class ReportProcessingException extends RuntimeException {

    private List<Exception> exceptions;

    public ReportProcessingException(String message, List<Exception> exceptions) {
        super(message, exceptions.isEmpty() ? null : exceptions.get(0));
        this.exceptions = exceptions;
    }

    public List<Exception> getExceptions() {
        return exceptions;
    }

}
