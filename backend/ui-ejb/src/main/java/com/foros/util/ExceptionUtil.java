package com.foros.util;

import com.foros.persistence.hibernate.StatementTimeoutException;
import com.foros.reporting.ReportingException;

import java.sql.SQLException;
import java.util.List;
import org.apache.commons.lang.exception.ExceptionUtils;

public final class ExceptionUtil {
    private ExceptionUtil() {
    }

    /**
     * Return a root causing message. Usefull if need to extract message and we are not interested in stacktrace.
     *
     * Note: This method for administrative usage only. Please avoid its execution in user interfeces not for admins.
     */
    public static String getRootMessage(Exception e) {
        Throwable rootCause = ExceptionUtils.getRootCause(e);
        return rootCause != null ? rootCause.getMessage() : e.getMessage();
    }

     public static Exception getRootException(Exception e) {
        Throwable rootCause = ExceptionUtils.getRootCause(e);
        if (rootCause == null) {
            return e;
        } else if (rootCause instanceof Exception){
            return (Exception)rootCause;
        } else {
            throw new IllegalStateException("Root cause is not inherited from java.lang.Exception", rootCause);
        }
    }

    public static boolean hasCause(Exception e, Class<? extends Exception> clazz) {
        return ExceptionUtils.indexOfType(e, clazz) != -1;
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Exception> T getCause(Exception e, Class<T> clazz) {
        List<? extends Throwable> throwableList = ExceptionUtils.getThrowableList(e);
        for (Throwable throwable : throwableList) {
            if (clazz.isAssignableFrom(throwable.getClass())) {
                return (T) throwable;
            }
        }
        return null;
    }

    public static String getReportingErrorUserFriendlyMessage(ReportingException e) {
        if (e.getCause() instanceof SQLException) {
            SQLException sqle = (SQLException) e.getCause();
            if (getPostgreTimeoutException(null, sqle) != null) {
                return "Request timed out";
            }
        }
        return "Unexpected error";
    }

    public static StatementTimeoutException getPostgreTimeoutException(String sql, SQLException sqlException) {
        String sqlState = sqlException.getSQLState();
        if (sqlState != null && sqlState.contains("57014")) {
            return new StatementTimeoutException(sql, sqlException);
        }
        return null;
    }
}
