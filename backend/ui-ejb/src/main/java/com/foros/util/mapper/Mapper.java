package com.foros.util.mapper;

/**
 * Author: Boris Vanin
*/
public interface Mapper<T, K, V> extends Converter<T, Pair<K, V>> {

    Pair<K, V> item(T value);

}
