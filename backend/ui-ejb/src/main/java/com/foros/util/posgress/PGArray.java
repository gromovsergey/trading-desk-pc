package com.foros.util.posgress;

import com.foros.util.CollectionUtils;
import com.foros.util.mapper.Converter;
import com.foros.util.mapper.Mapper;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PGArray implements Iterable<PGRow>, Iterator<PGRow>, AutoCloseable {

    private final ResultSet set;

    private PGArray(ResultSet resultSet) {
        set = resultSet;
    }

    @Override
    public boolean hasNext() {
        try {
            return set.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PGRow next() {
        try {
            return PGRow.read(set.getString(2));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove() {
    }

    @Override
    public Iterator<PGRow> iterator() {
        return this;
    }

    @Override
    public void close() throws SQLException {
        set.close();
    }

    public static <T> List<T> read(Array array, PGRow.Converter<T> converter) throws SQLException {
        try (PGArray reader = new PGArray(array.getResultSet())) {
            return CollectionUtils.convert(reader, converter);
        }
    }

    public static <T> List<T> read(Array array, Converter<PGRow, T> converter) throws SQLException {
        try (PGArray reader = new PGArray(array.getResultSet())) {
            return CollectionUtils.convert(reader, converter);
        }
    }

    public static <K, V> Map<K, V> read(Array array, Mapper<PGRow, K, V> mapper) throws SQLException {
        try (PGArray reader = new PGArray(array.getResultSet())) {
            return CollectionUtils.map(mapper, reader);
        }
    }
}
