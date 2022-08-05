package com.foros.action.xml.options.converter;

import com.foros.util.NameValuePair;
import com.foros.util.PairUtil;

public abstract class AbstractConverter<T> implements Converter<T> {
    private boolean concatForValue = false;

    protected abstract String getName(T value);

    protected abstract String getValue(T value);

    public AbstractConverter(boolean concatForValue) {
        this.concatForValue = concatForValue;
    }

    public boolean isConcatForValue() {
        return concatForValue;
    }

    public void setConcatForValue(boolean concatForValue) {
        this.concatForValue = concatForValue;
    }

    @Override
    public NameValuePair<String, String> convert(T o) {
        String name = getName(o);
        String value = getValue(o);
        return new NameValuePair<String, String>(name, concatForValue ? PairUtil.createAsString(value, name) : value);
    }
}
