package com.foros.action.xml.options.filter;

import java.util.Collection;

public interface Filter<T> {
    void filter(Collection<? extends T> options);
}
