package com.foros.action.xml.options.converter;

import com.foros.session.NamedTO;

public class NamedTOConverter extends AbstractConverter<NamedTO> {
    public NamedTOConverter(boolean concatForValue) {
        super(concatForValue);
    }

    @Override
    protected String getName(NamedTO value) {
        return value.getName();
    }

    @Override
    protected String getValue(NamedTO value) {
        return value.getId() == null ? "" : value.getId().toString();
    }
}
