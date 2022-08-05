package com.foros.session.fileman;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class BadZipException extends FileManagerException {
    public BadZipException(Throwable cause) {
        super(cause);
    }

    public BadZipException(String message) {
        super(message);
    }

    public BadZipException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public boolean isArchiveException() {
        return true;
    }

    @Override
    public void setArchiveException(boolean archiveException) {
        // ignore
    }
}
