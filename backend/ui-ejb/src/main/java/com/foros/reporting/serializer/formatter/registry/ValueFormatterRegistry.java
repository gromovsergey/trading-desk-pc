package com.foros.reporting.serializer.formatter.registry;

import com.foros.reporting.meta.Column;
import com.foros.reporting.serializer.formatter.ValueFormatter;

public interface ValueFormatterRegistry {

    <T> ValueFormatter<T> get(Column column);

}
