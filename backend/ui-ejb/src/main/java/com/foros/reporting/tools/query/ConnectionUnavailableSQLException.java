package com.foros.reporting.tools.query;

import javax.ejb.ApplicationException;
import javax.ejb.EJBException;

@ApplicationException(rollback = false)
public class ConnectionUnavailableSQLException extends EJBException {
    public ConnectionUnavailableSQLException(Exception root) {
        super(root);
    }
}
