package com.foros.framework;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.interceptor.ExceptionMappingInterceptor;
import com.opensymphony.xwork2.util.TextParseUtil;

public class ForosExceptionMappingInterceptor extends ExceptionMappingInterceptor {
    private static final Logger logger = Logger.getLogger(ForosExceptionMappingInterceptor.class.getName());

    private Set<Class<?>> excludeExceptions = Collections.emptySet();

    private Set<Class<?>> oneLineExceptions = Collections.emptySet();

    private Set<Class<?>> parseClasses(String commaDelim) throws ClassNotFoundException {
        if (commaDelim == null || commaDelim.trim().length() == 0) {
            return Collections.emptySet();
        }
        Collection<String> excludeClasses =  TextParseUtil.commaDelimitedStringToSet(commaDelim);
        Set<Class<?>> res = new HashSet<Class<?>>();
        for (String className : excludeClasses) {
            res.add(Class.forName(className));
        }
        return res;
    }

    public void setExcludeExceptions(String commaDelim) throws ClassNotFoundException {
        excludeExceptions = parseClasses(commaDelim);
    }

    public void setOneLineExceptions(String commaDelim) throws ClassNotFoundException {
        oneLineExceptions = parseClasses(commaDelim);
    }

    @Override
    protected String findResultFromExceptions(List<ExceptionMappingConfig> exceptionMappings, Throwable t) {
        Throwable cause = ExceptionUtils.getRootCause(t);
        if (cause instanceof SecurityException) {
            t = cause;
        }
        return super.findResultFromExceptions(exceptionMappings, t);
    }

    @Override
    protected void handleLogging(Exception e) {
        Throwable cause = ExceptionUtils.getRootCause(e);
        if (cause == null) {
            cause = e;
        }

        if (excludeExceptions.contains(cause.getClass())) {
            return;
        }
        if (oneLineExceptions.contains(cause.getClass())) {
            logger.info(e.getMessage());
            return;
        }
        super.handleLogging(e);
    }
}
