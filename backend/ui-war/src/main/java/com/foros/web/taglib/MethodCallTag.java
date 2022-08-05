package com.foros.web.taglib;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.opensymphony.xwork2.ActionContext;

public class MethodCallTag extends TagSupport implements ParameterizedTag {
    protected List<MethodParameter> methodParameters = new ArrayList<MethodParameter>();
    protected Object target;
    protected String method;
    protected String scope = "page";
    protected String result;

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setResult(String result) {
        this.result = result;
    }
    
    public void addParameter(Object value, String className) {
        Class paramClass = null;
        // use class name if specified
        if (className != null) {
            try {
                paramClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        // use object class
        if (className == null && value != null) {
            paramClass = value.getClass();
        }

        // ups..
        if (paramClass == null) {
            throw new RuntimeException("Can't find parameter class");
        }

        methodParameters.add(new MethodParameter(value, paramClass));
    }

    @Override
    public int doStartTag() throws JspException {
        return Tag.EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() throws JspException {
        Object callResult = MethodHelper.invokeMethod(target, method, methodParameters);

        if ("application".equals(scope)) {
            super.pageContext.getServletContext().setAttribute(result, callResult);
        } else if ("session".equals(scope)) {
            pageContext.getSession().setAttribute(result, callResult);
        } else if ("request".equals(scope)) {
            pageContext.getRequest().setAttribute(result, callResult);
        } else if ("page".equals(scope)) {
            pageContext.setAttribute(result, callResult);
        } else {
            ActionContext.getContext().put(result, callResult);
        }

        //clean up routine
        cleanUp();
        
        return Tag.EVAL_PAGE;
    }

    @Override
    public void release() {
        super.release();
        cleanUp();
    }

    private void cleanUp() {
        if (methodParameters != null) {
            methodParameters.clear();
        }

        target = null;
        method = null;
        scope = null;
        result = null;
    }

    private static class MethodHelper {
        public static Object invokeMethod(Object targetObject, String targetMethod, List<MethodParameter> methodParameters)  throws JspException {
            Object result = null;

            if (targetObject == null || targetMethod == null || targetMethod.trim().length() == 0) {
                return result;
            }

            Class clazz = targetObject.getClass();
            Class[] parameterTypes = getParameterTypes(methodParameters);

            List<Object> values = new ArrayList<Object>(methodParameters.size());
            for (MethodParameter parameter : methodParameters) {
                values.add(parameter.getValue());
            }

            try {
                Method method = clazz.getMethod(targetMethod, parameterTypes);
                result = method.invoke(targetObject, values.toArray());
            } catch(Exception e) {
                throw new JspException(e);
            }

            return result;
        }

        private static Class[] getParameterTypes(List<MethodParameter> methodParameters) {
            Class[] result = new Class[methodParameters.size()];

            for (int index = 0; index < methodParameters.size(); index++) {
                result[index] = methodParameters.get(index).getParamClass();
            }

            return result;
        }        
    }

    static class MethodParameter {
        private Object value;
        private Class paramClass;

        private MethodParameter(Object value, Class paramClass) {
            this.value = value;
            this.paramClass = paramClass;
        }

        public Object getValue() {
            return value;
        }

        public Class getParamClass() {
            return paramClass;
        }
    }
}
