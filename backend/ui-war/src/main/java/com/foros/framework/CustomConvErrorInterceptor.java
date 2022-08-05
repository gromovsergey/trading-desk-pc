package com.foros.framework;

import com.foros.action.Invalidable;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.interceptor.StrutsConversionErrorInterceptor;

public class CustomConvErrorInterceptor extends StrutsConversionErrorInterceptor {
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ActionContext invocationContext = invocation.getInvocationContext();
        Map conversionErrors = invocationContext.getConversionErrors();
        ValueStack stack = invocationContext.getValueStack();
        HashMap fakie = null;

        for (Iterator iterator = conversionErrors.entrySet().iterator();
                iterator.hasNext();) {
            Map.Entry entry = (Map.Entry)iterator.next();
            String propertyName = (String)entry.getKey();
            Object value = entry.getValue();

            if (shouldAddError(propertyName, value)) {

                if (isEntityNotFoundShouldBeThrown(invocation, propertyName)) {
                    throw new EntityNotFoundException("Entity with id = " + getOverrideExprUnEscaped(invocation, value) + " not found");
                }

                String resFieldName = getResFieldName(propertyName);
                String message = XWorkConverter.getConversionErrorMessage(resFieldName, stack);

                Object action = invocation.getAction();
                if (action instanceof ValidationAware) {
                    ValidationAware va = (ValidationAware)action;
                    va.addFieldError(propertyName, message);
                }

                if (fakie == null) {
                    fakie = new HashMap();
                }

                fakie.put(propertyName, getOverrideExpr(invocation, value));
            } else {
                iterator.remove();
            }
        }

        if (fakie != null) {
            // if there were some errors, put the original (fake) values in place right before the result
            stack.getContext().put(ORIGINAL_PROPERTY_OVERRIDE, fakie);
            invocation.addPreResultListener(new PreResultListener() {
                public void beforeResult(ActionInvocation invocation, String resultCode) {
                    Map fakie = (Map)invocation.getInvocationContext().get(ORIGINAL_PROPERTY_OVERRIDE);

                    if (fakie != null) {
                        invocation.getStack().setExprOverrides(fakie);
                    }
                }
            });
        }

        Object action = invocation.getAction();
        // todo: note that it is assumend that in case of conversion errors, no further custom interceptors will be invoked
        if ((fakie != null && !fakie.isEmpty()) && action instanceof Invalidable) {
            // few conversion errors exist
            ((Invalidable) action).invalid();
        }

        return invocation.invoke();
    }

    private boolean isEntityNotFoundShouldBeThrown(ActionInvocation invocation, String propertyName) {
        if (propertyName == null) {
            return false;
        }

        if (!propertyName.toLowerCase().equals("id")) {
            return false;
        }

        ActionContext context = invocation.getInvocationContext();
        HttpServletRequest request = (HttpServletRequest) context.get(StrutsStatics.HTTP_REQUEST);

        return request.getQueryString().contains(propertyName);
    }

    // Transform full property name to field name eligible for finding readable text from resources
    private String getResFieldName(String propertyName) {
        String fieldName;
        
        //transform array-based field name
        fieldName = propertyName.replaceAll("\\[\\w*\\]", "");
        if (fieldName.lastIndexOf('.') >= 0) {
            fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
        }
        
        return fieldName;
    }

    private Object getOverrideExprUnEscaped(ActionInvocation invocation, Object value) {
        ValueStack stack = invocation.getStack();

        try {
            stack.push(value);

            return stack.findString("top");
        } finally {
            stack.pop();
        }
    }
}
