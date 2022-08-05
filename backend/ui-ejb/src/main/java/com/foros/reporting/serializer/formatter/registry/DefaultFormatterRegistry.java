package com.foros.reporting.serializer.formatter.registry;

import com.foros.reporting.RowType;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.serializer.formatter.CountryValueFormatter;
import com.foros.reporting.serializer.formatter.DateTimeValueFormatter;
import com.foros.reporting.serializer.formatter.DayOfWeekValueFormatter;
import com.foros.reporting.serializer.formatter.DefaultValueFormatter;
import com.foros.reporting.serializer.formatter.HeaderFormatter;
import com.foros.reporting.serializer.formatter.IdValueFormatter;
import com.foros.reporting.serializer.formatter.KeywordTypeValueFormatter;
import com.foros.reporting.serializer.formatter.LocalDateTimeValueFormatter;
import com.foros.reporting.serializer.formatter.LocalDateValueFormatter;
import com.foros.reporting.serializer.formatter.MonthValueFormatter;
import com.foros.reporting.serializer.formatter.NullValueFormatterWrapper;
import com.foros.reporting.serializer.formatter.NumberValueFormatter;
import com.foros.reporting.serializer.formatter.PercentValueFormatter;
import com.foros.reporting.serializer.formatter.QuarterValueFormatter;
import com.foros.reporting.serializer.formatter.StatusValueFormatter;
import com.foros.reporting.serializer.formatter.StringValueFormatter;
import com.foros.reporting.serializer.formatter.TimeSpanValueFormatter;
import com.foros.reporting.serializer.formatter.WeekValueFormatter;
import com.foros.reporting.serializer.formatter.YearValueFormatter;
import com.foros.util.CollectionUtils;

import java.util.Map;

public class DefaultFormatterRegistry {

    public static final ValueFormatterRegistry DEFAULT_REGISTRY = ValueFormatterRegistries.registry()
            .type(ColumnTypes.string(), new StringValueFormatter())
            .type(ColumnTypes.number(), new NumberValueFormatter())
            .type(ColumnTypes.percents(), new PercentValueFormatter())
            .type(ColumnTypes.currency(), new NumberValueFormatter())
            .type(ColumnTypes.date(), new LocalDateValueFormatter())
            .type(ColumnTypes.dateTime(), new LocalDateTimeValueFormatter())
            .type(ColumnTypes.dayOfWeek(), new DayOfWeekValueFormatter())
            .type(ColumnTypes.week(), new WeekValueFormatter())
            .type(ColumnTypes.month(), new MonthValueFormatter())
            .type(ColumnTypes.quarter(), new QuarterValueFormatter())
            .type(ColumnTypes.year(), new YearValueFormatter())
            .type(ColumnTypes.country(), new CountryValueFormatter())
            .type(ColumnTypes.status(), new StatusValueFormatter())
            .type(ColumnTypes.keywordType(), new KeywordTypeValueFormatter())
            .defaultFormatter(new DefaultValueFormatter());

    public static final ValueFormatterRegistry DEFAULT_HEADER_REGISTRY = ValueFormatterRegistries.registry()
            .defaultFormatter(new HeaderFormatter());


    private static final Map<RowType, ValueFormatterRegistry> defaultRegistries = CollectionUtils.<RowType, ValueFormatterRegistry>
             map(RowTypes.data(), DEFAULT_REGISTRY)
            .map(RowTypes.header(), DEFAULT_HEADER_REGISTRY)
            .build();

    private DefaultFormatterRegistry() {
    }

    public static ValueFormatterRegistry create(RowType rowType) {
        return defaultRegistries.get(rowType);
    }

    public static ValueFormatterRegistryHolder createHolder() {
        ValueFormatterRegistryHolder holder = new ValueFormatterRegistryHolder();
        for (Map.Entry<RowType, ValueFormatterRegistry> entry : defaultRegistries.entrySet()) {
            holder.registry(entry.getKey()).registry(entry.getValue());
        }
        return holder;
    }

    public static final ValueFormatterRegistry DEFAULT_BULK_REGISTRY = ValueFormatterRegistries.registry()
            .type(ColumnTypes.id(), new IdValueFormatter())
            .type(ColumnTypes.string(), new StringValueFormatter())
            .type(ColumnTypes.number(), new NullValueFormatterWrapper(new NumberValueFormatter()))
            .type(ColumnTypes.currency(), new NullValueFormatterWrapper(new NumberValueFormatter(2)))
            .type(ColumnTypes.percents(), new NullValueFormatterWrapper(new PercentValueFormatter()))
            .type(ColumnTypes.dateTime(), new DateTimeValueFormatter())
            .type(ColumnTypes.timeSpan(), new TimeSpanValueFormatter())
            .type(ColumnTypes.status(), new BulkStatusValueFormatter())
            .type(ColumnTypes.qaStatus(), new BulkQaStatusValueFormatter())
            .defaultFormatter(new DefaultValueFormatter());
}
