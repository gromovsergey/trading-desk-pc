package com.foros.util;

import com.foros.model.Identifiable;

public class IdentifiableCollectionTransformer<T extends Identifiable> extends CollectionTransformer<T, Long> {

    @Override
    public Long item(T value) {
        return value.getId();
    }
}
