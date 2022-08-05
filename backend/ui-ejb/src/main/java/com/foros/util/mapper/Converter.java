package com.foros.util.mapper;

/**
 * Author: Boris Vanin
 */
public interface Converter<V, R> {

    R item(V value);

}
