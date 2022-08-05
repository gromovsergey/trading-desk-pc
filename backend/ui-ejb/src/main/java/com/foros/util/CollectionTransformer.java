package com.foros.util;

import com.foros.util.mapper.Converter;

import java.util.ArrayList;
import java.util.Collection;

public abstract class CollectionTransformer<S, T> implements Converter<S, T> {
    public Collection<T> transform(final Collection<? extends S> target) {
        Collection<T> transformedCollection = new ArrayList<T>();

        for (S s : target) {
            transformedCollection.add(item(s));
        }

        return transformedCollection;
    }
}
