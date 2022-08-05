package com.foros.framework;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.HttpHeaderResult;

import com.opensymphony.xwork2.ActionInvocation;

/**
 * Extend the {@link HttpHeaderResult} to also support an "errorCode"
 * parameter that is passed as sendError() rather than setStatus() on
 * the response. So we follow standart way of errror proccessing by container.
 *
 * @author alexey_chernenko
 */

public class ErrorSupportingHttpHeaderResult extends HttpHeaderResult {
    private int errorCode = -1;

    /** Executes the action. */
    public void execute(final ActionInvocation invocation) throws Exception {
        super.execute(invocation);

        if (errorCode != -1) {
            HttpServletResponse response = ServletActionContext.getResponse();
            response.sendError(errorCode);
        }
    }

    /** @param ec The error code to set. */
    public void setErrorCode(final int ec) {
        errorCode = ec;
    }
}
