package com.foros.profiling;

import java.lang.reflect.Method;

public class StatisticKey {

    private String url;
    private Method method;
    private Method parentMethod;

    public StatisticKey(String url, Method method, Method parentMethod) {
        this.url = url;
        this.method = method;
        this.parentMethod = parentMethod;
    }

    public Method getParent() {
        return parentMethod;
    }

    public String getUrl() {
        return url;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatisticKey that = (StatisticKey) o;

        if (method != that.method) return false;
        if (parentMethod != null && parentMethod != that.parentMethod) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + method.hashCode();
        result = 31 * result + (parentMethod != null ? parentMethod.hashCode() : 0);
        return result;
    }
}
