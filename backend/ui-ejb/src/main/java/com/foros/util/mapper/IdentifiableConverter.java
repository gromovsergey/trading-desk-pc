package com.foros.util.mapper;

import com.foros.model.Identifiable;

public class IdentifiableConverter<T extends Identifiable> implements Converter<T, Long> {
    @Override
    public Long item(T value) {
        return value.getId();
    }
}
