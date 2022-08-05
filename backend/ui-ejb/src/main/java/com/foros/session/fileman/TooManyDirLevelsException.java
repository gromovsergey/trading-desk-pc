package com.foros.session.fileman;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class TooManyDirLevelsException extends FileManagerException {
    public TooManyDirLevelsException(String message) {
        super(message);
    }
}
