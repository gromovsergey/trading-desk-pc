package com.foros.util;

import com.foros.action.account.ContextNotSetException;
import com.foros.persistence.hibernate.StatementTimeoutException;
import com.foros.session.creative.PreviewException;
import com.foros.session.fileman.FileSizeException;

import javax.persistence.EntityNotFoundException;

public class ErrorPageUtil {

    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
    public static String getUserFriendlyMessage(Exception e) {

        Exception cause;

        cause = ExceptionUtil.getCause(e, SecurityException.class);
        if (cause != null) {
            return "Access is forbidden";
        }

        cause = ExceptionUtil.getCause(e, EntityNotFoundException.class);
        if (cause != null) {
            return "Entity not found: " + cause.getMessage();
        }

        cause = ExceptionUtil.getCause(e, IllegalStateException.class);
        if (cause != null && "Post too large".equalsIgnoreCase(cause.getMessage())) {
            return "Error 500: " + cause.getMessage();
        }

        cause = ExceptionUtil.getCause(e, ContextNotSetException.class);
        if (cause != null) {
            return cause.getMessage();
        }

        cause = ExceptionUtil.getCause(e, StatementTimeoutException.class);
        if (cause != null) {
            return "Request timed out";
        }


        cause = ExceptionUtil.getCause(e, PreviewException.class);
        if (cause != null) {
            return "Unable to create Preview";
        }

        cause = ExceptionUtil.getCause(e, FileSizeException.class);
        if (cause != null) {
            return StringUtil.getLocalizedString("errors.file.sizeExceeded");
        }
        return "Unexpected error";
    }

}
