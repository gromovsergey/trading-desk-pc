package com.foros.session.fileman;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class FileManagerException extends RuntimeException {
    private boolean archiveException;

    public FileManagerException(Throwable cause) {
        super(cause);
    }

    public FileManagerException(String message) {
        super(message);
    }

    public FileManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public boolean isArchiveException() {
        return archiveException;
    }

    public void setArchiveException(boolean archiveException) {
        this.archiveException = archiveException;
    }
}
