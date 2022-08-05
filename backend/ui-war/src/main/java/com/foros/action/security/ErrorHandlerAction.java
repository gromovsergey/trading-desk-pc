package com.foros.action.security;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;

public class ErrorHandlerAction extends BaseActionSupport {

    private String errorCode;

    @ReadOnly
    public String handleError() throws Exception {
        return SUCCESS;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}

