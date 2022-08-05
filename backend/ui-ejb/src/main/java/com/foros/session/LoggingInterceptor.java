package com.foros.session;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.commons.lang.ArrayUtils;

public class LoggingInterceptor {
    private static final Logger logger = Logger.getLogger(LoggingInterceptor.class.getName());

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        if (logger.isLoggable(Level.INFO)) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(context.getTarget().getClass().getSimpleName());
                sb.append(".");
                sb.append(context.getMethod().getName());
                sb.append("(");
                if (context.getParameters() != null) {
                    for (int i = 0; i < context.getParameters().length; i++) {
                        if (i > 0) {
                            sb.append(", ");    
                        }
                        sb.append(convertToString(context.getParameters()[i]));
                    }
                }
                sb.append(")");
                logger.log(Level.INFO, sb.toString());
            } catch (Exception e) {
                logger.log(Level.WARNING, "Exception in LoggingInterceptor: " + e.getMessage(), e);
            }
        }
        return context.proceed();
    }

    private Object convertToString(Object param) {
        if (param == null) {
            return "null";
        }
        if (param.getClass().isArray()) {
            return ArrayUtils.toString(param);
        }
        if (param.getClass().isAssignableFrom(Collection.class)) {
            return Arrays.toString(((Collection<?>) param).toArray());
        }
        return param.toString();
    }

}
