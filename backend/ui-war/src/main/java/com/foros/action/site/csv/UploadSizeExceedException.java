package com.foros.action.site.csv;

public class UploadSizeExceedException extends Exception {
    public UploadSizeExceedException() {
        super();
    }

    public UploadSizeExceedException(String message) {
        super(message);
    }
}
