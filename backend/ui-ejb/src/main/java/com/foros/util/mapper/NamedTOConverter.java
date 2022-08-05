package com.foros.util.mapper;

import com.foros.session.NamedTO;

public class NamedTOConverter implements Converter<Long, NamedTO> {
    @Override
    public NamedTO item(Long value) {
        return new NamedTO(value, null);
    }
}
