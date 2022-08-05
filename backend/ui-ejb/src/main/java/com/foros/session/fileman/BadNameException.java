package com.foros.session.fileman;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class BadNameException extends FileManagerException {

    public BadNameException(String message) {
        super(message);
    }

}
