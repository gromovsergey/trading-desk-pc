package com.foros.framework;

import com.foros.framework.support.RequestContextsAware;
import com.foros.util.StringUtil;
import com.foros.util.context.Contexts;
import com.foros.util.context.RequestContexts;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ContextsInterceptor extends AbstractInterceptor {
    private Set<String> skippedResults = new HashSet<String>();

    public String intercept(ActionInvocation invocation) throws Exception {
        invocation.addPreResultListener(new ProcessPreResultListener());
        return invocation.invoke();
    }

    private class ProcessPreResultListener implements PreResultListener {
        @Override
        public void beforeResult(ActionInvocation actionInvocation, String s) {
            Object action = actionInvocation.getAction();

            if (action instanceof RequestContextsAware && !skippedResults.contains(s)) {
                HttpServletRequest servletRequest = ServletActionContext.getRequest();
                RequestContexts contexts = Contexts.getContexts(servletRequest);
                ((RequestContextsAware) action).switchContext(contexts);
            }
        }
    }

    public void setSkippedResults(String results) {
        this.skippedResults = new HashSet<String>(Arrays.asList(StringUtil.splitByComma(results)));
    }
}
