package com.foros.util.posgress;

import com.foros.util.SQLUtil;
import com.foros.util.csv.CsvReader;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.postgresql.core.BaseConnection;
import org.postgresql.core.Oid;
import org.postgresql.jdbc2.AbstractJdbc2Array;
import org.postgresql.jdbc4.Jdbc4Array;

public class PGRow {
    private static final Pattern REMOVE_BRACKETS = Pattern.compile("^\\(|\\)$");

    private static final Pattern REMOVE_DOUBLE_SLASH = Pattern.compile("\\\\\\\\");

    private String[] values;

    public PGRow(String[] values) {
        this.values = values;
    }

    public String getString(int i) {
        return values[i];
    }

    public Long getLong(int i) {
        String value = values[i];
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Long.valueOf(value);
    }

    public Integer getInteger(int i) {
        String value = values[i];
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Integer.valueOf(value);
    }

    public Character getCharacter(int i) {
        String value = values[i];
        if (value == null || value.isEmpty()) {
            return null;
        }

        if (value.length() > 1) {
            throw new IllegalStateException(value);
        }
        return value.charAt(0);
    }

    public DateTime getDateTime(int i) {
        String value = values[i];
        if (value == null || value.isEmpty()) {
            return null;
        }
        return SQLUtil.getDateTimeParser().parseDateTime(value);
    }

    public LocalDateTime getLocalDateTime(int i) {
        String value = values[i];
        if (value == null || value.isEmpty()) {
            return null;
        }
        return SQLUtil.getDateTimeParser().parseLocalDateTime(value);
    }

    public Timestamp getTimestamp(int i) {
        DateTime dateTime = getDateTime(i);
        return dateTime != null ? new Timestamp(dateTime.getMillis()) : null;
    }

    public Array getSubArray(int i, Array array) throws SQLException {
        String value = values[i];
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            Field connectionField = AbstractJdbc2Array.class.getDeclaredField("connection");
            connectionField.setAccessible(true);
            BaseConnection connection = (BaseConnection) connectionField.get(array);
            return new Jdbc4Array(connection, Oid.UNSPECIFIED, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static PGRow read(String row) throws SQLException {
        try {
            CsvReader csvReader = new CsvReader(new StringReader(REMOVE_BRACKETS.matcher(row).replaceAll("")));
            csvReader.readRecord();
            String[] values = csvReader.getValues();
            for (int i = 0; i < values.length; i++) {
                values[i] = REMOVE_DOUBLE_SLASH.matcher(values[i]).replaceAll("\\\\");
            }
            return new PGRow(values);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T read(String value, Converter<T> converter) throws SQLException {
        return converter.item(PGRow.read(value));
    }

    public interface Converter<T> extends com.foros.util.mapper.Converter<PGRow, T> {
        // SQLException
    }

    @Override
    public String toString() {
        // todo format it as postgres do it
        return Arrays.toString(values);
    }
}
