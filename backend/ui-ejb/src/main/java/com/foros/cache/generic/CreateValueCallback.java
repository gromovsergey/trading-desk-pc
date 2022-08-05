package com.foros.cache.generic;

import java.util.Collection;

public interface CreateValueCallback<T, K> {
    T create(K key, Collection<?> tags);
}
