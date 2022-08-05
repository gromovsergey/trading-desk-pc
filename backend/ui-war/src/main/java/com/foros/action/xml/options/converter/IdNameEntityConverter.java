package com.foros.action.xml.options.converter;

import com.foros.model.IdNameEntity;

public class IdNameEntityConverter extends AbstractConverter<IdNameEntity> {
    public IdNameEntityConverter(boolean concatForValue) {
        super(concatForValue);
    }

    @Override
    protected String getName(IdNameEntity value) {
        return value.getName();
    }

    @Override
    protected String getValue(IdNameEntity value) {
        return value.getId().toString();
    }
}