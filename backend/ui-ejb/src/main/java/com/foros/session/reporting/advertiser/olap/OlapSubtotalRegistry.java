package com.foros.session.reporting.advertiser.olap;

import com.foros.reporting.meta.AggregatableColumn;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.reporting.serializer.formatter.HighlightValueFormatterWrapper;
import com.foros.reporting.serializer.formatter.NullValueFormatter;
import com.foros.reporting.serializer.formatter.TotalValueFormatterWrapper;
import com.foros.reporting.serializer.formatter.ValueFormatter;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OlapSubtotalRegistry implements ValueFormatterRegistry {
    public static final String CSS_CLASS = "subtotal";

    private ValueFormatterRegistry target;
    private OlapColumn subtotalColumn;
    private OlapColumn subtotalTextColumn;
    private Set<? extends AggregatableColumn> highlightColumns;
    private Map<Column, ValueFormatter> formatters = new HashMap<Column, ValueFormatter>();

    public OlapSubtotalRegistry(ValueFormatterRegistry target, OlapColumn subtotalColumn, OlapColumn subtotalTextColumn, List<? extends AggregatableColumn> highlightColumns) {
        this.target = target;
        this.subtotalColumn = subtotalColumn;
        this.subtotalTextColumn = subtotalTextColumn;
        this.highlightColumns = new HashSet<AggregatableColumn>(highlightColumns);
        for (AggregatableColumn column : highlightColumns) {
            buildFormatter(column);
        }
        buildFormatter(subtotalTextColumn);
    }

    @Override
    public <T> ValueFormatter<T> get(Column column) {
        ValueFormatter formatter = formatters.get(column);
        if (formatter != null) {
            return formatter;
        }

        return target.get(column);
    }

    private <T> ValueFormatter<T> buildFormatter(AggregatableColumn column) {
        ValueFormatter result;

        if (subtotalTextColumn.equals(column)) {
            result = target.get(subtotalColumn);
            result = new TotalValueFormatterWrapper(result);
        } else if (!column.hasAggregateFunction()) {
            result = NullValueFormatter.INSTANCE;
        } else {
            result = target.get(column);
        }

        if (highlightColumns.contains(column)) {
            result = new HighlightValueFormatterWrapper(result, target.get(column), CSS_CLASS);
        }

        formatters.put(column, result);
        //noinspection unchecked
        return result;
    }
}
