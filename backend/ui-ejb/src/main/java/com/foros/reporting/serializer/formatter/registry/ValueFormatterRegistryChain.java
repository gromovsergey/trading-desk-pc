package com.foros.reporting.serializer.formatter.registry;

import com.foros.reporting.meta.Column;
import com.foros.reporting.serializer.formatter.ValueFormatter;

import java.util.LinkedList;
import java.util.List;

public class ValueFormatterRegistryChain implements ValueFormatterRegistry {

    private List<ValueFormatterRegistry> queue = new LinkedList<ValueFormatterRegistry>();

    protected ValueFormatterRegistryChain() {
    }

    public ValueFormatterRegistryImpl add() {
        ValueFormatterRegistryImpl registry = new ValueFormatterRegistryImpl();
        registry(registry);
        return registry;
    }

    public ValueFormatterRegistryChain defaultFormatter(ValueFormatter formatter) {
        return registry(new ValueFormatterRegistryImpl().defaultFormatter(formatter));
    }

    public ValueFormatterRegistryChain registry(ValueFormatterRegistry registry) {
        if (registry != null) {
            queue.add(0, registry);
        }

        return this;
    }

    @Override
    public <T> ValueFormatter<T> get(Column column) {
        for (ValueFormatterRegistry registry : queue) {
            ValueFormatter<T> formatter = registry.get(column);
            if (formatter != null) {
                return formatter;
            }
        }

        return DefaultFormatterRegistry.DEFAULT_REGISTRY.get(column);
    }

}
