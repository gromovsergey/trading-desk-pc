package com.foros.framework;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;

import java.util.Map;
import org.apache.struts2.dispatcher.ServletActionRedirectResult;

/**
 *
 * @author alexey_koloskov
 */
public class InputResult extends ServletActionRedirectResult {
    public static final String INPUT_PARAMS = "inputParams";
    public static final String INPUT_ERRORS = "inputErrors";

    @Override
    public void execute(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        Map session = invocation.getInvocationContext().getSession();

        session.put(INPUT_PARAMS, invocation.getInvocationContext().getParameters());

        if (action instanceof ValidationAware) {
            session.put(INPUT_ERRORS, ((ValidationAware)action).getFieldErrors());
        }

        super.execute(invocation);
    }
}
