package com.foros.action.xml.options;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.action.xml.options.converter.Converter;
import com.foros.action.xml.options.filter.Filter;
import com.foros.util.NameValuePair;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractOptionsAction<T> extends AbstractXmlAction<Collection<NameValuePair<String, String>>> {
    private Converter<? super T> converter;
    private Filter<? super T> filter;

    protected abstract Collection<? extends T> getOptions() throws ProcessException;

    protected AbstractOptionsAction(Converter<? super T> converter) {
        this.converter = converter;
    }

    protected AbstractOptionsAction(Converter<? super T> converter, Filter<? super T> filter) {
        this.converter = converter;
        this.filter = filter;
    }

    public Converter<? super T> getConverter() {
        return converter;
    }

    public void setConverter(Converter<? super T> converter) {
        this.converter = converter;
    }

    protected Filter<? super T> getFilter() {
        return filter;
    }

    protected void setFilter(Filter<? super T> filter) {
        this.filter = filter;
    }

    @Override
    protected Collection<NameValuePair<String, String>> generateModel() throws ProcessException {
        Collection<? extends T> options = getOptions();
        filter(options);
        return convert(options);
    }

    private Collection<NameValuePair<String, String>> convert(Collection<? extends T> options) {
        Collection<NameValuePair<String, String>> result = new ArrayList<NameValuePair<String, String>>();
        for (T option : options) {
            result.add(converter.convert(option));
        }

        return result;
    }

    protected void filter(Collection<? extends T> options) {
        if (filter != null) {
            filter.filter(options);
        }
    }
}
