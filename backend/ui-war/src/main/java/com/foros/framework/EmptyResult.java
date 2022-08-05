package com.foros.framework;

import com.opensymphony.xwork2.ActionInvocation;
import org.apache.struts2.dispatcher.StrutsResultSupport;

import javax.servlet.http.HttpServletResponse;

public class EmptyResult extends StrutsResultSupport {

    @Override
    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        HttpServletResponse response = (HttpServletResponse) invocation.getInvocationContext().get(HTTP_RESPONSE);
        response.setStatus(204);
    }
}