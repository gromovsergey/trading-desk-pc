package com.foros.persistence.hibernate;

import com.foros.util.ExceptionUtil;

import java.sql.SQLException;
import org.hibernate.JDBCException;
import org.hibernate.exception.SQLStateConverter;
import org.hibernate.exception.ViolatedConstraintNameExtracter;

public class ForosExceptionConverter extends SQLStateConverter {

    public ForosExceptionConverter(ViolatedConstraintNameExtracter extracter) {
        super(extracter);
    }

    @Override
    protected JDBCException handledNonSpecificException(SQLException sqlException, String message, String sql) {
        StatementTimeoutException timeoutException = ExceptionUtil.getPostgreTimeoutException(sql, sqlException);
        if (timeoutException != null) {
            return new JDBCException(sql, new SQLException(timeoutException));
        }        
        return super.handledNonSpecificException(sqlException, message, sql);
    }
}
