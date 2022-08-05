package com.foros.reporting.serializer.formatter.registry;

import com.foros.reporting.meta.Column;
import com.foros.reporting.serializer.formatter.DefaultValueFormatter;
import com.foros.reporting.serializer.formatter.ValueFormatter;

public final class ValueFormatterRegistries {

    private ValueFormatterRegistries() {
    }

    public static ValueFormatterRegistryImpl registry() {
        return new ValueFormatterRegistryImpl();
    }

    public static ValueFormatterRegistryChain chain() {
        return new ValueFormatterRegistryChain();
    }

    public static ValueFormatterRegistryHolder defaultAnd(ValueFormatterRegistryHolder registry) {
        return DefaultFormatterRegistry.createHolder().registry(registry);
    }

    public static ValueFormatterRegistryHolder defaultHolder() {
        return DefaultFormatterRegistry.createHolder();
    }

    public static ValueFormatterRegistryHolder holder() {
        return new ValueFormatterRegistryHolder();
    }

    public static ValueFormatterRegistry bulkDefaultAnd(ValueFormatterRegistry registry) {
        return chain()
                .registry(DefaultFormatterRegistry.DEFAULT_BULK_REGISTRY)
                .registry(registry);
    }

    public static ValueFormatterRegistry defaultUnparsedRowRegistry() {
        return new UnparsedRowRegistry();
    }

    private static class UnparsedRowRegistry implements ValueFormatterRegistry {
        @Override
        public <T> ValueFormatter<T> get(Column column) {
            //noinspection unchecked
            return (ValueFormatter<T>) DefaultValueFormatter.INSTANCE;
        }
    }
}
