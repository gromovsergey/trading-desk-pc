package com.foros.session.fileman;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class FileSizeException extends FileManagerException {
    private long threshold;

    public FileSizeException() {
        super("Can't upload file. Allowed file size exceeded.");
    }

    public FileSizeException(String fileName, long threshold) {
        super("Can't to upload file '" + fileName + "' with size more than " + threshold + " byte(s)");
        this.threshold = threshold;
    }

    public long getThreshold() {
        return threshold;
    }
}
