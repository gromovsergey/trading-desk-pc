package com.foros.restriction;

import java.lang.reflect.Method;
import java.util.Map;
import javax.interceptor.InvocationContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

public class InvocationContextAdapter implements InvocationContext {

    private ProceedingJoinPoint joinPoint;

    public InvocationContextAdapter(ProceedingJoinPoint joinPoint) {
        this.joinPoint = joinPoint;
    }

    @Override
    public Object getTarget() {
        return joinPoint.getTarget();
    }

    @Override
    public Method getMethod() {
        return ((MethodSignature) joinPoint.getSignature()).getMethod();
    }

    @Override
    public Object[] getParameters() {
        return joinPoint.getArgs();
    }

    @Override
    public void setParameters(Object[] objects) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> getContextData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getTimer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object proceed() throws Exception {
        try {
            return joinPoint.proceed();
        } catch (Exception exception) {
            throw exception;
        } catch (Throwable throwable) {
            throw new UnsupportedOperationException(throwable);
        }
    }
}
