package com.foros.resource;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;
import java.util.HashMap;
import java.util.Map;

public class MockStack implements ValueStack {
    private Map<String, Object> context = new HashMap<String, Object>();
    {
        context.put(ActionContext.CONTAINER, new MockContainer());
    }
    @Override
    public Map<String, Object> getContext() {
        return context;
    }

    @Override
    public void setDefaultType(Class defaultType) {

    }

    @Override
    public void setExprOverrides(Map<Object, Object> overrides) {

    }

    @Override
    public Map<Object, Object> getExprOverrides() {
        return null;
    }

    @Override
    public CompoundRoot getRoot() {
        return null;
    }

    @Override
    public void setValue(String expr, Object value) {

    }

    @Override
    public void setParameter(String expr, Object value) {

    }

    @Override
    public void setValue(String expr, Object value, boolean throwExceptionOnFailure) {

    }

    @Override
    public String findString(String expr) {
        return null;
    }

    @Override
    public String findString(String expr, boolean throwExceptionOnFailure) {
        return null;
    }

    @Override
    public Object findValue(String expr) {
        return null;
    }

    @Override
    public Object findValue(String expr, boolean throwExceptionOnFailure) {
        return null;
    }

    @Override
    public Object findValue(String expr, Class asType) {
        return null;
    }

    @Override
    public Object findValue(String expr, Class asType, boolean throwExceptionOnFailure) {
        return null;
    }

    @Override
    public Object peek() {
        return null;
    }

    @Override
    public Object pop() {
        return null;
    }

    @Override
    public void push(Object o) {
    }

    @Override
    public void set(String key, Object o) {
    }

    @Override
    public int size() {
        return 0;
    }
}
