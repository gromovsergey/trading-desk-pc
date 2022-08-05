package com.foros.util.bean;

/**
 * Basic interface for element filter
 * @author: alexey_chernenko
 */
public interface Filter<T> {
    public boolean accept(final T element);
}
