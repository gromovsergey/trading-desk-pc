package com.foros.framework;

import com.foros.action.ConstraintValidationsAware;
import com.foros.validation.constraint.violation.ConstraintViolation;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsStatics;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class MessageStoreInterceptor extends AbstractInterceptor implements StrutsStatics {
    public static final String ACTION_ERRORS_KEY = "com.foros.request_errors.action";
    public static final String FIELD_ERRORS_KEY = "com.foros.request_errors.field";
    public static final String CONSTRAINT_VIOLATIONS_KEY = "com.foros.request_errors.constraintViolations";
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        restoreErrors(invocation, action);
        return invocation.invoke();
    }

    private void restoreErrors(ActionInvocation invocation, Object action) {
        if (action instanceof ValidationAware) {
            ValidationAware vaction = (ValidationAware) action;
            ActionContext context = invocation.getInvocationContext();
            restoreActionErrors(context, vaction);
            restoreFieldErrors(context, vaction);
            if (action instanceof ConstraintValidationsAware) {
                restoreConstraintValidations(context, (ConstraintValidationsAware) action);
            }
        }
    }

    private void restoreConstraintValidations(ActionContext context, ConstraintValidationsAware action) {
        Collection<ConstraintViolation> errors = (Collection) getRequest(context).getAttribute(CONSTRAINT_VIOLATIONS_KEY);
        if (errors == null) {
            return;
        }
        action.getConstraintViolations().addAll(errors);

    }

    private void restoreActionErrors(ActionContext context, ValidationAware action) {
        Collection<String> errors = (Collection) getRequest(context).getAttribute(ACTION_ERRORS_KEY);
        if (errors == null) {
            return;
        }
        for (String error : errors) {
            action.addActionError(error);
        }
    }

    private void restoreFieldErrors(ActionContext context, ValidationAware action) {
        Map<String, List<String>> errors = (Map<String, List<String>>) getRequest(context).getAttribute(FIELD_ERRORS_KEY);
        if (errors == null) {
            return;
        }
        for (Map.Entry<String, List<String>> entry : errors.entrySet()) {
            for (String message : entry.getValue()) {
                action.addFieldError(entry.getKey(), message);
            }
        }
    }

    public static void saveErrors(ActionContext context, ValidationAware action) {
        HttpServletRequest request = getRequest(context);
        request.setAttribute(ACTION_ERRORS_KEY, action.getActionErrors());
        request.setAttribute(FIELD_ERRORS_KEY, action.getFieldErrors());
        if (action instanceof ConstraintValidationsAware) {
            request.setAttribute(CONSTRAINT_VIOLATIONS_KEY, ((ConstraintValidationsAware) action).getConstraintViolations());
        }
    }

    private static HttpServletRequest getRequest(ActionContext context) {
        return (HttpServletRequest) context.get(HTTP_REQUEST);
    }
}
