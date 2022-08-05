package com.foros.reporting.rowsource.olap.saiku;

import com.foros.reporting.meta.ColumnType;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.reporting.meta.olap.OlapColumnType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class SaikuCellSetValueReaderRegistry {

    private static final SaikuCellSetValueReader<BigDecimal> DEFAULT_METRIC_READER
            = new SimpleSaikuCellSetValueReader<>(new SimpleSaikuCellSetValueReader.DefaultMetricColumnValueFetcher(), new SimpleSaikuCellSetValueReader.BigDecimalValueParser());

    private static final SaikuCellSetValueReader<String> DEFAULT_OUTPUT_READER
            = new SimpleSaikuCellSetValueReader<>(new SimpleSaikuCellSetValueReader.DefaultOutputColumnValueFetcher(), new SimpleSaikuCellSetValueReader.NullValueParser());

    public static final SaikuCellSetValueReaderRegistry DEFAULT = new SaikuCellSetValueReaderRegistry()
            .forOlapColumnType(OlapColumnType.METRIC, DEFAULT_METRIC_READER)
            .type(ColumnTypes.number(),
                    new SimpleSaikuCellSetValueReader<>(new SimpleSaikuCellSetValueReader.DefaultMetricColumnValueFetcher(), new SimpleSaikuCellSetValueReader.LongValueParser()))
            .type(ColumnTypes.currency(),
                    new SimpleSaikuCellSetValueReader<>(new SimpleSaikuCellSetValueReader.DefaultMetricColumnValueFetcher(), new SimpleSaikuCellSetValueReader.BigDecimalValueParser()))
            .forOlapColumnType(OlapColumnType.OUTPUT, DEFAULT_OUTPUT_READER)
            .type(ColumnTypes.id(),
                    new SimpleSaikuCellSetValueReader<>(new SimpleSaikuCellSetValueReader.DefaultOutputColumnValueFetcher(), new SimpleSaikuCellSetValueReader.IdValueParser()))
            .type(ColumnTypes.date(),
                    new SimpleSaikuCellSetValueReader<>(new SimpleSaikuCellSetValueReader.DefaultOutputColumnValueFetcher(), new SimpleSaikuCellSetValueReader.DateValueParser()))
            .type(ColumnTypes.month(),
                    new SimpleSaikuCellSetValueReader<>(new SimpleSaikuCellSetValueReader.DefaultOutputColumnValueFetcher(), new SimpleSaikuCellSetValueReader.MonthValueParser()))
            .type(ColumnTypes.number(),
                    new SimpleSaikuCellSetValueReader<>(new SimpleSaikuCellSetValueReader.DefaultOutputColumnValueFetcher(), new SimpleSaikuCellSetValueReader.LongValueParser()))
            .type(ColumnTypes.currency(),
                    new SimpleSaikuCellSetValueReader<>(new SimpleSaikuCellSetValueReader.DefaultOutputColumnValueFetcher(), new SimpleSaikuCellSetValueReader.BigDecimalValueParser()))
            .type(ColumnTypes.status(),
                    new SimpleSaikuCellSetValueReader<>(new SimpleSaikuCellSetValueReader.DefaultOutputColumnValueFetcher(), new SimpleSaikuCellSetValueReader.StatusValueParser()))
            .build();

    private SaikuCellSetValueReaderRegistry parent;
    private Map<OlapColumnType, SaikuCellSetValueReader<?>> defaultReaders = new HashMap<>();
    private Map<OlapColumnType, Map<ColumnType, SaikuCellSetValueReader<?>>> readers = new HashMap<>();

    public SaikuCellSetValueReaderRegistry(SaikuCellSetValueReaderRegistry parent) {
        this.parent = parent;
    }

    public SaikuCellSetValueReaderRegistry() {
    }

    public SaikuCellSetValueReadersBuilder forOlapColumnType(OlapColumnType olapColumnType, SaikuCellSetValueReader<?> defaultReader) {
        return new SaikuCellSetValueReadersBuilder(olapColumnType, defaultReader);
    }

    public SaikuCellSetValueReader<?> get(OlapColumn column) {
        SaikuCellSetValueReader<?> reader = getInternal(column);

        if (reader != null) {
            return reader;
        }

        if (parent != null) {
            reader = parent.getInternal(column);
        }

        if (reader != null) {
            return reader;
        }

        reader = getDefaultInternal(column);

        if (reader != null) {
            return reader;
        }

        if (parent != null) {
            reader = parent.getDefaultInternal(column);
        }

        if (reader != null) {
            return reader;
        }

        throw new IllegalStateException("Reader for " + column.getNameKey() + " not found.");
    }

    private SaikuCellSetValueReader<?> getDefaultInternal(OlapColumn column) {
        return defaultReaders.get(column.getOlapColumnType());
    }

    private SaikuCellSetValueReader<?> getInternal(OlapColumn column) {
        Map<ColumnType, SaikuCellSetValueReader<?>> typeReaders = readers.get(column.getOlapColumnType());

        if (typeReaders == null) {
            return null;
        }

        return typeReaders.get(column.getType());
    }

    public class SaikuCellSetValueReadersBuilder {

        private final HashMap<ColumnType, SaikuCellSetValueReader<?>> typeReaders;

        public SaikuCellSetValueReadersBuilder(OlapColumnType olapColumnType, SaikuCellSetValueReader<?> defaultReader) {
            this.typeReaders = new HashMap<>();
            defaultReaders.put(olapColumnType, defaultReader);
            readers.put(olapColumnType, typeReaders);
        }

        public <T> SaikuCellSetValueReadersBuilder type(ColumnType type, SaikuCellSetValueReader<T> reader) {
            typeReaders.put(type, reader);
            return this;
        }

        public SaikuCellSetValueReadersBuilder forOlapColumnType(OlapColumnType olapColumnType, SaikuCellSetValueReader<?> defaultReader) {
            return new SaikuCellSetValueReadersBuilder(olapColumnType, defaultReader);
        }

        public SaikuCellSetValueReaderRegistry build() {
            return SaikuCellSetValueReaderRegistry.this;
        }
    }


}
