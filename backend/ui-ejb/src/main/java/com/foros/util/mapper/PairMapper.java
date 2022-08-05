package com.foros.util.mapper;

/**
 * Author: Boris Vanin
 */
public class PairMapper<K, V> implements Mapper<Pair<K, V>, K, V> {

    public Pair<K, V> item(Pair<K, V> value) {
        return value;
    }

}