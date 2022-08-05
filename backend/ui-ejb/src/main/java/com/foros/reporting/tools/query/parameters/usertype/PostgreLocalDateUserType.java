package com.foros.reporting.tools.query.parameters.usertype;

import java.io.Serializable;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class PostgreLocalDateUserType implements UserType {

    public static final PostgreLocalDateUserType INSTANCE = new PostgreLocalDateUserType();
    private static final int SQL_TYPE = Types.OTHER;

    public static DateTimeFormatter FORMATTER = ISODateTimeFormat.date();

    @Override
    public int[] sqlTypes() {
        return new int[]{SQL_TYPE};
    }

    @Override
    public Class returnedClass() {
        return LocalDate.class;
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
        Date date = rs.getDate(names[0]);
        return date == null ? null : new LocalDate(date.getTime());
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        LocalDate localDate = (LocalDate) value;
        if (localDate != null) {
            st.setObject(index, PostgreLocalDateUserType.FORMATTER.print(localDate), sqlTypes()[0]);
        } else {
            st.setNull(index, SQL_TYPE);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        LocalDate localdate = (LocalDate) value;
        if (localdate == null) {
            return null;
        } else {
            return new LocalDate(localdate.getYear(), localdate.getMonthOfYear(), localdate.getDayOfMonth());
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
