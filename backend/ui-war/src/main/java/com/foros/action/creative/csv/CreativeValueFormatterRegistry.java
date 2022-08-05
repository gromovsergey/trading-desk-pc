package com.foros.action.creative.csv;

import com.foros.reporting.meta.Column;
import com.foros.reporting.serializer.formatter.ExcelCellAccessor;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.HtmlCellAccessor;
import com.foros.reporting.serializer.formatter.ValueFormatter;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;

public class CreativeValueFormatterRegistry implements ValueFormatterRegistry {
    private static final ValueFormatterRegistry commonRegistry = ValueFormatterRegistries.bulkDefaultAnd(null);

    private CreativeValueFormatter creativeValueFormatter = new CreativeValueFormatter();
    private Column currentColumn;

    @Override
    public ValueFormatter<Object> get(Column column) {
        currentColumn = column;
        return creativeValueFormatter;
    }

    private class CreativeValueFormatter implements ValueFormatter<Object> {

        public String formatText(Object value, FormatterContext context) {
            return getValueFormatter(context).formatText(value, context);
        }

        public String formatCsv(Object value, FormatterContext context) {
            return getValueFormatter(context).formatCsv(value, context);
        }

        public void formatHtml(HtmlCellAccessor cellAccessor, Object value, FormatterContext context) {
            getValueFormatter(context).formatHtml(cellAccessor, value, context);
        }

        public void formatExcel(ExcelCellAccessor cellAccessor, Object value, FormatterContext context) {
            getValueFormatter(context).formatExcel(cellAccessor, value, context);
        }

        private ValueFormatter getValueFormatter(FormatterContext context) {
            return commonRegistry.get(((CreativeCsvRow)context.getRow()).getColumnWithRightType(currentColumn));
        }
    }
}
