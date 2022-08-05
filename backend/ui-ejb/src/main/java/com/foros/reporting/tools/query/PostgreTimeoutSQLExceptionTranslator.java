package com.foros.reporting.tools.query;

import com.foros.persistence.hibernate.StatementTimeoutException;
import com.foros.util.ExceptionUtil;

import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;

public class PostgreTimeoutSQLExceptionTranslator implements SQLExceptionTranslator {
    private SQLExceptionTranslator defaultTranslator = new SQLStateSQLExceptionTranslator();

    @Override
    public DataAccessException translate(String task, String sql, SQLException ex) {
        StatementTimeoutException timeoutException = ExceptionUtil.getPostgreTimeoutException(sql, ex);
        if (timeoutException != null) {
            return new DataAccessResourceFailureException(buildMessage(task, sql, ex), timeoutException);
        }
        return defaultTranslator.translate(task, sql, ex);
    }

    private String buildMessage(String task, String sql, SQLException ex) {
        return task + "; SQL [" + sql + "]; " + ex.getMessage();
    }

}
