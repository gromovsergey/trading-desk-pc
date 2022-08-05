package com.foros.session.fileman;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class AccountSizeExceededException extends FileManagerException {
    public AccountSizeExceededException(String message) {
        super(message);
    }
}
