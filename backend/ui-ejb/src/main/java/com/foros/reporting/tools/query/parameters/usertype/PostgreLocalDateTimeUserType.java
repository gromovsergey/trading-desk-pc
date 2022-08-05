package com.foros.reporting.tools.query.parameters.usertype;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class PostgreLocalDateTimeUserType implements UserType {
    public static final PostgreLocalDateTimeUserType INSTANCE = new PostgreLocalDateTimeUserType();

    private static DateTimeFormatter FORMATTER = ISODateTimeFormat.dateTime();

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.OTHER};
    }

    @Override
    public Class returnedClass() {
        return LocalDateTime.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return ObjectUtils.equals(x, y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        Timestamp dateTime = rs.getTimestamp(names[0]);
        return dateTime == null ? null : new LocalDateTime(dateTime);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        LocalDateTime localDateTime = (LocalDateTime) value;
        if (localDateTime != null) {
            st.setObject(index, PostgreLocalDateTimeUserType.FORMATTER.print(localDateTime), sqlTypes()[0]);
        } else {
            st.setNull(index, sqlTypes()[0]);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        LocalDateTime localDateTime = (LocalDateTime) value;
        if (localDateTime == null) {
            return null;
        } else {
            return new LocalDateTime(
                    localDateTime.getYear(), localDateTime.getMonthOfYear(),
                    localDateTime.getDayOfMonth(), localDateTime.getHourOfDay(),
                    localDateTime.getMinuteOfHour(), localDateTime.getSecondOfMinute());
        }
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) deepCopy(value);
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return deepCopy(cached);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }
}
