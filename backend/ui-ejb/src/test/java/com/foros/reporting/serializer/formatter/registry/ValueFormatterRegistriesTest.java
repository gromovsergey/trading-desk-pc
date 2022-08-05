package com.foros.reporting.serializer.formatter.registry;

import group.Report;
import group.Unit;

import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.ColumnType;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.serializer.formatter.HeaderFormatter;
import com.foros.reporting.serializer.formatter.NumberValueFormatter;
import com.foros.reporting.serializer.formatter.ValueFormatter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Unit.class, Report.class })
public class ValueFormatterRegistriesTest extends Assert {

    @Test
    public void testDefaults() {
        ValueFormatterRegistryImpl custom = ValueFormatterRegistries.registry();

        ValueFormatterRegistryHolder registry = ValueFormatterRegistries
                .defaultHolder()
                .registry(RowTypes.data(), custom);

        TestColumn column = new TestColumn(ColumnTypes.number());

        ValueFormatter<?> numberFormatter = registry.registry(RowTypes.data()).get(column);
        assertTrue(numberFormatter instanceof NumberValueFormatter);

        ValueFormatter<Column> headerFormatter = registry.registry(RowTypes.header()).get(column);
        assertTrue(headerFormatter instanceof HeaderFormatter);
    }

    private static class TestColumn implements Column {
        private ColumnType number;
        private TestColumn(ColumnType number) {
            this.number = number;
        }

        @Override
        public ColumnType getType() {
            return number;
        }

        @Override
        public String getNameKey() {
            return "test";
        }
    }
}
