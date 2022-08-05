package com.foros.framework;

import com.opensymphony.xwork2.ActionInvocation;
import org.apache.struts2.dispatcher.ServletDispatcherResult;

public class ClearContextDispatcherResult extends ServletDispatcherResult {

    public void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        invocation.getStack().getRoot().clear();

        super.doExecute(finalLocation, invocation);
    }
}
