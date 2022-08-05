package com.foros.reporting.serializer.formatter.registry;

import com.foros.reporting.RowType;

import java.util.HashMap;
import java.util.Map;

public class ValueFormatterRegistryHolder {

    private Map<RowType, ValueFormatterRegistryChain> holder = new HashMap<RowType, ValueFormatterRegistryChain>();

    public ValueFormatterRegistryHolder registry(RowType type, ValueFormatterRegistry registry) {
        registry(type).registry(registry);
        return this;
    }

    public ValueFormatterRegistryHolder registries(ValueFormatterRegistry registry, RowType rowType) {
        this.registry(rowType).registry(registry);
        return this;
    }

    public ValueFormatterRegistryChain registry(RowType type) {
        ValueFormatterRegistryChain chain = holder.get(type);

        if (chain == null) {
            chain = new ValueFormatterRegistryChain();
            holder.put(type, chain);
        }

        return chain;
    }

    public ValueFormatterRegistryHolder registry(ValueFormatterRegistryHolder holder) {
        if (holder != null) {
            for (Map.Entry<RowType, ValueFormatterRegistryChain> entry : holder.holder.entrySet()) {
                registry(entry.getKey()).registry(entry.getValue());
            }
        }

        return this;
    }

}
