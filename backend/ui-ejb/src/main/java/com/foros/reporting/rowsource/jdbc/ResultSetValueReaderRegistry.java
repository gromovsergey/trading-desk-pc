package com.foros.reporting.rowsource.jdbc;

import com.foros.model.Status;
import com.foros.persistence.hibernate.type.GenericEnumType;
import com.foros.reporting.meta.ColumnType;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ResultSetNameResolver;
import com.foros.reporting.tools.query.parameters.usertype.PostgreLocalDateTimeUserType;
import com.foros.reporting.tools.query.parameters.usertype.PostgreLocalDateUserType;
import com.foros.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.hibernate.type.EnumType;
import org.hibernate.usertype.UserType;
import org.joda.time.LocalDate;


public class ResultSetValueReaderRegistry implements ResultSetNameResolver {

    private static final ResultSetValueReader<?> DEFAULT_VALUE_READER = new DefaultResultSetValueReader();


    private static final Map<ColumnType, ResultSetValueReader> POSTGRE_READERS = CollectionUtils.<ColumnType, ResultSetValueReader>
            map(ColumnTypes.id(), new LongResultSetValueReader())
            .map(ColumnTypes.date(), new PostgreDateResultSetValueReader())
            .map(ColumnTypes.dateTime(), new UserTypeResultSetValueReader(new PostgreLocalDateTimeUserType()))
            .map(ColumnTypes.dayOfWeek(), new LongResultSetValueReader())
            .map(ColumnTypes.week(), new PostgreDateResultSetValueReader())
            .map(ColumnTypes.month(), new PostgreDateResultSetValueReader())
            .map(ColumnTypes.quarter(), new PostgreDateResultSetValueReader())
            .map(ColumnTypes.year(), new PostgreDateResultSetValueReader())
            .map(ColumnTypes.status(), new UserTypeResultSetValueReader(new GenericEnumType(Status.class, "getLetter", "valueOf")))
            .map(ColumnTypes.bool(), new BooleanResultSetValueReader())
            .build();

    private static final Map<ColumnType, ResultSetValueReader> IMPALA_READERS = CollectionUtils.<ColumnType, ResultSetValueReader>
            map(ColumnTypes.id(), new LongResultSetValueReader())
            .map(ColumnTypes.bool(), new BooleanResultSetValueReader())
            .build();

    private static final ResultSetValueReaderRegistry POSTGRE_DEFAULT_REGISTRY = new ResultSetValueReaderRegistry(POSTGRE_READERS);

    private static final ResultSetValueReaderRegistry IMPALA_DEFAULT_REGISTRY = new ResultSetValueReaderRegistry(IMPALA_READERS);

    private Map<ColumnType, ResultSetValueReader> readers;
    private ResultSetValueReaderRegistry parent;
    private Map<String, String> aliases = new HashMap<String, String>();

    ResultSetValueReaderRegistry(ResultSetValueReaderRegistry parent) {
        this.parent = parent;
        this.readers = new HashMap<ColumnType, ResultSetValueReader>();
    }

    ResultSetValueReaderRegistry(Map<ColumnType, ResultSetValueReader> readers) {
        this.readers = readers;
    }

    public ResultSetValueReader get(DbColumn column) {
        ResultSetValueReader reader = getInternal(column);
        String alias = aliases.get(column.getResultSetName());
        if (alias != null) {
            reader = new AliasValueReader(reader, alias);
        }
        return reader;
    }

    private ResultSetValueReader getInternal(DbColumn column) {
        ColumnType type = column.getType();
        ResultSetValueReader reader = readers.get(type);
        if (reader != null) {
            return reader;
        }

        if (parent != null) {
            return parent.get(column);
        }

        return DEFAULT_VALUE_READER;
    }

    public static ResultSetValueReaderRegistry getPostgreDefault() {
        return new ResultSetValueReaderRegistry(POSTGRE_DEFAULT_REGISTRY);
    }

    public static ResultSetValueReaderRegistry getImpalaDefault() {
        return new ResultSetValueReaderRegistry(IMPALA_DEFAULT_REGISTRY);
    }

    public ResultSetValueReaderRegistry type(ColumnType type, UserType userType) {
        readers.put(type, new UserTypeResultSetValueReader(userType));
        return this;
    }

    public void setAlias(DbColumn column, String alias) {
        aliases.put(column.getResultSetName(), alias);
    }

    @Override
    public String getResultSetName(DbColumn column) {
        String alias = aliases.get(column.getResultSetName());
        if (alias == null) {
            if (parent != null) {
                alias = parent.getResultSetName(column);
            } else {
                alias = column.getResultSetName();
            }
        }
        return alias;
    }

    public static class UserTypeResultSetValueReader<T> implements ResultSetValueReader<T> {
        private UserType type;

        public UserTypeResultSetValueReader(UserType type) {
            this.type = type;
        }

        @Override
        public T readValue(ResultSet resultSet, String name) throws SQLException {
            //noinspection unchecked
            return (T) type.nullSafeGet(resultSet, new String[] {name}, null);
        }
    }

    public static class DefaultResultSetValueReader implements ResultSetValueReader<Object> {
        @Override
        public Object readValue(ResultSet resultSet, String name) throws SQLException {
            return resultSet.getObject(name);
        }
    }

    private static class LongResultSetValueReader implements ResultSetValueReader<Long> {
        @Override
        public Long readValue(ResultSet resultSet, String name) throws SQLException {
            if (resultSet.getObject(name) == null) {
                return null;
            }
            return resultSet.getLong(name);
        }
    }

    private static class BooleanResultSetValueReader implements ResultSetValueReader<Boolean> {
        @Override
        public Boolean readValue(ResultSet resultSet, String name) throws SQLException {
            if (resultSet.getObject(name) == null) {
                return null;
            }
            return resultSet.getBoolean(name);
        }
    }

    private static class PostgreDateResultSetValueReader implements ResultSetValueReader<LocalDate> {

        private ResultSetValueReader<LocalDate> reader =
                new UserTypeResultSetValueReader<LocalDate>(new PostgreLocalDateUserType());

        @Override
        public LocalDate readValue(ResultSet resultSet, String name) throws SQLException {
            return reader.readValue(resultSet, name);
        }
    }

    public static UserType enumOrdinalType(Class<? extends Enum> enumClass) {
        EnumType type = new EnumType();
        Properties parameters = new Properties();
        parameters.setProperty(EnumType.ENUM, enumClass.getName());
        type.setParameterValues(parameters);
        return type;
    }

    private static class AliasValueReader implements ResultSetValueReader {
        private ResultSetValueReader target;
        private String alias;

        public AliasValueReader(ResultSetValueReader target, String alias) {
            this.target = target;
            this.alias = alias;
        }

        @Override
        public Object readValue(ResultSet resultSet, String name) throws SQLException {
            try {
                return target.readValue(resultSet, alias);
            } catch (Exception e) {
                throw new SQLException("Can't read " + alias, e);
            }
        }
    }
}
