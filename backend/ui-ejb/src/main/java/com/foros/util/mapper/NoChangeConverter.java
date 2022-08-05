package com.foros.util.mapper;

/**
 * Author: Boris Vanin
 */
public class NoChangeConverter<V> implements Converter<V, V> {

    public V item(V value) {
        return value;
    }

}
