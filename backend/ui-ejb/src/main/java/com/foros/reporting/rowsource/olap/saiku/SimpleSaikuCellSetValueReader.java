package com.foros.reporting.rowsource.olap.saiku;

import com.foros.model.Status;
import com.foros.reporting.meta.olap.OlapColumn;
import com.phorm.oix.saiku.cellset.SaikuCellsRow;
import java.math.BigDecimal;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class SimpleSaikuCellSetValueReader<T> implements SaikuCellSetValueReader<T> {

    private ValueFetcher valueFetcher;
    private ValueParser<T> parser;

    public SimpleSaikuCellSetValueReader(ValueFetcher valueFetcher, ValueParser<T> parser) {
        this.valueFetcher = valueFetcher;
        this.parser = parser;
    }

    @Override
    public T read(SaikuCellsRow row, OlapColumn column, Object context) {
        String value = valueFetcher.fetch(row, column, context);
        return parser.parse(value);
    }

    public static interface ValueFetcher {

        String fetch(SaikuCellsRow row, OlapColumn column, Object context);

    }

    public static interface ValueParser<T> {

        T parse(String value);

    }

    public static class IdValueParser implements ValueParser<Long> {

        private ValueParser<Long> parser = new LongValueParser();

        @Override
        public Long parse(String value) {
            Long number = parser.parse(value);

            if (number == null) {
                return null;
            }

            return number == 0 ? null : number;
        }
    }

    public static class LongValueParser implements ValueParser<Long> {

        private ValueParser<BigDecimal> parser = new BigDecimalValueParser();

        @Override
        public Long parse(String value) {
            if (value == null) {
                return null;
            }

            BigDecimal bigDecimal = parser.parse(value);

            if (bigDecimal == null) {
                return null;
            }

            return bigDecimal.longValue();
        }
    }

    public static class BigDecimalValueParser implements ValueParser<BigDecimal> {
        @Override
        public BigDecimal parse(String value) {
            if (value == null) {
                return null;
            }

            return new BigDecimal(value);
        }
    }

    public static class StatusValueParser implements ValueParser<Status> {
        @Override
        public Status parse(String value) {
            if (value == null) {
                return null;
            }

            return Status.valueOf(value.charAt(0));
        }
    }

    public static class MonthValueParser implements ValueParser<LocalDate> {

        private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM");

        @Override
        public LocalDate parse(String value) {
            if (value == null) {
                return null;
            }

            return dateTimeFormatter.parseDateTime(value).toLocalDate();
        }
    }

    public static class DateValueParser implements ValueParser<LocalDate> {

        private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

        @Override
        public LocalDate parse(String value) {
            if (value == null) {
                return null;
            }

            return dateTimeFormatter.parseDateTime(value).toLocalDate();
        }
    }

    public static class NullValueParser implements ValueParser<String> {
        @Override
        public String parse(String value) {
            return value;
        }
    }

    public static class DefaultMetricColumnValueFetcher implements ValueFetcher {

        @Override
        public String fetch(SaikuCellsRow row, OlapColumn column, Object context) {
            return row.findDataValue(column.getMember(context));
        }
    }

    public static class DefaultOutputColumnValueFetcher implements ValueFetcher {

        @Override
        public String fetch(SaikuCellsRow row, OlapColumn column, Object context) {
            String name = row.findMemberByHierarchy(column.getMember(context));

            if (name.equalsIgnoreCase("#null")) {
                return null;
            }

            return name;
        }

    }
}
