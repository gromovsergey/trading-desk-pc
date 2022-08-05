package com.foros.reporting.tools.query.impala;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;

import java.sql.SQLException;

public class ImpalaUnavailableSQLExceptionTranslator implements SQLExceptionTranslator {
    @Override
    public DataAccessException translate(String task, String sql, SQLException ex) {
        if ("HY000".equals(ex.getSQLState())) {
            ImpalaUnavailableSQLException impalaException = new ImpalaUnavailableSQLException(ex);
            return new DataAccessResourceFailureException("Impala Database maintenance is in progress. Please, try later.", impalaException);
        }

        SQLExceptionTranslator defaultTranslator = new SQLStateSQLExceptionTranslator();
        return defaultTranslator.translate(task, sql, ex);
    }
}
