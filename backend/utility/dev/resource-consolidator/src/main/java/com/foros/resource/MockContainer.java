package com.foros.resource;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.util.OgnlTextParser;
import com.opensymphony.xwork2.util.TextParser;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class MockContainer implements Container {
    private Map<Class, Object> instances ;

    MockContainer() {
        instances = new HashMap<Class, Object>();
        instances.put(TextParser.class, new OgnlTextParser());
        instances.put(XWorkConverter.class, new XWorkConverter() {});
    }

    @Override
    public void inject(Object o) {
    }

    @Override
    public <T> T inject(Class<T> implementation) {
        return null;
    }

    @Override
    public <T> T getInstance(Class<T> type, String name) {
        return null;
    }

    @Override
    public <T> T getInstance(Class<T> type) {
        return (T) instances.get(type);
    }

    @Override
    public Set<String> getInstanceNames(Class<?> type) {
        return null;
    }

    @Override
    public void setScopeStrategy(Scope.Strategy scopeStrategy) {
    }

    @Override
    public void removeScopeStrategy() {
    }
}
