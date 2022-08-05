package com.foros.birt.web.util;

import com.foros.restriction.AccessRestrictedException;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.birt.core.exception.BirtException;

public abstract class ExceptionUtils {

    public static int getResponseStatusByException(Exception e) {
        if (e instanceof AccessRestrictedException) {
            return HttpServletResponse.SC_FORBIDDEN;
        } else {
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
    }

    public static String getMessageByException(Exception e) {

        Exception cause;

        cause = com.foros.util.ExceptionUtil.getCause(e, SecurityException.class);
        if (cause != null) {
            return "Access is forbidden";
        }

        cause = com.foros.util.ExceptionUtil.getCause(e, EntityNotFoundException.class);
        if (cause != null) {
            return "Entity not found: " + cause.getMessage();
        }

        cause = com.foros.util.ExceptionUtil.getCause(e, BirtException.class);
        if (cause != null) {
            return cause.getMessage();
        }

        if (e.getMessage() != null) {
            return e.getMessage();
        }

        return "Unexpected error";
    }
}
