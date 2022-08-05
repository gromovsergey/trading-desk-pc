package com.foros.reporting.tools.query.impala;

import com.foros.reporting.tools.query.ConnectionUnavailableSQLException;

public class ImpalaUnavailableSQLException extends ConnectionUnavailableSQLException {
    public ImpalaUnavailableSQLException(Exception root) {
        super(root);
    }
}
