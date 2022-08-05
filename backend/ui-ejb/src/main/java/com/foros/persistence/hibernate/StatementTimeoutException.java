package com.foros.persistence.hibernate;

import java.sql.SQLException;
import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class StatementTimeoutException extends RuntimeException {
    private String sql;

    public StatementTimeoutException(String sql, SQLException root) {
        super("Statement timeout while executing query" , root);
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + ":\n=== sql ===\n" + sql + "\n=== sql ===";
    }
}
