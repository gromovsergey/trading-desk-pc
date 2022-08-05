package com.foros.action.xml.options.converter;

import com.foros.action.IdNameBean;

public class IdNameBeanConverter extends AbstractConverter<IdNameBean> {
    public IdNameBeanConverter(boolean concatForValue) {
        super(concatForValue);
    }

    @Override
    protected String getName(IdNameBean value) {
        return value.getName();
    }

    @Override
    protected String getValue(IdNameBean value) {
        return value.getId();
    }
}
